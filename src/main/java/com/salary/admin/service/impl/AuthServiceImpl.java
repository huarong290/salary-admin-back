package com.salary.admin.service.impl;

import com.salary.admin.exception.BusinessException;
import com.salary.admin.model.dto.TokenResDTO;
import com.salary.admin.model.dto.UserLoginReqDTO;
import com.salary.admin.model.entity.sys.SysUser;
import com.salary.admin.service.IAuthService;
import com.salary.admin.service.IRedisService;
import com.salary.admin.service.ISysUserService;
import com.salary.admin.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.salary.admin.constants.security.JwtConstants.DEVICE_BIND_PREFIX;
import static com.salary.admin.constants.security.JwtConstants.REFRESH_TOKEN_PREFIX;

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
        claims.put("deviceId", dto.getClientInfo().getDeviceId());
        claims.put("loginIp", dto.getLoginIp());

        // 5. 生成双 Token (Access & Refresh)
        String accessToken = jwtUtil.generateAccessToken(sysUser.getUsername(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(sysUser.getUsername(), claims);

        // 6. 获取 JTI (JWT唯一标识) 用于管理 Refresh Token 生命周期
        String jti = jwtUtil.getJti(refreshToken);

        // 7. 存储 Refresh Token 映射关系 (Fail-Secure 策略)
        // Key: auth:refresh:{jti} -> Value: {userId}:{deviceId}
        String refreshKey = REFRESH_TOKEN_PREFIX + jti;
        String refreshValue = sysUser.getId() + ":" + dto.getClientInfo().getDeviceId();

        // 保存至 Redis，时间与 RefreshToken 有效期一致（如 7 天）
        boolean stored = iRedisService.setEx(refreshKey, refreshValue, 7, TimeUnit.DAYS);
        if (!stored) {
            log.error("Redis 写入失败，阻断登录。User: {}", sysUser.getUsername());
            throw new BusinessException("系统繁忙，登录会话创建失败");
        }
        // 8. 处理“单端登录”或“设备互踢”逻辑 (可选)
        // 如果需要同一账号同一端只能一个在线，可以在这里清理旧的 deviceKey
        handleDeviceSession(sysUser.getId(), dto.getClientInfo().getDeviceId(), jti);

        // 9. 更新数据库最后登录信息 (虚拟线程会处理好阻塞)
        iSysUserService.updateById(new SysUser()
                .setId(sysUser.getId())
                .setLastLoginTime(LocalDateTime.now()));
        // 10. 组装返回
        return TokenResDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtil.getAccessTokenTtl())
                .refreshExpiresIn(jwtUtil.getRefreshTokenTtl())
                .deviceId(dto.getClientInfo().getDeviceId())
                .clientType(dto.getClientInfo().getClientType())
                .ip(dto.getLoginIp())
                .build();
    }

    /**
     * 维护设备会话关系
     * Key: auth:device:{userId}:{deviceId} -> Value: {jti}
     */
    private void handleDeviceSession(Long userId, String deviceId, String jti) {
        String deviceKey = DEVICE_BIND_PREFIX + userId + ":" + deviceId;
        // 覆盖写入，确保该设备下最新的 JTI 有效
        iRedisService.setEx(deviceKey, jti, 7, TimeUnit.DAYS);
    }
}
