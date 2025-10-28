package com.agentnurse.service;

import com.agentnurse.model.dto.UserProfileRequest;
import com.agentnurse.model.dto.UserResponse;
import com.agentnurse.model.entity.User;
import com.agentnurse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 获取当前登录用户
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
    }

    /**
     * 获取当前用户信息
     */
    public UserResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        return UserResponse.fromUser(user);
    }

    /**
     * 根据ID获取用户信息
     */
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        return UserResponse.fromUser(user);
    }

    /**
     * 更新用户资料
     */
    @Transactional
    public UserResponse updateProfile(UserProfileRequest request) {
        User user = getCurrentUser();

        // 更新邮箱（需要重新验证）
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("邮箱已被其他用户使用");
            }
            user.setEmail(request.getEmail());
            user.setEmailVerified(false);
        }

        // 更新其他资料
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        userRepository.save(user);

        log.info("用户资料更新成功: username={}", user.getUsername());

        return UserResponse.fromUser(user);
    }

    /**
     * 删除用户账户
     */
    @Transactional
    public void deleteAccount() {
        User user = getCurrentUser();
        userRepository.delete(user);
        log.info("用户账户已删除: username={}", user.getUsername());
    }
}
