package com.salary.admin.mapper.ext;

import com.salary.admin.mapper.auto.SysMenuMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * 根据用户ID查询所有去重后的权限标识码
     */
    Set<String> selectPermissionsByUserId(@Param("userId") Long userId);
}
