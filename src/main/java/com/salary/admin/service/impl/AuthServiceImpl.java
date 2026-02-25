package com.salary.admin.service.impl;

import com.salary.admin.constants.redis.RedisCacheConstants;
import com.salary.admin.constants.security.JwtConstants;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.model.dto.TokenResDTO;
import com.salary.admin.model.dto.UserLoginReqDTO;
import com.salary.admin.model.entity.sys.SysUser;
import com.salary.admin.service.IAuthService;
import com.salary.admin.service.IRedisService;
import com.salary.admin.service.ISysMenuService;
import com.salary.admin.service.ISysUserService;
import com.salary.admin.utils.JwtUtil;
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
    private IRedisService iRedisService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    /**
     * 用户登录
     *
     * @param dto 用户登录请求参数
     * @return TokenResDTO 包含 AccessToken、RefreshToken 等信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResDTO login(UserLoginReqDTO dto) {
        log.info("用户尝试登录: {}, 设备ID: {}, IP: {}", dto.getUsername(), dto.getClientInfo().getDeviceId(), dto.getLoginIp());
        // 1. 验证码校验

        //2. 获取用户信息并校验
        SysUser sysUser = iSysUserService.selectUserByUsername(dto.getUsername());
        if (sysUser == null || !passwordEncoder.matches(dto.getPassword(), sysUser.getPassword())) {
            // 生产建议：返回模糊错误信息，防止账号嗅探
            throw new BusinessException("用户名或密码错误");
        }
        // 3. 账号状态检查
        if (sysUser.getStatus() != 1) {
            throw new BusinessException("该账号已被禁用");
        }
        // 4. 构建 JWT 自定义载荷 (Claims)
        // 将设备ID和IP存入 Token，方便后续刷新时比对环境一致性
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", sysUser.getId().toString()); // 转为 String 存入
        claims.put("deviceId", dto.getClientInfo().getDeviceId());
        claims.put("loginIp", dto.getLoginIp());

        // 5. 生成双 Token (Access & Refresh)
        String accessToken = jwtUtil.generateAccessToken(sysUser.getUsername(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(sysUser.getUsername(), claims);

        // 6. 获取 JTI (JWT唯一标识) 用于管理 Refresh Token 生命周期
        String accessJti = jwtUtil.getJti(accessToken);
        String refreshJti = jwtUtil.getJti(refreshToken);
        //7. 执行5表联查
        Set<String> permissions = iSysMenuService.selectPermissionsByUserId(sysUser.getId());
        if (permissions != null && !permissions.isEmpty()) {
            // 存入 Redis，Key 为 auth:permission:{userId}
            iRedisService.setEx(RedisCacheConstants.AUTH_USER_PERMISSIONS + sysUser.getId(),
                    permissions, 7, TimeUnit.DAYS);
        }
        // 8. 存储 Refresh Token 映射关系 (Fail-Secure 策略)
        // Key: auth:refresh:{jti} -> Value: {userId}:{deviceId}
        String refreshKey = RedisCacheConstants.AUTH_REFRESH_TOKEN + refreshJti;

        // 修改存储到 Redis 的 Value 格式：userId:deviceId:clientType
        String refreshValue = sysUser.getId() + ":" + dto.getClientInfo().getDeviceId() + ":" + dto.getClientInfo().getClientType();

        // 保存至 Redis，时间与 RefreshToken 有效期一致（如 7 天）
        boolean stored = iRedisService.setEx(refreshKey, refreshValue, jwtUtil.getRefreshTokenTtl(), TimeUnit.SECONDS);
        if (!stored) {
            log.error("Redis 写入失败，阻断登录。User: {}", sysUser.getUsername());
            throw new BusinessException("系统繁忙，登录会话创建失败");
        }
        // 8. 处理设备会话 (全端挤兑)
        // 如果需要同一账号同一端只能一个在线，可以在这里清理旧的 deviceKey
        handleDeviceSession(sysUser.getId(), dto.getClientInfo().getDeviceId(), accessJti, refreshJti);

        // 9. 更新数据库最后登录信息 (虚拟线程会处理好阻塞)
        iSysUserService.updateById(new SysUser()
                .setId(sysUser.getId())
                .setLastLoginTime(LocalDateTime.now()));

        // 10. 组装返回 (符合 OAuth 2.0 规范版)
        return TokenResDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                // 显式设置 tokenType，方便前端拦截器直接拼接 header
                .tokenType(JwtConstants.JWT_BEARER_PREFIX.trim())
                // 🚨 如果你和前端约定使用秒，记得 / 1000；如果约定毫秒则保持原样
                .expiresIn(jwtUtil.getAccessTokenTtl())
                .refreshExpiresIn(jwtUtil.getRefreshTokenTtl())
                .deviceId(dto.getClientInfo().getDeviceId())
                .clientType(dto.getClientInfo().getClientType())
                .ip(dto.getLoginIp())
                .build();
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
        String tokenUserId = claims.get("userId", String.class);
        // 🚨 增加逻辑：确保 Token 里的 userId (如果有) 与 Redis 存的一致
        // 如果你在 generateToken 时把 userId 塞进了 Claims，这里可以双重校验
        if (tokenUserId != null && !tokenUserId.equals(storedUserId)) {
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
        newClaims.put("userId", user.getId().toString());
        newClaims.put("deviceId", deviceId);
        newClaims.put("loginIp", currentIp);

        String newAccess = jwtUtil.generateAccessToken(username, newClaims);
        String newRefresh = jwtUtil.generateRefreshToken(username, newClaims);

        // 7. 写入新会话到 Redis (Fail-Secure)
        String newAccessJti = jwtUtil.getJti(newAccess);
        String newRefreshJti = jwtUtil.getJti(newRefresh);
        String nextValue = user.getId() + ":" + deviceId + ":" + storedClientType;
        iRedisService.setEx(RedisCacheConstants.AUTH_REFRESH_TOKEN + newAccessJti,
                nextValue,
                jwtUtil.getRefreshTokenTtl(),
                TimeUnit.SECONDS);

        // 8. 更新设备最新绑定的 JTI (实现设备互踢逻辑)
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
    @Override
    public void clearUserPermissionsCache(Long userId) {
        if (userId == null) return;
        String key = RedisCacheConstants.AUTH_USER_PERMISSIONS + userId;
        iRedisService.del(key);
        log.info("权限变更：已清理用户 {} 的权限缓存", userId);
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
        // 1. 活跃用户全局 Key (全端互踢)
        String userActiveKey = RedisCacheConstants.AUTH_USER_ACTIVE + userId;
        // 💡 增加一个 Key 用于追踪全局活跃的 RefreshToken JTI
        String userActiveRefreshKey = RedisCacheConstants.AUTH_USER_ACTIVE + ":refresh:" + userId;
        // 2. 获取该用户当前已登录的所有设备 JTI并踢出
        String oldRefreshJti = iRedisService.get(userActiveRefreshKey, String.class);
        // 2. 如果存在旧 JTI，说明之前有人在用，执行“踢人”
        if (StringUtils.isNotBlank(oldRefreshJti)) {
            log.info("用户 {} 在设备 {} 上重新登录，正在作废旧令牌 JTI: {}", userId, deviceId, oldRefreshJti);
            // 清除旧的刷新令牌，让旧设备“掉线”
            iRedisService.del(RedisCacheConstants.AUTH_REFRESH_TOKEN + oldRefreshJti);
        }

        // 3. 绑定新设备与新的 JTI，有效期与 RefreshToken 一致（如 7 天）
        iRedisService.setEx(userActiveKey, accessJti, 7, TimeUnit.DAYS);
        // 4. 把 Refresh JTI 存起来，供下次踢人时读取并清理
        iRedisService.setEx(userActiveRefreshKey, refreshJti, 7, TimeUnit.DAYS);
        // 5. 记录设备绑定 (环境校验)
        String deviceKey = RedisCacheConstants.AUTH_DEVICE_BIND + userId + ":" + deviceId;
        iRedisService.setEx(deviceKey, refreshJti, 7, TimeUnit.DAYS);
    }
}
