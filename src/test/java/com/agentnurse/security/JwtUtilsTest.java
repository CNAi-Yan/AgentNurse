package com.agentnurse.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private static final String TEST_SECRET = "TestSecretKeyForJWTTokenGenerationMustBeLongEnough32Chars";
    private static final long TEST_EXPIRATION = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", TEST_EXPIRATION);
    }

    @Test
    void generateTokenFromUsername_Success() {
        // Act
        String token = jwtUtils.generateTokenFromUsername("testuser");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void generateToken_FromAuthentication_Success() {
        // Arrange
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Act
        String token = jwtUtils.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUsernameFromToken_Success() {
        // Arrange
        String username = "testuser";
        String token = jwtUtils.generateTokenFromUsername(username);

        // Act
        String extractedUsername = jwtUtils.getUsernameFromToken(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void getUsernameFromToken_DifferentUsernames() {
        // Test multiple different usernames
        String[] usernames = {"user1", "admin", "john_doe", "test@example.com"};
        
        for (String username : usernames) {
            String token = jwtUtils.generateTokenFromUsername(username);
            String extractedUsername = jwtUtils.getUsernameFromToken(token);
            assertEquals(username, extractedUsername);
        }
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Arrange
        String token = jwtUtils.generateTokenFromUsername("testuser");

        // Act
        boolean isValid = jwtUtils.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_MalformedToken_ReturnsFalse() {
        // Arrange
        String malformedToken = "invalid.token.here";

        // Act
        boolean isValid = jwtUtils.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_EmptyToken_ReturnsFalse() {
        // Act
        boolean isValid = jwtUtils.validateToken("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_NullToken_ReturnsFalse() {
        // Act
        boolean isValid = jwtUtils.validateToken(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        // Arrange - Create a token with very short expiration
        JwtUtils shortLivedJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(shortLivedJwtUtils, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(shortLivedJwtUtils, "jwtExpirationMs", -1000L); // Already expired

        String expiredToken = shortLivedJwtUtils.generateTokenFromUsername("testuser");

        // Act
        boolean isValid = jwtUtils.validateToken(expiredToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_TokenWithDifferentSecret_ReturnsFalse() {
        // Arrange - Create token with one secret
        String token = jwtUtils.generateTokenFromUsername("testuser");

        // Create new JwtUtils with different secret
        JwtUtils differentSecretJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(differentSecretJwtUtils, "jwtSecret", 
            "DifferentSecretKeyForJWTTokenGeneration32Chars!!");
        ReflectionTestUtils.setField(differentSecretJwtUtils, "jwtExpirationMs", TEST_EXPIRATION);

        // Act - Try to validate with different secret
        boolean isValid = differentSecretJwtUtils.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void generateTokenFromUsername_NullUsername_Success() {
        // Null username should still create a token (though may not be useful)
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> jwtUtils.generateTokenFromUsername(null));
    }

    @Test
    void generateTokenFromUsername_EmptyUsername_Success() {
        // Act
        String token = jwtUtils.generateTokenFromUsername("");

        // Assert
        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
        assertEquals("", jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void tokenRoundTrip_MultipleTokens() {
        // Test that multiple tokens can be generated and validated independently
        String token1 = jwtUtils.generateTokenFromUsername("user1");
        String token2 = jwtUtils.generateTokenFromUsername("user2");
        String token3 = jwtUtils.generateTokenFromUsername("user3");

        assertTrue(jwtUtils.validateToken(token1));
        assertTrue(jwtUtils.validateToken(token2));
        assertTrue(jwtUtils.validateToken(token3));

        assertEquals("user1", jwtUtils.getUsernameFromToken(token1));
        assertEquals("user2", jwtUtils.getUsernameFromToken(token2));
        assertEquals("user3", jwtUtils.getUsernameFromToken(token3));
    }
}