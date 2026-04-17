package com.salary.admin.constants.redis;

/**
 * Redis 缓存常量统一管理类
 *
 * <p>命名规范：</p>
 * <pre>
 * 模块:子模块:类型:标识
 *
 * 示例：
 * auth:token:refresh:{jti}
 * auth:session:user:{userId}
 * auth:device:bind:{userId}:{deviceId}
 * </pre>
 *
 * <p>设计原则：</p>
 * <ul>
 *     <li>统一前缀，方便 Redis Key 管理</li>
 *     <li>语义清晰，层级分明</li>
 *     <li>避免 Key 命名冲突</li>
 *     <li>便于后续系统扩展</li>
 * </ul>
 *
 * <p>Redis Key 总体结构：</p>
 *
 * <pre>
 * auth
 *  ├─ token
 *  │   ├─ access:{jti}
 *  │   ├─ refresh:{jti}
 *  │   └─ blacklist:{jti}
 *  │
 *  ├─ session
 *  │   ├─ user:{userId}
 *  │   └─ refresh:{userId}
 *  │
 *  ├─ device
 *  │   └─ bind:{userId}:{deviceId}
 *  │
 *  ├─ user
 *  │   ├─ auth:{userId}
 *  │   └─ devices:{userId}
 *  │
 *  ├─ permission:{userId}
 *  ├─ roles:{userId}
 *  ├─ captcha:{uuid}
 *  └─ login:limit:{username}
 *
 * biz
 *  └─ user:info:{userId}
 * </pre>
 */
public final class RedisCacheConstants {

    private RedisCacheConstants() {}

    /**
     * 认证模块前缀
     */
    private static final String AUTH_ROOT = "auth:";

    /**
     * 业务模块前缀
     */
    private static final String BIZ_ROOT = "biz:";

    // ============================ TTL 统一配置 ⭐新增 ============================

    /**
     * 权限缓存过期时间（天）
     */
    public static final long AUTH_PERMISSION_CACHE_TTL_DAYS = 7;   // ⭐ 新增

    /**
     * 角色缓存过期时间（天）
     */
    public static final long AUTH_ROLE_CACHE_TTL_DAYS = 7;         // ⭐ 新增

    /**
     * 验证码过期时间（分钟）
     */
    public static final long AUTH_CAPTCHA_TTL_MINUTES = 5;         // ⭐ 新增

    /**
     * 登录失败计数锁定时间（分钟）
     */
    public static final long AUTH_LOGIN_LIMIT_TTL_MINUTES = 15;    // ⭐ 新增


    // ============================ Token 相关 ============================

    /**
     * Access Token 信息
     *
     * auth:token:access:{jti} -> userId
     *
     * 用途：
     * 1. Token 风控
     * 2. Token 统计
     * 3. Token 强制失效
     */
    public static final String AUTH_ACCESS_TOKEN = AUTH_ROOT + "token:access:";   // ⭐ 新增


    /**
     * Refresh Token 映射关系
     *
     * auth:token:refresh:{jti} -> userId:deviceId:clientType
     *
     * 用途：
     * 1. Refresh Token 单次使用
     * 2. Refresh Token Rotation
     */
    public static final String AUTH_REFRESH_TOKEN = AUTH_ROOT + "token:refresh:";


    /**
     * Token 黑名单
     *
     * auth:token:blacklist:{jti} -> 1
     *
     * 用途：
     * 1. 主动登出
     * 2. 安全风控
     */
    public static final String AUTH_TOKEN_BLACKLIST = AUTH_ROOT + "token:blacklist:";


    // ============================ 会话管理 ============================

    /**
     * 用户当前活跃 AccessToken
     *
     * auth:session:user:{userId} -> accessJti
     *
     * 用途：
     * 单账号全端互踢
     */
    public static final String AUTH_USER_SESSION = AUTH_ROOT + "session:user:";


    /**
     * 用户当前活跃 RefreshToken
     *
     * auth:session:refresh:{userId} -> refreshJti
     *
     * 用途：
     * 登录互踢
     */
    public static final String AUTH_USER_REFRESH_SESSION = AUTH_ROOT + "session:refresh:";


    /**
     * 设备绑定关系
     *
     * auth:device:bind:{userId}:{deviceId} -> refreshJti
     *
     * 用途：
     * 设备唯一会话
     */
    public static final String AUTH_DEVICE_BIND = AUTH_ROOT + "device:bind:";


    /**
     * 用户登录设备列表
     *
     * auth:user:devices:{userId} -> Set<deviceId>
     *
     * 用途：
     * 1. 查看登录设备
     * 2. 踢出指定设备
     * 3. 设备管理
     */
    public static final String AUTH_USER_DEVICES = AUTH_ROOT + "user:devices:";   // ⭐ 新增


    // ============================ 权限缓存 ============================

    /**
     * 用户权限缓存
     *
     * auth:permission:{userId} -> Set<String>
     */
    public static final String AUTH_USER_PERMISSIONS = AUTH_ROOT + "permission:";


    /**
     * 用户角色缓存
     * auth:roles:{userId} -> Set<String>
     */
    public static final String AUTH_USER_ROLES = AUTH_ROOT + "roles:";


    /**
     * 用户权限综合缓存
     * auth:user:auth:{userId} -> {roles, permissions}
     * 用途：
     * 1. 减少 Redis 查询次数
     * 2. 高并发权限查询优化
     */
    public static final String AUTH_USER_AUTH = AUTH_ROOT + "user:auth:";


    // ============================ 安全控制 ============================
    // MFA 临时令牌的 Redis 前缀 (你可以将它移到 RedisCacheConstants 中)
    public static final String AUTH_MFA_TOKEN_PREFIX = "auth:mfa:token:";
    /**
     * 验证码缓存
     *
     * auth:captcha:{uuid} -> code
     */
    public static final String AUTH_CAPTCHA = AUTH_ROOT + "captcha:";


    /**
     * 登录失败计数
     *
     * auth:login:limit:{username} -> count
     */
    public static final String AUTH_LOGIN_LIMIT = AUTH_ROOT + "login:limit:";


    // ============================ 业务缓存 ============================

    /**
     * 用户信息缓存
     *
     * biz:user:info:{userId}
     */
    public static final String BIZ_USER_INFO = BIZ_ROOT + "user:info:";
}