package com.salary.admin.mapper.ext;

import com.salary.admin.mapper.auto.SysMenuMapper;
import com.salary.admin.model.entity.sys.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 菜单表 Mapper Ext接口
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysMenuExtMapper extends SysMenuMapper {

    /**
     * 根据用户 ID 查询所有去重后的权限标识码
     *
     * @param userId 用户的唯一标识（主键 ID）
     * @return 去重后的权限标识码集合，例如 ["sys:user:add", "sys:user:delete", "sys:menu:view"]
     */
    Set<String> selectPermissionsByUserId(@Param("userId") Long userId);
    /**
     * 根据用户ID查询其可访问的菜单列表
     *
     * @param userId 用户的唯一标识（主键 ID）
     * @return 菜单实体列表，每个元素为 {@link SysMenu} 对象，包含菜单的基本信息
     */
    List<SysMenu> selectMenuByUserId(@Param("userId") Long userId);
}


