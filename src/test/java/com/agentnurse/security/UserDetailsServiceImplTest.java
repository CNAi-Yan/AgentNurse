package com.agentnurse.security;

import com.agentnurse.model.entity.User;
import com.agentnurse.model.enums.UserRole;
import com.agentnurse.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.PATIENT)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void loadUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertFalse(userDetails.isAccountLocked());
        assertFalse(userDetails.isAccountExpired());
        assertFalse(userDetails.isCredentialsExpired());
        
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername("nonexistent")
        );

        assertTrue(exception.getMessage().contains("nonexistent"));
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void loadUserByUsername_DisabledUser_AccountLocked() {
        // Arrange
        testUser.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertTrue(userDetails.isAccountLocked());
        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_WithPatientRole() {
        // Arrange
        testUser.setRole(UserRole.PATIENT);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertEquals(1, userDetails.getAuthorities().size());
        GrantedAuthority authority = userDetails.getAuthorities().iterator().next();
        assertEquals("ROLE_PATIENT", authority.getAuthority());
    }

    @Test
    void loadUserByUsername_WithAdminRole() {
        // Arrange
        testUser.setRole(UserRole.ADMIN);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertEquals(1, userDetails.getAuthorities().size());
        GrantedAuthority authority = userDetails.getAuthorities().iterator().next();
        assertEquals("ROLE_ADMIN", authority.getAuthority());
    }

    @Test
    void loadUserByUsername_WithCaregiverRole() {
        // Arrange
        testUser.setRole(UserRole.CAREGIVER);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        GrantedAuthority authority = userDetails.getAuthorities().iterator().next();
        assertEquals("ROLE_CAREGIVER", authority.getAuthority());
    }

    @Test
    void loadUserByUsername_WithFamilyRole() {
        // Arrange
        testUser.setRole(UserRole.FAMILY);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        GrantedAuthority authority = userDetails.getAuthorities().iterator().next();
        assertEquals("ROLE_FAMILY", authority.getAuthority());
    }

    @Test
    void loadUserByUsername_EnabledUser_NotLocked() {
        // Arrange
        testUser.setEnabled(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertFalse(userDetails.isAccountLocked());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_AccountNeverExpires() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertFalse(userDetails.isAccountExpired());
    }

    @Test
    void loadUserByUsername_CredentialsNeverExpire() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertFalse(userDetails.isCredentialsExpired());
    }
}