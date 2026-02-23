package com.salary.admin.service;

import com.salary.admin.model.entity.sys.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

/**
 * <p>
 * 权限菜单表 服务类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 根据用户ID获取权限标识集合
     */
    Set<String> getPermissionsByUserId(Long userId);
}
