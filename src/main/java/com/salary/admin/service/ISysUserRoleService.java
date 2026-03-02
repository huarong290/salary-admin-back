package com.salary.admin.service;

import com.salary.admin.model.dto.userrole.UserRoleAssignReqDTO;
import com.salary.admin.model.entity.sys.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户与角色关联表 服务类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

    // ======================== 1. 核心分配操作 ========================

    /**
     * 给用户分配角色 (全量覆盖模式)
     * <p>
     * 逻辑说明：先物理删除该用户之前绑定的所有角色，然后批量插入新的角色关联关系。
     * </p>
     *
     * @param reqDTO 分配角色请求参数 (包含用户ID和角色ID列表)
     */
    void assignRolesToUser(UserRoleAssignReqDTO reqDTO);
}
