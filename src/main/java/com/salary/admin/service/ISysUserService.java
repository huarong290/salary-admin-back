package com.salary.admin.service;

import com.salary.admin.model.dto.user.UserInfoDTO;
import com.salary.admin.model.entity.sys.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.NotBlank;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
public interface ISysUserService extends IService<SysUser> {
    /**
     * 根据用户名获取用户基本信息
     *
     * @param username 用户名
     * @return 用户对象
     */
    SysUser selectUserByUsername(String username);
    /**
     * 根据用户id获取用户基本信息
     *
     * @param userId 用户名
     * @return 用户信息聚合对象
     */
    UserInfoDTO getUserInfoAggregation(Long userId);
}
