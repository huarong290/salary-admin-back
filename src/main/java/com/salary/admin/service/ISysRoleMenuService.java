package com.salary.admin.service;

import com.salary.admin.model.dto.rolemenu.RoleMenuAssignReqDTO;
import com.salary.admin.model.entity.sys.SysRoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色与菜单关联表 服务类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
public interface ISysRoleMenuService extends IService<SysRoleMenu> {
    /**
     * 给角色分配菜单权限 (全量覆盖模式)
     */
    void assignMenusToRole(RoleMenuAssignReqDTO reqDTO);

    /**
     * 获取某个角色当前关联的所有菜单ID
     * (用于前端树形组件的默认勾选回显)
     */
    List<Long> getMenuIdsByRoleId(Long roleId);
}
