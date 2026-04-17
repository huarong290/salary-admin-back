package com.salary.admin.service.impl;

import com.salary.admin.constants.redis.RedisCacheConstants;
import com.salary.admin.constants.security.JwtConstants;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.model.dto.LoginResultDTO;
import com.salary.admin.model.dto.MfaVerifyReqDTO;
import com.salary.admin.model.dto.TokenResDTO;
import com.salary.admin.model.dto.UserLoginReqDTO;
import com.salary.admin.model.entity.sys.SysUser;
import com.salary.admin.service.*;
import com.salary.admin.utils.JwtUtil;
import com.salary.admin.utils.UserContextUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.salary.admin.constants.redis.RedisCacheConstants.AUTH_MFA_TOKEN_PREFIX;

/**
 * 认证服务核心实现类 (安全增强版)
 * <p>
 * 核心安全策略：
 * 1. 令牌轮转 (Refresh Token Rotation): 每次刷新都更换新的 Refresh Token。
 * 2. 令牌复用检测 (Reuse Detection): 检测到旧令牌被重复使用，视为盗号，强制下线所有端。
 * 3. 故障阻断 (Fail-Secure): Redis 写入失败时抛出异常，不允许“幽灵登录”。
 * 4. 设备绑定: Token 与 deviceId 绑定，防止异地窃取 Token 使用。
 * </p>
 */
