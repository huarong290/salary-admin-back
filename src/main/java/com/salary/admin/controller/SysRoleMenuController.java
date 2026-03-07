package com.salary.admin.controller;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.model.dto.rolemenu.RoleMenuAssignReqDTO;
import com.salary.admin.service.ISysRoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色与菜单关联表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@RestController
@RequestMapping("/api/rolemenu")
@Tag(name = "角色分配权限", description = "角色与菜单(权限)关联管理")
@Slf4j
public class SysRoleMenuController {
    @Autowired
    private ISysRoleMenuService iSysRoleMenuService;

    @GetMapping("/listMenuIds/{roleId}")
    @Operation(summary = "获取角色拥有的菜单ID列表", description = "用于打开'分配权限'弹窗时，默认勾选已有的菜单树节点")
    public ApiResult<List<Long>> getMenuIdsByRoleId(
            @Parameter(description = "角色主键ID", required = true) @PathVariable("roleId") Long roleId) {
        List<Long> menuIds = iSysRoleMenuService.getMenuIdsByRoleId(roleId);
        return ApiResult.successResult(menuIds);
    }

    @PostMapping("/assignMenus")
    @Operation(summary = "给角色分配菜单 (覆盖旧权限)")
    @Loggable(title = "角色管理-分配菜单权限", logRequest = true)
    public ApiResult<Void> assignMenus(@Validated @RequestBody RoleMenuAssignReqDTO reqDTO) {
        iSysRoleMenuService.assignMenusToRole(reqDTO);
        return ApiResult.defaultSuccessResult();
    }
}
