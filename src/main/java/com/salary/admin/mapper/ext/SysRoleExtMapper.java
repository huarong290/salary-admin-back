package com.salary.admin.mapper.ext;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.admin.mapper.auto.SysRoleMapper;
import com.salary.admin.model.dto.role.RoleQueryReqDTO;
import com.salary.admin.model.entity.sys.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
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

    // ==================== 1. 多表关联查询 ====================
    /**
     * 根据用户 ID 查询其拥有的角色标识集合。
     *
     * @param userId 用户的唯一标识（主键 ID）
     * @return 角色标识集合，例如 ["ADMIN", "USER", "MANAGER"]
     */
    Set<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    // ==================== 2. 单表增强操作 ====================
    /**
     * 校验角色编码是否唯一
     *
     * @param roleCode  角色编码
     * @param excludeId 需要排除的角色ID (修改时使用，新增传 null)
     * @return 1 表示存在，null 表示不存在
     */
    Integer checkRoleCodeUnique(@Param("roleCode") String roleCode, @Param("excludeId") Long excludeId);
    /**
     * 分页查询角色列表 (带动态条件)
     *
     * @param page   分页对象（MyBatis Plus 提供）
     * @param reqDTO 查询条件
     * @return 分页结果
     */
    Page<SysRole> selectRoleListByPage(Page<SysRole> page, @Param("reqDTO") RoleQueryReqDTO reqDTO);

    /**
     * 获取所有状态正常的角色
     * (供前端在新建/编辑用户时，下拉框勾选分配角色使用)
     *
     * @return 角色列表
     */
    List<SysRole> selectAllNormalRoles();

    /**
     * 物理删除角色（直接删除记录）
     *
     * @param id 角色ID
     * @return 影响行数
     */
    int physicalDeleteRoleById(@Param("id") Long id);

    /**
     * 批量物理删除角色
     *
     * @param ids 角色ID集合
     * @return 影响行数
     */
    int batchPhysicalDeleteRoleByIds(@Param("ids") List<Long> ids);
}


