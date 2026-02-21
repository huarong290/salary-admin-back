package com.salary.admin.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * JWT 认证异常 用于表示 Token 无效、过期、被拉黑等情况
 *
 * 用途：
 *  - 表示 Token 无效、过期、被拉黑等情况
 *  - 继承自 Spring Security 的 AuthenticationException
 *    这样可以被 Security 的 AuthenticationEntryPoint 捕获
 */
public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}
