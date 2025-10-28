package com.agentnurse.service;

import com.agentnurse.model.dto.AuthResponse;
import com.agentnurse.model.dto.LoginRequest;
import com.agentnurse.model.dto.RegisterRequest;
import com.agentnurse.model.entity.User;
import com.agentnurse.model.enums.UserRole;
import com.agentnurse.repository.UserRepository;
import com.agentnurse.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Test@123");
        registerRequest.setRole(UserRole.PATIENT);
        registerRequest.setRealName("Test User");
        registerRequest.setPhone("1234567890");

        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("Test@123");

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.PATIENT)
                .realName("Test User")
                .phone("1234567890")
                .enabled(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void register_Success() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtils.generateTokenFromUsername(anyString())).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUser());
        assertEquals("testuser", response.getUser().getUsername());
        assertEquals("test@example.com", response.getUser().getEmail());

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("Test@123");
        verify(userRepository).save(any(User.class));
        verify(jwtUtils).generateTokenFromUsername("testuser");
    }

    @Test
    void register_UsernameExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.register(registerRequest)
        );

        assertEquals("用户名已存在", exception.getMessage());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_EmailExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.register(registerRequest)
        );

        assertEquals("邮箱已被注册", exception.getMessage());
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_WithDifferentRoles() {
        // Test each role
        UserRole[] roles = {UserRole.PATIENT, UserRole.FAMILY, UserRole.CAREGIVER, UserRole.ADMIN};
        
        for (UserRole role : roles) {
            registerRequest.setRole(role);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            
            User userWithRole = User.builder()
                    .username("testuser")
                    .email("test@example.com")
                    .role(role)
                    .enabled(true)
                    .build();
            
            when(userRepository.save(any(User.class))).thenReturn(userWithRole);
            when(jwtUtils.generateTokenFromUsername(anyString())).thenReturn("jwt-token");

            AuthResponse response = authService.register(registerRequest);

            assertNotNull(response);
            assertEquals(role, response.getUser().getRole());
        }
    }

    @Test
    void login_Success_WithUsername() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateToken(any(Authentication.class))).thenReturn("jwt-token");
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUser());
        assertEquals("testuser", response.getUser().getUsername());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateToken(authentication);
        verify(userRepository).findByUsernameOrEmail("testuser", "testuser");
        verify(userRepository).save(argThat(user -> user.getLastLoginAt() != null));
    }

    @Test
    void login_Success_WithEmail() {
        // Arrange
        loginRequest.setUsernameOrEmail("test@example.com");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateToken(any(Authentication.class))).thenReturn("jwt-token");
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        verify(userRepository).findByUsernameOrEmail("test@example.com", "test@example.com");
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateToken(any(Authentication.class))).thenReturn("jwt-token");
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.login(loginRequest)
        );

        assertEquals("用户不存在", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_UpdatesLastLoginTime() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateToken(any(Authentication.class))).thenReturn("jwt-token");
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime beforeLogin = LocalDateTime.now();

        // Act
        authService.login(loginRequest);

        // Assert
        verify(userRepository).save(argThat(user -> {
            LocalDateTime lastLogin = user.getLastLoginAt();
            return lastLogin != null && 
                   !lastLogin.isBefore(beforeLogin) && 
                   !lastLogin.isAfter(LocalDateTime.now().plusSeconds(1));
        }));
    }
}