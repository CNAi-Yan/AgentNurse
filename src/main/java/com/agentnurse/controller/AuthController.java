package com.agentnurse.controller;

import com.agentnurse.model.dto.ApiResponse;
import com.agentnurse.model.dto.AuthResponse;
import com.agentnurse.model.dto.LoginRequest;
import com.agentnurse.model.dto.RegisterRequest;
import com.agentnurse.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("收到注册请求: username={}, email={}, role={}", 
                 request.getUsername(), request.getEmail(), request.getRole());
        
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success("注册成功", response));
        } catch (IllegalArgumentException e) {
            log.warn("注册失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("收到登录请求: usernameOrEmail={}", request.getUsernameOrEmail());
        
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (Exception e) {
            log.warn("登录失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("用户名/邮箱或密码错误"));
        }
    }

    /**
     * 健康检查
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("AgentNurse 服务运行正常"));
    }
}
