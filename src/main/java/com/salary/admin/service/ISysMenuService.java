package com.salary.admin.service;

import com.salary.admin.model.entity.sys.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
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
     * 根据用户 ID 查询所有去重后的权限标识码
     *
     * @param userId 用户的唯一标识（主键 ID）
     * @return 去重后的权限标识码集合，例如 ["sys:user:add", "sys:user:delete", "sys:menu:view"]
     */
    Set<String> selectPermissionsByUserId(Long userId);
    /**
     * 根据用户ID查询其可访问的菜单列表
     *
     * @param userId 用户的唯一标识（主键 ID）
     * @return 菜单实体列表，每个元素为 {@link SysMenu} 对象，包含菜单的基本信息
     */
    List<SysMenu> selectMenuByUserId(Long userId);
}
