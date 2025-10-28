package com.agentnurse.controller;

import com.agentnurse.model.dto.ApiResponse;
import com.agentnurse.model.dto.UserProfileRequest;
import com.agentnurse.model.dto.UserResponse;
import com.agentnurse.model.enums.UserRole;
import com.agentnurse.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse userResponse;
    private UserProfileRequest profileRequest;

    @BeforeEach
    void setUp() {
        userResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.PATIENT)
                .realName("Test User")
                .phone("1234567890")
                .enabled(true)
                .build();

        profileRequest = new UserProfileRequest();
        profileRequest.setRealName("Updated Name");
        profileRequest.setPhone("9876543210");
    }

    @Test
    void getCurrentUser_Success_ReturnsOk() {
        // Arrange
        when(userService.getCurrentUserProfile()).thenReturn(userResponse);

        // Act
        ResponseEntity<ApiResponse<UserResponse>> response = userController.getCurrentUser();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals("testuser", response.getBody().getData().getUsername());

        verify(userService).getCurrentUserProfile();
    }

    @Test
    void updateProfile_Success_ReturnsOk() {
        // Arrange
        UserResponse updatedUser = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.PATIENT)
                .realName("Updated Name")
                .phone("9876543210")
                .enabled(true)
                .build();

        when(userService.updateProfile(any(UserProfileRequest.class))).thenReturn(updatedUser);

        // Act
        ResponseEntity<ApiResponse<UserResponse>> response = userController.updateProfile(profileRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals("资料更新成功", response.getBody().getMessage());
        assertEquals("Updated Name", response.getBody().getData().getRealName());
        assertEquals("9876543210", response.getBody().getData().getPhone());

        verify(userService).updateProfile(profileRequest);
    }

    @Test
    void updateProfile_EmailExists_ReturnsBadRequest() {
        // Arrange
        when(userService.updateProfile(any(UserProfileRequest.class)))
                .thenThrow(new IllegalArgumentException("邮箱已被其他用户使用"));

        // Act
        ResponseEntity<ApiResponse<UserResponse>> response = userController.updateProfile(profileRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("邮箱已被其他用户使用", response.getBody().getMessage());
    }

    @Test
    void deleteAccount_Success_ReturnsOk() {
        // Arrange
        doNothing().when(userService).deleteAccount();

        // Act
        ResponseEntity<ApiResponse<Void>> response = userController.deleteAccount();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals("账户已删除", response.getBody().getMessage());

        verify(userService).deleteAccount();
    }

    @Test
    void getUserById_Success_ReturnsOk() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(userResponse);

        // Act
        ResponseEntity<ApiResponse<UserResponse>> response = userController.getUserById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals(1L, response.getBody().getData().getId());
        assertEquals("testuser", response.getBody().getData().getUsername());

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_UserNotFound_ReturnsBadRequest() {
        // Arrange
        when(userService.getUserById(999L))
                .thenThrow(new IllegalArgumentException("用户不存在: 999"));

        // Act
        ResponseEntity<ApiResponse<UserResponse>> response = userController.getUserById(999L);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertTrue(response.getBody().getMessage().contains("用户不存在"));
    }

    @Test
    void updateProfile_WithAllFields() {
        // Arrange
        profileRequest.setEmail("newemail@example.com");
        profileRequest.setRealName("New Name");
        profileRequest.setPhone("5555555555");
        profileRequest.setGender("F");
        profileRequest.setBirthDate("1990-05-05");
        profileRequest.setAddress("New Address");
        profileRequest.setAvatarUrl("https://example.com/avatar.jpg");

        UserResponse updatedUser = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("newemail@example.com")
                .role(UserRole.PATIENT)
                .realName("New Name")
                .phone("5555555555")
                .gender("F")
                .birthDate("1990-05-05")
                .address("New Address")
                .avatarUrl("https://example.com/avatar.jpg")
                .enabled(true)
                .build();

        when(userService.updateProfile(any(UserProfileRequest.class))).thenReturn(updatedUser);

        // Act
        ResponseEntity<ApiResponse<UserResponse>> response = userController.updateProfile(profileRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponse data = response.getBody().getData();
        assertEquals("newemail@example.com", data.getEmail());
        assertEquals("New Name", data.getRealName());
        assertEquals("F", data.getGender());
        assertEquals("1990-05-05", data.getBirthDate());
    }
}