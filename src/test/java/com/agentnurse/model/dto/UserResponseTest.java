package com.agentnurse.model.dto;

import com.agentnurse.model.entity.User;
import com.agentnurse.model.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    @Test
    void fromUser_ConvertsAllFields() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);
        
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password") // Should not be in response
                .role(UserRole.PATIENT)
                .realName("Test User")
                .phone("1234567890")
                .gender("M")
                .birthDate("1990-01-01")
                .address("123 Test St")
                .avatarUrl("https://example.com/avatar.jpg")
                .enabled(true)
                .emailVerified(true)
                .createdAt(now)
                .lastLoginAt(lastLogin)
                .build();

        // Act
        UserResponse response = UserResponse.fromUser(user);

        // Assert
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
        assertTrue(response.getEmailVerified());
        assertEquals(now, response.getCreatedAt());
        assertEquals(lastLogin, response.getLastLoginAt());
    }

    @Test
    void fromUser_WithNullOptionalFields() {
        // Arrange
        User user = User.builder()
                .id(2L)
                .username("minimaluser")
                .email("minimal@example.com")
                .password("password")
                .role(UserRole.FAMILY)
                .enabled(false)
                .emailVerified(false)
                .build();

        // Act
        UserResponse response = UserResponse.fromUser(user);

        // Assert
        assertEquals(2L, response.getId());
        assertEquals("minimaluser", response.getUsername());
        assertEquals("minimal@example.com", response.getEmail());
        assertEquals(UserRole.FAMILY, response.getRole());
        assertNull(response.getRealName());
        assertNull(response.getPhone());
        assertNull(response.getGender());
        assertNull(response.getBirthDate());
        assertNull(response.getAddress());
        assertNull(response.getAvatarUrl());
        assertFalse(response.getEnabled());
        assertFalse(response.getEmailVerified());
    }

    @Test
    void fromUser_WithDifferentRoles() {
        UserRole[] roles = {UserRole.PATIENT, UserRole.FAMILY, UserRole.CAREGIVER, UserRole.ADMIN};
        
        for (UserRole role : roles) {
            User user = User.builder()
                    .id(1L)
                    .username("user")
                    .email("user@example.com")
                    .role(role)
                    .enabled(true)
                    .build();

            UserResponse response = UserResponse.fromUser(user);

            assertEquals(role, response.getRole());
        }
    }

    @Test
    void builder_CreatesUserResponse() {
        // Act
        UserResponse response = UserResponse.builder()
                .id(5L)
                .username("builder_user")
                .email("builder@example.com")
                .role(UserRole.CAREGIVER)
                .enabled(true)
                .emailVerified(false)
                .build();

        // Assert
        assertEquals(5L, response.getId());
        assertEquals("builder_user", response.getUsername());
        assertEquals("builder@example.com", response.getEmail());
        assertEquals(UserRole.CAREGIVER, response.getRole());
        assertTrue(response.getEnabled());
        assertFalse(response.getEmailVerified());
    }
}