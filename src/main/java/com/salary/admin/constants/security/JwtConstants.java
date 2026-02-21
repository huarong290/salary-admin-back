package com.salary.admin.constants.security;


/**
 * JWT 相关常量
 * <p>
 * 1. Header 信息
 * 2. Token 类型
 * 3. Redis 前缀
 * 4. Claims Key 常量
 */
public final class JwtConstants {

    private JwtConstants() {}

    // ---------------- Header ----------------

    /**
     * Authorization Header
     */
    public static final String JWT_HEADER = "Authorization";

    /**
     * Bearer 前缀（注意后面有空格）
     */
    public static final String JWT_BEARER_PREFIX = "Bearer ";

    // ---------------- Redis ----------------

    /**
     * Redis 中 refreshToken 的 key 前缀
     */
    public static final String REFRESH_TOKEN_PREFIX = "jwt:refresh:";

    /**
     * Redis 中 jti 黑名单 key 前缀（防重放）
     */
    public static final String JTI_BLACKLIST_PREFIX = "jwt:blacklist:";

    // ---------------- Token 类型 ----------------

    /**
     * AccessToken 类型标识
     */
    public static final String TOKEN_TYPE_ACCESS = "access";

    /**
     * RefreshToken 类型标识
     *
     */
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    // ---------------- Claims Key ----------------

    /**
     * JWT ID (jti)
     */
    public static final String CLAIM_JTI = "jti";

    /**
     * Token 类型字段
     */
    public static final String CLAIM_TOKEN_TYPE = "type";

    /**
     * 用户名字段 (sub)
     */
    public static final String CLAIM_USERNAME = "sub";
}
