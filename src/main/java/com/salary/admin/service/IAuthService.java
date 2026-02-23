package com.salary.admin.service;

import com.salary.admin.model.dto.TokenResDTO;
import com.salary.admin.model.dto.UserLoginReqDTO;

import java.util.Collection;

/**
 * 认证服务接口
 * <p>
 * 提供登录、登出、刷新 Token、获取用户信息等功能
 */
public interface IAuthService {
    /**
     * 用户登录
     *
     * @param dto 用户登录请求参数
     * @return TokenResDTO 包含 AccessToken、RefreshToken 等信息
     */
    TokenResDTO login(UserLoginReqDTO dto);

    /**
     * 刷新 Token (安全增强版)
     * 逻辑：令牌轮转 + 复用检测 + 设备绑定校验
     * @param oldRefreshToken 旧的刷新token
     * @param deviceId 设备号
     * @param currentIp 当前ip
     */
     TokenResDTO refreshToken(String oldRefreshToken, String deviceId, String currentIp);

    /**
     * 清除指定用户的权限缓存
     * @param userId 用户ID
     */
    void clearUserPermissionsCache(Long userId);

    /**
     * 批量清除多个用户的权限缓存
     * @param userIds 用户ID集合
     */
    void clearUserPermissionsCache(Collection<Long> userIds);
}
