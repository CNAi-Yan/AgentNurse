package com.agentnurse.constants;

/**
 * 认证相关常量
 */
public final class AuthConstants {

    private AuthConstants() {
        // 防止实例化
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 无效凭证错误消息
     */
    public static final String MESSAGE_INVALID_CREDENTIALS = "用户名/邮箱或密码错误";
}
