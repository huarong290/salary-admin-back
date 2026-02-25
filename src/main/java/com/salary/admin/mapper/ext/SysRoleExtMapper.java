package com.salary.admin.mapper.ext;

import com.salary.admin.mapper.auto.SysRoleMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * <p>
 * 角色表 Mapper Ext接口
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysRoleExtMapper extends SysRoleMapper {

    /**
     * 根据用户 ID 查询其拥有的角色标识集合。
     *
     * @param userId 用户的唯一标识（主键 ID）
     * @return 角色标识集合，例如 ["ADMIN", "USER", "MANAGER"]
     */
    Set<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}


