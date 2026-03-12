package com.salary.admin.mapper.ext;

import com.salary.admin.mapper.auto.SysUserRoleMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户-角色关联表 Mapper Ext接口
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysUserRoleExtMapper extends SysUserRoleMapper {

    /**
     * 根据角色ID查询所有关联的用户ID
     *
     * @param roleId 角色ID
     * @return 用户ID集合
     */
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);
}

