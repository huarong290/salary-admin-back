package com.salary.admin.constants.redis;

/**
 * Redis 缓存常量统一管理类
 * 命名规范：模块:子模块:标识 (如 auth:token:refresh:)
 */
public final class RedisCacheConstants {

    private RedisCacheConstants() {}

    /** 认证模块根前缀 */
    private static final String AUTH_ROOT = "auth:";

    private static final String BIZ_ROOT = "biz:";
    // ============================ 登录认证相关 ============================

    /**
     *  Refresh Token 映射关系
     * auth:token:refresh:{jti} -> userId:deviceId
     */
    public static final String AUTH_REFRESH_TOKEN = AUTH_ROOT + "token:refresh:";

    /**
     * 用户活跃会话 (单点登录/互踢)
     * auth:session:active:{userId} -> jti
     */
    public static final String AUTH_USER_ACTIVE = AUTH_ROOT + "session:active:";

    /**
     * 设备与 JTI 绑定关系 (环境校验)
     * auth:device:bind:{userId}:{deviceId} -> jti
     */
    public static final String AUTH_DEVICE_BIND = AUTH_ROOT + "device:bind:";
    /**
     * Token 黑名单 (用于手动注销或安全拉黑)
     */
    public static final String AUTH_TOKEN_BLACKLIST = AUTH_ROOT + "token:blacklist:";

    /**
     * 验证码缓存
     * auth:captcha:{uuid} -> code
     */
    public static final String AUTH_CAPTCHA = AUTH_ROOT + "captcha:";

    /**
     * 登录失败计数 (锁定账号)
     * auth:login:limit:{username} -> count
     */
    public static final String AUTH_LOGIN_LIMIT = AUTH_ROOT + "login:limit:";

    // ============================ 业务缓存预留 ============================

    /**
     * 用户信息缓存
     */
    public static final String BIZ_USER_INFO = "biz:user:info:";
}
