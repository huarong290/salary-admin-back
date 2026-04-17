package com.salary.admin.service;

import com.salary.admin.model.dto.LoginResultDTO;
import com.salary.admin.model.dto.MfaVerifyReqDTO;
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
     * 用户登录 (第一阶段)
     * 逻辑：校验账号密码。若需要MFA，下发临时Token；若不需要，直接下发真实Token。
     *
     * @param dto 用户登录请求参数
     * @return LoginResultDTO 包含是否需要MFA的标志及相应Token
     */
    LoginResultDTO login(UserLoginReqDTO dto);
    /**
     * MFA 二次验证 (第二阶段)
     * 逻辑：校验临时Token和动态口令，通过后颁发真实的 AccessToken 和会话
     *
     * @param verifyDto MFA验证请求参数
     * @param currentIp 当前IP
     * @return TokenResDTO 真实的授权令牌
     */
    TokenResDTO verifyMfa(MfaVerifyReqDTO verifyDto, String currentIp);
    /**
     * 刷新 Token (安全增强版)
     * 逻辑：令牌轮转 + 复用检测 + 设备绑定校验
     * @param oldRefreshToken 旧的刷新token
     * @param deviceId 设备号
     * @param currentIp 当前ip
     */
     TokenResDTO refreshToken(String oldRefreshToken, String deviceId, String currentIp);
    /**
     * 用户登出
     * 逻辑：销毁当前 AccessToken (加入黑名单) 并删除关联的 RefreshToken
     */
    void logout();
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
