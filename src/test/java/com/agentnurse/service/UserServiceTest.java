package com.agentnurse.service;

import com.agentnurse.model.dto.UserProfileRequest;
import com.agentnurse.model.dto.UserResponse;
import com.agentnurse.model.entity.User;
import com.agentnurse.model.enums.UserRole;
import com.agentnurse.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.PATIENT)
                .realName("Test User")
                .phone("1234567890")
                .gender("M")
                .birthDate("1990-01-01")
                .address("123 Test St")
                .avatarUrl("https://example.com/avatar.jpg")
                .enabled(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
    }

    @Test
    void getCurrentUser_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getCurrentUser_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userService.getCurrentUser()
        );

        assertTrue(exception.getMessage().contains("testuser"));
    }

    @Test
    void getCurrentUserProfile_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserResponse response = userService.getCurrentUserProfile();

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(UserRole.PATIENT, response.getRole());
        assertEquals("Test User", response.getRealName());
        assertEquals("1234567890", response.getPhone());
        assertEquals("M", response.getGender());
        assertEquals("1990-01-01", response.getBirthDate());
        assertEquals("123 Test St", response.getAddress());
        assertEquals("https://example.com/avatar.jpg", response.getAvatarUrl());
        assertTrue(response.getEnabled());
        assertFalse(response.getEmailVerified());
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        UserResponse response = userService.getUserById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.getUserById(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    void updateProfile_UpdateAllFields_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileRequest request = new UserProfileRequest();
        request.setEmail("newemail@example.com");
        request.setRealName("Updated Name");
        request.setPhone("9876543210");
        request.setGender("F");
        request.setBirthDate("1995-05-05");
        request.setAddress("456 New St");
        request.setAvatarUrl("https://example.com/new-avatar.jpg");

        // Act
        UserResponse response = userService.updateProfile(request);

        // Assert
        assertNotNull(response);
        assertEquals("newemail@example.com", response.getEmail());
        assertFalse(response.getEmailVerified()); // Should be reset when email changes
        assertEquals("Updated Name", response.getRealName());
        assertEquals("9876543210", response.getPhone());
        assertEquals("F", response.getGender());
        assertEquals("1995-05-05", response.getBirthDate());
        assertEquals("456 New St", response.getAddress());
        assertEquals("https://example.com/new-avatar.jpg", response.getAvatarUrl());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateProfile_EmailAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        UserProfileRequest request = new UserProfileRequest();
        request.setEmail("existing@example.com");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateProfile(request)
        );

        assertTrue(exception.getMessage().contains("邮箱已被其他用户使用"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateProfile_SameEmail_DoesNotResetVerification() {
        // Arrange
        testUser.setEmailVerified(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileRequest request = new UserProfileRequest();
        request.setEmail("test@example.com"); // Same email
        request.setRealName("Updated Name");

        // Act
        UserResponse response = userService.updateProfile(request);

        // Assert
        assertTrue(response.getEmailVerified()); // Should remain verified
        assertEquals("Updated Name", response.getRealName());
    }

    @Test
    void updateProfile_OnlyRealName_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileRequest request = new UserProfileRequest();
        request.setRealName("Only Name Changed");

        // Act
        UserResponse response = userService.updateProfile(request);

        // Assert
        assertEquals("Only Name Changed", response.getRealName());
        assertEquals("test@example.com", response.getEmail()); // Unchanged
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateProfile_NullFields_DoesNotUpdate() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileRequest request = new UserProfileRequest();
        // All fields null

        // Act
        UserResponse response = userService.updateProfile(request);

        // Assert
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getRealName());
        assertEquals("1234567890", response.getPhone());
        // Original values preserved
    }

    @Test
    void deleteAccount_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        userService.deleteAccount();

        // Assert
        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteAccount_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.deleteAccount());
        verify(userRepository, never()).delete(any());
    }
}