package com.agentnurse.controller;

import com.agentnurse.model.dto.ApiResponse;
import com.agentnurse.model.dto.UserProfileRequest;
import com.agentnurse.model.dto.UserResponse;
import com.agentnurse.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息
     * GET /api/users/me
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        log.info("获取当前用户信息");
        UserResponse user = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * 更新当前用户资料
     * PUT /api/users/me
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UserProfileRequest request) {
        log.info("更新用户资料");
        
        try {
            UserResponse user = userService.updateProfile(request);
            return ResponseEntity.ok(ApiResponse.success("资料更新成功", user));
        } catch (IllegalArgumentException e) {
            log.warn("资料更新失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除当前用户账户
     * DELETE /api/users/me
     */
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteAccount() {
        log.info("删除用户账户");
        userService.deleteAccount();
        return ResponseEntity.ok(ApiResponse.success("账户已删除", null));
    }

    /**
     * 根据ID获取用户信息（管理员功能）
     * GET /api/users/{userId}
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        log.info("获取用户信息: userId={}", userId);
        
        try {
            UserResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (IllegalArgumentException e) {
            log.warn("获取用户失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
