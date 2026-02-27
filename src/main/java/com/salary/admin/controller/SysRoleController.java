package com.salary.admin.controller;


import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.role.RoleAddReqDTO;
import com.salary.admin.model.dto.role.RoleEditReqDTO;
import com.salary.admin.model.dto.role.RoleQueryReqDTO;
import com.salary.admin.model.vo.role.SysRoleVO;
import com.salary.admin.service.ISysRoleService;
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
 * 角色表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-02-27
 */
@RestController
@RequestMapping("/api/role")
@Tag(name = "角色管理", description = "角色增删改查及分配接口")
@Slf4j
public class SysRoleController {

    @Autowired
    private ISysRoleService iSysRoleService;

    // ======================== 1. 新增操作 (Create) ========================

    @PostMapping("/add")
    @Operation(summary = "新增角色")
    @Loggable(title = "角色管理-新增角色")
    public ApiResult<Long> addRole(@Validated @RequestBody RoleAddReqDTO reqDTO) {
        Long newRoleId = iSysRoleService.addRole(reqDTO);
        return ApiResult.successResult(newRoleId);
    }


    // ======================== 2. 删除操作 (Delete) ========================

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "单条删除角色")
    @Loggable(title = "角色管理-单条删除")
    public ApiResult<Boolean> deleteRoleById(
            @Parameter(description = "角色ID", required = true) @PathVariable("id") Long id,
            @Parameter(description = "是否逻辑删除(默认true)") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        boolean result = iSysRoleService.deleteRoleById(id, logicalDelete);
        return ApiResult.successResult(result);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "批量删除角色")
    @Loggable(title = "角色管理-批量删除")
    public ApiResult<Boolean> deleteRoleByIds(
            @Parameter(description = "角色ID列表", required = true) @RequestBody List<Long> ids,
            @Parameter(description = "是否逻辑删除(默认true)") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        boolean result = iSysRoleService.deleteRoleByIds(ids, logicalDelete);
        return ApiResult.successResult(result);
    }


    // ======================== 3. 修改操作 (Update) ========================

    @PutMapping("/edit")
    @Operation(summary = "修改角色基本信息")
    @Loggable(title = "角色管理-修改角色")
    public ApiResult<Integer> editRole(@Validated @RequestBody RoleEditReqDTO reqDTO) {
        Integer result = iSysRoleService.editRole(reqDTO);
        return ApiResult.successResult(result);
    }


    // ======================== 4. 查询操作 (Read) ========================

    @PostMapping("/page")
    @Operation(summary = "分页查询角色列表")
    @Loggable(title = "角色管理-分页查询")
    public ApiResult<PageResult<SysRoleVO>> getRolePage(@RequestBody RoleQueryReqDTO reqDTO) {
        PageResult<SysRoleVO> pageResult = iSysRoleService.selectRoleListByPage(reqDTO);
        return ApiResult.successResult(pageResult);
    }

    @GetMapping("/listAll")
    @Operation(summary = "获取所有正常状态角色 (下拉框使用)")
    @Loggable(title = "角色管理-获取全部正常角色")
    public ApiResult<List<SysRoleVO>> listAllNormalRoles() {
        // 这个接口通常给前端在新建用户时，渲染"分配角色"的下拉复选框使用
        List<SysRoleVO> roleList = iSysRoleService.selectAllNormalRoles();
        return ApiResult.successResult(roleList);
    }
}
