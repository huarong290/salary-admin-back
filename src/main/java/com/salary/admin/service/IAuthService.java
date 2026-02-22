package com.salary.admin.service;

import com.salary.admin.model.dto.TokenResDTO;
import com.salary.admin.model.dto.UserLoginReqDTO;

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
}
