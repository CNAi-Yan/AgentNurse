package com.agentnurse.model.dto;

import com.agentnurse.model.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void builder_CreatesAuthResponse() {
        // Arrange
        UserResponse user = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.PATIENT)
                .build();

        // Act
        AuthResponse response = AuthResponse.builder()
                .accessToken("jwt-token-12345")
                .tokenType("Bearer")
                .user(user)
                .build();

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token-12345", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUser());
        assertEquals("testuser", response.getUser().getUsername());
    }

    @Test
    void builder_DefaultTokenType() {
        // Act
        AuthResponse response = AuthResponse.builder()
                .accessToken("token")
                .build();

        // Assert
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        // Arrange
        AuthResponse response = new AuthResponse();
        UserResponse user = UserResponse.builder()
                .id(2L)
                .username("user2")
                .build();

        // Act
        response.setAccessToken("new-token");
        response.setTokenType("CustomType");
        response.setUser(user);

        // Assert
        assertEquals("new-token", response.getAccessToken());
        assertEquals("CustomType", response.getTokenType());
        assertEquals("user2", response.getUser().getUsername());
    }

    @Test
    void authResponse_WithAllRoles() {
        UserRole[] roles = {UserRole.PATIENT, UserRole.FAMILY, UserRole.CAREGIVER, UserRole.ADMIN};
        
        for (UserRole role : roles) {
            UserResponse user = UserResponse.builder()
                    .id(1L)
                    .username("user")
                    .role(role)
                    .build();

            AuthResponse response = AuthResponse.builder()
                    .accessToken("token")
                    .user(user)
                    .build();

            assertEquals(role, response.getUser().getRole());
        }
    }
}