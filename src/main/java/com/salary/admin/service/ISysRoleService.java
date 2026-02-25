package com.salary.admin.service;

import com.salary.admin.model.entity.sys.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 根据用户 ID 获取其拥有的角色编码集合
     * @param userId 用户 ID
     * @return 角色编码集合
     */
    Set<String> getRoleCodesByUserId(Long userId);

}
