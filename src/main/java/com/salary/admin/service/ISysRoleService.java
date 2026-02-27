package com.salary.admin.service;

import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.role.RoleAddReqDTO;
import com.salary.admin.model.dto.role.RoleEditReqDTO;
import com.salary.admin.model.dto.role.RoleQueryReqDTO;
import com.salary.admin.model.entity.sys.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.model.vo.role.SysRoleVO;

import java.util.List;
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

// ======================== 1. 新增操作 (Create) ========================

    /**
     * 新增角色
     *
     * @param reqDTO 新增角色请求参数
     * @return 新生成的主键 ID
     */
    Long addRole(RoleAddReqDTO reqDTO);


    // ======================== 2. 删除操作 (Delete) ========================

    /**
     * 删除角色 (支持逻辑/物理删除)
     *
     * @param id 角色主键 ID
     * @param logicalDelete 是否逻辑删除
     * true  = 逻辑删除（delete_flag = 1）
     * false = 物理删除（DELETE）
     * @return 是否删除成功
     */
    boolean deleteRoleById(Long id, boolean logicalDelete);

    /**
     * 删除角色 (支持批量删除，多个 ID 用逗号隔开)
     *
     * @param ids 角色 ID 列表
     * @param logicalDelete 是否逻辑删除
     * true  = 逻辑删除（delete_flag = 1）
     * false = 物理删除（DELETE）
     * @return 是否成功
     */
    boolean deleteRoleByIds(List<Long> ids, boolean logicalDelete);


    // ======================== 3. 修改操作 (Update) ========================

    /**
     * 修改角色
     *
     * @param reqDTO 编辑角色请求参数
     * @return 成功修改的记录数
     */
    Integer editRole(RoleEditReqDTO reqDTO);


    // ======================== 4. 查询操作 (Read) ========================

    /**
     * 分页查询角色列表 (支持多条件模糊查询)
     *
     * @param reqDTO 查询条件参数
     * @return 角色分页结果对象
     */
    PageResult<SysRoleVO> selectRoleListByPage(RoleQueryReqDTO reqDTO);

    /**
     * 获取所有状态正常的角色
     * (供前端在新建/编辑用户时，下拉框勾选分配角色使用)
     *
     * @return 角色视图对象列表
     */
    List<SysRoleVO> selectAllNormalRoles();

    /**
     * 根据用户 ID 获取其拥有的角色编码集合
     * (底层需联查 sys_user_role 表，用于 Spring Security 鉴权和前端动态路由)
     *
     * @param userId 用户 ID
     * @return 角色编码集合
     */
    Set<String> selectRoleCodesByUserId(Long userId);

}
