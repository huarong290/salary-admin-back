package com.salary.admin.mapper.ext;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.admin.mapper.auto.SysUserMapper;
import com.salary.admin.model.dto.user.UserQueryReqDTO;
import com.salary.admin.model.entity.sys.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 物理删除用户（直接删除记录）
     * @param id 用户ID
     * @return 影响行数
     */
    int physicalDeleteUserById(@Param("id") Long id);

    /**
     * 批量物理删除用户
     * @param ids 用户ID集合
     * @return 影响行数
     */
    int batchPhysicalDeleteUserByIds(@Param("ids") List<Long> ids);

    /**
     * 分页查询用户列表
     * @param page 分页对象（MyBatis Plus 提供）
     * @param reqDTO 查询条件
     * @return 分页结果
     */
    Page<SysUser> selectUserListByPage(Page<SysUser> page, @Param("reqDTO") UserQueryReqDTO reqDTO);

}

