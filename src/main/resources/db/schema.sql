-- 创建数据库
CREATE DATABASE IF NOT EXISTS agent_nurse 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE agent_nurse;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密后）',
    role VARCHAR(20) NOT NULL COMMENT '用户角色：PATIENT/FAMILY/CAREGIVER/ADMIN',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    gender CHAR(1) COMMENT '性别：M/F/U',
    birth_date VARCHAR(20) COMMENT '出生日期',
    address VARCHAR(200) COMMENT '地址',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '账户是否启用',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE COMMENT '邮箱是否已验证',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