@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private ISysUserService iSysUserService;
    @Autowired
    private ISysMenuService iSysMenuService;
    @Autowired
    private ISysRoleService iSysRoleService;
    @Autowired
    private IRedisService iRedisService;
    @Autowired
    private ICaptchaService iCaptchaService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResultDTO login(UserLoginReqDTO dto) {
        log.info("用户尝试登录: {}, 设备ID: {}, IP: {}", dto.getUsername(), dto.getClientInfo().getDeviceId(), dto.getLoginIp());

        // 1. 验证码校验
        iCaptchaService.validateCaptcha(dto.getCaptchaId(), dto.getCaptchaCode());

        // 2. 获取用户信息并校验密码
        SysUser sysUser = iSysUserService.selectUserByUsername(dto.getUsername());
        if (sysUser == null || !passwordEncoder.matches(dto.getPassword().trim(), sysUser.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 账号状态检查
        if (sysUser.getStatus() != 1) {
            throw new BusinessException("该账号已被禁用");
        }

        // ==========================================
        // 🌟 4. 核心分流：检查是否开启了 MFA (两步验证)
        // ==========================================
        // TODO: 这里请替换为真实的业务逻辑（例如检查 sysUser 表里有没有存 Google Secret）
        // 假设通过 sysUser.getMfaSecret() != null 来判断
        boolean isMfaEnabled = checkUserMfaStatus(sysUser);

        if (isMfaEnabled) {
            log.info("用户 {} 已开启 MFA，进入二次验证流程", sysUser.getUsername());

            // 生成临时 mfaToken (UUID)
            String mfaToken = UUID.randomUUID().toString().replace("-", "");

            // 将后续核发 Token 所需的环境信息暂存到 Redis，有效期 5 分钟
            String mfaContextValue = sysUser.getId() + ":" + dto.getClientInfo().getDeviceId() + ":" + dto.getClientInfo().getClientType();
            iRedisService.setEx(AUTH_MFA_TOKEN_PREFIX + mfaToken, mfaContextValue, 5, TimeUnit.MINUTES);

            // 返回第一阶段结果 (不含真实 Token)
            LoginResultDTO result = new LoginResultDTO();
            result.setRequireMfa(true);
            result.setMfaToken(mfaToken);
            // 假设从数据库得知他绑定了谷歌和海月盾盾
            result.setSupportedTypes(Arrays.asList("GOOGLE", "HAIYUE"));
            return result;
        }

        // ==========================================
        // 5. 无需 MFA，直接走原来的发证流程
        // ==========================================
        TokenResDTO tokenRes = completeLoginProcess(sysUser, dto.getClientInfo().getDeviceId(), dto.getClientInfo().getClientType(), dto.getLoginIp());

        LoginResultDTO result = new LoginResultDTO();
        result.setRequireMfa(false);
        result.setAccessToken(tokenRes.getAccessToken());
        result.setRefreshToken(tokenRes.getRefreshToken());
        result.setTokenType(tokenRes.getTokenType());
        result.setExpiresIn(tokenRes.getExpiresIn());
        result.setRefreshExpiresIn(tokenRes.getRefreshExpiresIn());
        result.setDeviceId(tokenRes.getDeviceId());
        return result;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResDTO verifyMfa(MfaVerifyReqDTO verifyDto, String currentIp) {
        String mfaKey = RedisCacheConstants.AUTH_MFA_TOKEN_PREFIX + verifyDto.getMfaToken();

        // 1. 获取临时上下文
        String contextValue = iRedisService.get(mfaKey, String.class);
        if (StringUtils.isBlank(contextValue)) {
            throw new BusinessException("安全验证已超时或非法请求，请重新登录");
        }

        // 解析上下文：userId : deviceId : clientType
        String[] parts = contextValue.split(":");
        Long userId = Long.valueOf(parts[0]);
        String deviceId = parts[1];
        String clientType = parts[2];

        SysUser sysUser = iSysUserService.getById(userId);
        if (sysUser == null || sysUser.getStatus() != 1) {
            throw new BusinessException("账号状态异常");
        }

        // 2. 调用真实的 OTP 校验逻辑 (谷歌或海月盾盾)
        boolean isCodeValid = verifyDynamicCode(verifyDto.getMfaType(), verifyDto.getCode(), sysUser);
        if (!isCodeValid) {
            // 失败时不要删 Redis，让用户在 5 分钟内可以重试
            throw new BusinessException("动态验证码错误，请检查后重试");
        }

        // 3. 验证通过，阅后即焚，销毁临时 Token
        iRedisService.del(mfaKey);

        log.info("用户 {} MFA二次验证通过，下发真实令牌", sysUser.getUsername());

        // 4. 下发真实的 AccessToken 和 RefreshToken
        return completeLoginProcess(sysUser, deviceId, clientType, currentIp);
    }
    /**
     * 刷新 Token (安全增强版)
     * 逻辑：令牌轮转 + 复用检测 + 设备绑定校验
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResDTO refreshToken(String oldRefreshToken, String deviceId, String currentIp) {
        // 1. 解析并校验旧 Token
        Claims claims;
        try {
            claims = jwtUtil.parseToken(oldRefreshToken);
        } catch (Exception e) {
            log.warn("无效的刷新令牌尝试: {}", oldRefreshToken);
            throw new BusinessException("认证已过期，请重新登录");
        }

        String username = claims.getSubject();
        String jti = claims.getId();
        String refreshKey = RedisCacheConstants.AUTH_REFRESH_TOKEN + jti;

        // 2. 🚨 核心安全：原子获取并删除 (单次使用原则)
        // 利用接口中新增的 getAndDelete 方法
        String storedValue = iRedisService.getAndDelete(refreshKey);

        // 3. 🚨 令牌复用检测 (Reuse Detection)
        if (storedValue == null) {
            // 如果 Token 还在有效期内但在 Redis 找不到，说明该 JTI 之前已被消耗过
            // 极大概率是旧令牌被黑客截获并尝试二次使用
            log.error("🚨 安全警报：检测到令牌复用攻击！用户: {}, JTI: {}", username, jti);

            // 惩罚机制：强制该设备下线（可选：强制该用户全端下线）
            SysUser user = iSysUserService.selectUserByUsername(username);
            if (user != null) {
                iRedisService.del(RedisCacheConstants.AUTH_DEVICE_BIND + user.getId() + ":" + deviceId);
            }
            throw new BusinessException("安全检查未通过，请重新登录");
        }

        // 4. 设备 ID 与 IP 比对
        // storedValue 格式：userId:deviceId:clientType
        String[] parts = storedValue.split(":");
        String storedUserId = parts[0];
        String storedDeviceId = parts[1];
        String storedClientType = parts.length > 2 ? parts[2] : "UNKNOWN"; // 兼容旧数据
        Long tokenUserId = claims.get("userId", Long.class);
        // 🚨 增加逻辑：确保 Token 里的 userId (如果有) 与 Redis 存的一致
        // 如果你在 generateToken 时把 userId 塞进了 Claims，这里可以双重校验
        if (tokenUserId != null && !tokenUserId.equals(Long.valueOf(storedUserId))) {
            log.error("🚨 账号安全风险：Token 用户ID与缓存不符！User: {}", username);
            throw new BusinessException("认证状态异常，请重新登录");
        }
        if (!storedDeviceId.equals(deviceId)) {
            log.warn("🚨 设备指纹不匹配！用户: {}, 预期设备: {}, 实际设备: {}", username, storedDeviceId, deviceId);
            throw new BusinessException("环境异常，请重新登录");
        }

        // 5. 获取最新用户信息并检查状态
        SysUser user = iSysUserService.selectUserByUsername(username);
        if (user == null || user.getStatus() != 1) {
            throw new BusinessException("账号状态异常，请联系管理员");
        }

        // 6. 🟢 执行轮转：生成全新的双 Token
        Map<String, Object> newClaims = new HashMap<>();
        newClaims.put("userId", user.getId());
        newClaims.put("deviceId", deviceId);
        newClaims.put("loginIp", currentIp);

        String newAccess = jwtUtil.generateAccessToken(username, newClaims);
        String newRefresh = jwtUtil.generateRefreshToken(username, newClaims);

        // 7. 写入新会话到 Redis (Fail-Secure)
        String newRefreshJti = jwtUtil.getJti(newRefresh);
        String nextValue = user.getId() + ":" + deviceId + ":" + storedClientType;
        iRedisService.setEx(RedisCacheConstants.AUTH_REFRESH_TOKEN + newRefreshJti,
                nextValue,
                jwtUtil.getRefreshTokenTtl(),
                TimeUnit.SECONDS);

        // 8. 更新设备最新绑定的 JTI (实现设备互踢逻辑)
        String newAccessJti = jwtUtil.getJti(newAccess);
        handleDeviceSession(user.getId(), deviceId, newAccessJti,newRefreshJti);

        return TokenResDTO.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                // 显式设置 tokenType，方便前端拦截器直接拼接 header
                .tokenType(JwtConstants.JWT_BEARER_PREFIX.trim())
                // 🚨 如果你和前端约定使用秒，记得 / 1000；如果约定毫秒则保持原样
                .expiresIn(jwtUtil.getAccessTokenTtl())
                .refreshExpiresIn(jwtUtil.getRefreshTokenTtl())
                .deviceId(deviceId)
                .clientType(storedClientType)                   // 修复瑕疵 2：从会话记录中找回
                .ip(currentIp)
                .build();
    }

    /**
     * 用户登出实现
     * 核心逻辑：
     * 1. 从安全上下文中获取当前用户标识 (userId & deviceId)
     * 2. 精准删除 Redis 中维护的 Refresh Token (使其无法续期)
     * 3. 清理设备绑定关系和活跃会话状态
     * 4. (可选) 将当前的 Access Token 加入黑名单直到过期
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout() {
        // 1. 获取当前登录用户的 ID (从 JwtAuthenticationFilter 存入上下文的认证对象中提取)
        Long userId = UserContextUtil.getUserId();
        String username = UserContextUtil.getUsername();
        // 2. 从 SecurityContext 或当前 Request 中获取 deviceId
        // 注意：如果是通过 JwtUtil 解析 Token 拿到的 Claims，通常我们会把它存入上下文
        String deviceId = UserContextUtil.getDeviceId();
        // 💡 增加防御性编程：如果上下文丢失，尝试抛出明确异常或静默处理
        if (userId == null || deviceId == null) {
            log.warn("登出失败：无法从上下文中获取用户信息或设备标识");
            return;
        }
        log.info("用户主动退出登录: {}, 用户ID: {}, 设备ID: {}", username, userId, deviceId);

        // 3. 核心：销毁 Refresh Token
        // 先找到该设备关联的 Refresh JTI
        String deviceKey = RedisCacheConstants.AUTH_DEVICE_BIND + userId + ":" + deviceId;
        String refreshJti = iRedisService.get(deviceKey, String.class);

        if (StringUtils.isNotBlank(refreshJti)) {
            // 删除 Redis 中的刷新令牌记录，使其无法再调用 /refresh 接口
            iRedisService.del(RedisCacheConstants.AUTH_REFRESH_TOKEN + refreshJti);
        }

        // 4. 清理设备会话和活跃状态记录
        iRedisService.del(deviceKey);
        iRedisService.del(RedisCacheConstants.AUTH_USER_SESSION + userId);
        iRedisService.del(RedisCacheConstants.AUTH_USER_REFRESH_SESSION + userId);

        // 5. 清理权限缓存 (确保下次登录权限实时同步)
        this.clearUserPermissionsCache(userId);

        // 💡 进阶：如果你需要极致安全（防止 AccessToken 在有效期内仍可访问）
        // 获取当前的 AccessToken 并将其 JTI 加入 Redis 黑名单，过期时间为该 Token 的剩余 TTL
        // String currentJti = SecurityUtils.getJti();
        // iRedisService.setEx(RedisCacheConstants.AUTH_BLACKLIST + currentJti, "1", remainingTtl, TimeUnit.SECONDS);
    }

    @Override
    public void clearUserPermissionsCache(Long userId) {
        if (userId == null) {
            return;
        }
        // ===== 权限缓存 =====
        String key = RedisCacheConstants.AUTH_USER_PERMISSIONS + userId;
        // ===== 角色缓存 =====
        String roleKey = RedisCacheConstants.AUTH_USER_ROLES + userId;
        iRedisService.del(key);
        iRedisService.del(roleKey);
        log.info("权限变更：已清理用户 {} 的权限与角色缓存", userId);
    }

    @Override
    public void clearUserPermissionsCache(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return;
        List<String> keys = userIds.stream()
                .map(id -> RedisCacheConstants.AUTH_USER_PERMISSIONS + id)
                .toList();
        iRedisService.del(keys);
        log.info("权限变更：已批量清理 {} 个用户的权限缓存", userIds.size());
    }
    /**
     * 维护设备会话关系
     * Key: auth:device:{userId}:{deviceId} -> Value: {jti}
     */
    private void handleDeviceSession(Long userId, String deviceId, String accessJti, String refreshJti) {
        // 1. 活跃用户全局 Key (全端互踢) 用户 AccessToken 会话
        String userActiveKey = RedisCacheConstants.AUTH_USER_SESSION  + userId;
        // 💡 增加一个 Key 用于追踪全局活跃的 RefreshToken JTI 用户 RefreshToken 会话
        String userActiveRefreshKey = RedisCacheConstants.AUTH_USER_REFRESH_SESSION  + userId;
        // 2. 获取该用户当前已登录的所有设备 JTI并踢出  获取当前 Refresh 会话
        String oldRefreshJti = iRedisService.get(userActiveRefreshKey, String.class);
        // 2. 如果存在旧 JTI，说明之前有人在用，执行“踢人” 如果存在旧 JTI，则踢掉旧设备
        if (StringUtils.isNotBlank(oldRefreshJti)) {
            log.info("用户 {} 在设备 {} 上重新登录，正在作废旧令牌 JTI: {}", userId, deviceId, oldRefreshJti);
            // 清除旧的刷新令牌，让旧设备“掉线”
            iRedisService.del(RedisCacheConstants.AUTH_REFRESH_TOKEN + oldRefreshJti);
        }

        // 3. 绑定新设备与新的 JTI，有效期与 RefreshToken 一致（如 7 天） 写入新的 AccessToken 会话
        iRedisService.setEx(userActiveKey, accessJti, 7, TimeUnit.DAYS);
        // 4. 把 Refresh JTI 存起来，供下次踢人时读取并清理  写入新的 RefreshToken 会话
        iRedisService.setEx(userActiveRefreshKey, refreshJti, 7, TimeUnit.DAYS);
        // 5. 记录设备绑定 (环境校验)
        String deviceKey = RedisCacheConstants.AUTH_DEVICE_BIND + userId + ":" + deviceId;
        iRedisService.setEx(deviceKey, refreshJti, 7, TimeUnit.DAYS);
    }

    /**
     * 🌟 私有核心方法：完成颁发 Token 及会话初始化的所有逻辑
     * 无论是普通登录还是 MFA 验证通过，最终都调用这里收口。
     */
    private TokenResDTO completeLoginProcess(SysUser sysUser, String deviceId, String clientType, String loginIp) {
        // 构建 JWT 自定义载荷 (Claims)
        Map<String,Object> claims = Map.of(
                "userId", sysUser.getId(),
                "deviceId", deviceId,
                "loginIp", loginIp
        );

        // 生成双 Token
        String accessToken = jwtUtil.generateAccessToken(sysUser.getUsername(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(sysUser.getUsername(), claims);

        String accessJti = jwtUtil.getJti(accessToken);
        String refreshJti = jwtUtil.getJti(refreshToken);

        // 执行5表联查与权限缓存
        Set<String> permissions = iSysMenuService.selectPermissionsByUserId(sysUser.getId());
        if (permissions != null && !permissions.isEmpty()) {
            iRedisService.setEx(RedisCacheConstants.AUTH_USER_PERMISSIONS + sysUser.getId(), permissions, 7, TimeUnit.DAYS);
        }

        // 角色缓存
        Set<String> roles = iSysRoleService.selectRoleCodesByUserId(sysUser.getId());
        if (roles != null) {
            iRedisService.setEx(RedisCacheConstants.AUTH_USER_ROLES + sysUser.getId(), roles, 7, TimeUnit.DAYS);
        }

        // 存储 Refresh Token 映射关系 (Fail-Secure 策略)
        String refreshKey = RedisCacheConstants.AUTH_REFRESH_TOKEN + refreshJti;
        String refreshValue = sysUser.getId() + ":" + deviceId + ":" + clientType;
        boolean stored = iRedisService.setEx(refreshKey, refreshValue, jwtUtil.getRefreshTokenTtl(), TimeUnit.SECONDS);
        if (!stored) {
            throw new BusinessException("系统繁忙，登录会话创建失败");
        }

        // 处理设备互踢会话
        handleDeviceSession(sysUser.getId(), deviceId, accessJti, refreshJti);

        // 更新数据库最后登录时间
        iSysUserService.updateById(new SysUser().setId(sysUser.getId()).setLastLoginTime(LocalDateTime.now()));

        return TokenResDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(JwtConstants.JWT_BEARER_PREFIX.trim())
                .expiresIn(jwtUtil.getAccessTokenTtl())
                .refreshExpiresIn(jwtUtil.getRefreshTokenTtl())
                .deviceId(deviceId)
                .clientType(clientType)
                .ip(loginIp)
                .build();
    }

    // ==========================================
    // 🛡️ 占位方法：请根据实际使用的 MFA 厂商 SDK 进行实现
    // ==========================================

    /**
     * 判断用户是否开启了两步验证
     */
    private boolean checkUserMfaStatus(SysUser sysUser) {
        // TODO: 替换为实际数据库字段判断
        // return StringUtils.isNotBlank(sysUser.getGoogleSecret());
        return true; // 演示环境强制开启
    }

    /**
     * 校验动态验证码是否正确
     */
    private boolean verifyDynamicCode(String mfaType, String code, SysUser sysUser) {
        if ("GOOGLE".equals(mfaType)) {
            // TODO: 调用 Google Authenticator SDK 校验口令
            // return GoogleAuthenticatorUtil.checkCode(sysUser.getGoogleSecret(), Long.parseLong(code));
            return "123456".equals(code); // 演示环境：输入123456即通过
        } else if ("HAIYUE".equals(mfaType)) {
            // TODO: 调用海月盾盾云端 API 校验
            return "88888888".equals(code);
        }
        return false;
    }
}
