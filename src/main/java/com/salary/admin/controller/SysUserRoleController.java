package com.salary.admin.controller;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.model.dto.userrole.UserRoleAssignReqDTO;
import com.salary.admin.service.ISysUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户与角色关联表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@RestController
@RequestMapping("/api/userrole")
@Tag(name = "用户分配角色", description = "用户与角色中间表管理")
public class SysUserRoleController {


    @Autowired
    private ISysUserRoleService iSysUserRoleService;
    // ======================== 5. 权限分配操作 ========================

    @PostMapping("/assignRoles")
    @Operation(summary = "给用户分配角色 (覆盖旧角色)")
    @Loggable(title = "用户管理-分配角色", logRequest = true)
    public ApiResult<Void> assignRoles(@Validated @RequestBody UserRoleAssignReqDTO reqDTO) {
        iSysUserRoleService.assignRolesToUser(reqDTO);
        // 分配操作没有实质的数据体需要返回，直接返回成功状态即可
        return ApiResult.defaultSuccessResult();
    }
}
