package com.agentnurse.model.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void success_WithData_CreatesSuccessResponse() {
        // Arrange
        String testData = "Test Data";

        // Act
        ApiResponse<String> response = ApiResponse.success(testData);

        // Assert
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals("操作成功", response.getMessage());
        assertEquals(testData, response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void success_WithCustomMessage_CreatesSuccessResponse() {
        // Arrange
        String message = "Custom success message";
        Integer testData = 123;

        // Act
        ApiResponse<Integer> response = ApiResponse.success(message, testData);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(testData, response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void error_WithMessage_CreatesErrorResponse() {
        // Arrange
        String errorMessage = "Error occurred";

        // Act
        ApiResponse<String> response = ApiResponse.error(errorMessage);

        // Assert
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void builder_CreatesCustomResponse() {
        // Arrange & Act
        LocalDateTime timestamp = LocalDateTime.now();
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Custom message")
                .data("Custom data")
                .timestamp(timestamp)
                .build();

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("Custom message", response.getMessage());
        assertEquals("Custom data", response.getData());
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    void success_WithNullData_WorksCorrectly() {
        // Act
        ApiResponse<String> response = ApiResponse.success(null);

        // Assert
        assertTrue(response.getSuccess());
        assertNull(response.getData());
    }

    @Test
    void timestampIsSet_WhenCreatingResponse() {
        // Arrange
        LocalDateTime before = LocalDateTime.now();

        // Act
        ApiResponse<String> response = ApiResponse.success("test");
        
        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertNotNull(response.getTimestamp());
        assertFalse(response.getTimestamp().isBefore(before));
        assertFalse(response.getTimestamp().isAfter(after));
    }

    @Test
    void success_WithComplexObject() {
        // Arrange
        UserResponse user = UserResponse.builder()
                .id(1L)
                .username("test")
                .email("test@example.com")
                .build();

        // Act
        ApiResponse<UserResponse> response = ApiResponse.success(user);

        // Assert
        assertTrue(response.getSuccess());
        assertNotNull(response.getData());
        assertEquals("test", response.getData().getUsername());
    }
}