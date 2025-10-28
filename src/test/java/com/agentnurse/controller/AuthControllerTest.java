package com.agentnurse.controller;

import com.agentnurse.model.dto.ApiResponse;
import com.agentnurse.model.dto.AuthResponse;
import com.agentnurse.model.dto.LoginRequest;
import com.agentnurse.model.dto.RegisterRequest;
import com.agentnurse.model.dto.UserResponse;
import com.agentnurse.model.enums.UserRole;
import com.agentnurse.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Test@123");
        registerRequest.setRole(UserRole.PATIENT);

        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("Test@123");

        userResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.PATIENT)
                .enabled(true)
                .build();

        authResponse = AuthResponse.builder()
                .accessToken("jwt-token")
                .tokenType("Bearer")
                .user(userResponse)
                .build();
    }

    @Test
    void register_Success_ReturnsOk() {
        // Arrange
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getSuccess());
        assertEquals("注册成功", response.getBody().getMessage());
        assertEquals("jwt-token", response.getBody().getData().getAccessToken());
        assertEquals("testuser", response.getBody().getData().getUser().getUsername());

        verify(authService).register(registerRequest);
    }

    @Test
    void register_UsernameExists_ReturnsBadRequest() {
        // Arrange
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("用户名已存在"));

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getSuccess());
        assertEquals("用户名已存在", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void register_EmailExists_ReturnsBadRequest() {
        // Arrange
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("邮箱已被注册"));

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("邮箱已被注册", response.getBody().getMessage());
    }

    @Test
    void login_Success_ReturnsOk() {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getSuccess());
        assertEquals("登录成功", response.getBody().getMessage());
        assertEquals("jwt-token", response.getBody().getData().getAccessToken());

        verify(authService).login(loginRequest);
    }

    @Test
    void login_InvalidCredentials_ReturnsBadRequest() {
        // Arrange
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("用户名/邮箱或密码错误", response.getBody().getMessage());
    }

    @Test
    void login_ServiceException_ReturnsBadRequest() {
        // Arrange
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("用户名/邮箱或密码错误", response.getBody().getMessage());
    }

    @Test
    void health_ReturnsOk() {
        // Act
        ResponseEntity<ApiResponse<String>> response = authController.health();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals("AgentNurse 服务运行正常", response.getBody().getData());
    }

    @Test
    void register_WithDifferentRoles() {
        // Test registration with each role
        UserRole[] roles = {UserRole.PATIENT, UserRole.FAMILY, UserRole.CAREGIVER, UserRole.ADMIN};
        
        for (UserRole role : roles) {
            registerRequest.setRole(role);
            userResponse.setRole(role);
            when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

            ResponseEntity<ApiResponse<AuthResponse>> response = authController.register(registerRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().getSuccess());
        }
    }
}