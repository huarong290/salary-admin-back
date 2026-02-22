package com.salary.admin.mapper.ext;

import com.salary.admin.mapper.auto.SysUserMapper;
import com.salary.admin.model.entity.sys.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 系统用户表 Mapper Ext接口
 * </p>
 *
 * @author system
 * @since 2026-01-18
 */
@Mapper
public interface SysUserExtMapper extends SysUserMapper {
    /**
     * 根据用户名查询系统用户
     * @param username 用户名
     * @return 实体对象
     */
    SysUser selectUserByUsername(@Param("username") String username);
}