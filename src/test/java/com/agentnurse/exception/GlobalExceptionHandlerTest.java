package com.agentnurse.exception;

import com.agentnurse.constants.AuthConstants;
import com.agentnurse.model.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationExceptions_ReturnsValidationErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("user", "username", "用户名不能为空");
        FieldError fieldError2 = new FieldError("user", "email", "邮箱格式不正确");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // Act
        ResponseEntity<ApiResponse<Map<String, String>>> response = 
                exceptionHandler.handleValidationExceptions(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("参数验证失败", response.getBody().getMessage());
    }

    @Test
    void handleIllegalArgumentException_ReturnsBadRequest() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("参数非法");

        // Act
        ResponseEntity<ApiResponse<Void>> response = 
                exceptionHandler.handleIllegalArgumentException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("参数非法", response.getBody().getMessage());
    }

    @Test
    void handleUsernameNotFoundException_ReturnsNotFound() {
        // Arrange
        UsernameNotFoundException exception = new UsernameNotFoundException("用户不存在");

        // Act
        ResponseEntity<ApiResponse<Void>> response = 
                exceptionHandler.handleUsernameNotFoundException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("用户不存在", response.getBody().getMessage());
    }

    @Test
    void handleBadCredentialsException_ReturnsUnauthorized() {
        // Arrange
        BadCredentialsException exception = new BadCredentialsException("认证失败");

        // Act
        ResponseEntity<ApiResponse<Void>> response = 
                exceptionHandler.handleBadCredentialsException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals(AuthConstants.MESSAGE_INVALID_CREDENTIALS, response.getBody().getMessage());
    }

    @Test
    void handleAccessDeniedException_ReturnsForbidden() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("访问被拒绝");

        // Act
        ResponseEntity<ApiResponse<Void>> response = 
                exceptionHandler.handleAccessDeniedException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("没有权限访问此资源", response.getBody().getMessage());
    }

    @Test
    void handleGlobalException_ReturnsInternalServerError() {
        // Arrange
        Exception exception = new Exception("服务器错误");

        // Act
        ResponseEntity<ApiResponse<Void>> response = 
                exceptionHandler.handleGlobalException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("服务器内部错误，请稍后重试", response.getBody().getMessage());
    }

    @Test
    void handleRuntimeException_ReturnsInternalServerError() {
        // Arrange
        RuntimeException exception = new RuntimeException("运行时错误");

        // Act
        ResponseEntity<ApiResponse<Void>> response = 
                exceptionHandler.handleGlobalException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void handleNullPointerException_ReturnsInternalServerError() {
        // Arrange
        NullPointerException exception = new NullPointerException("空指针异常");

        // Act
        ResponseEntity<ApiResponse<Void>> response = 
                exceptionHandler.handleGlobalException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
    }
}