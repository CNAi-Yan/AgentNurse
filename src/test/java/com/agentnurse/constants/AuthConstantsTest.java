package com.agentnurse.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthConstantsTest {

    @Test
    void messageInvalidCredentials_HasCorrectValue() {
        // Assert
        assertEquals("用户名/邮箱或密码错误", AuthConstants.MESSAGE_INVALID_CREDENTIALS);
    }

    @Test
    void constructor_ThrowsException() {
        // Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            // Use reflection to access private constructor
            var constructor = AuthConstants.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }

    @Test
    void messageInvalidCredentials_IsNotNull() {
        // Assert
        assertNotNull(AuthConstants.MESSAGE_INVALID_CREDENTIALS);
        assertFalse(AuthConstants.MESSAGE_INVALID_CREDENTIALS.isEmpty());
    }
}
