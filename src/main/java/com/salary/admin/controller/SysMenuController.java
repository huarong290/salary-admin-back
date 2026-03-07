package com.salary.admin.controller;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.model.dto.menu.MenuAddReqDTO;
import com.salary.admin.model.dto.menu.MenuEditReqDTO;
import com.salary.admin.model.entity.sys.SysMenu;
import com.salary.admin.model.vo.menu.MenuTreeVO;
import com.salary.admin.service.ISysMenuService;
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
 * 权限菜单表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-02-27
 */
@RestController
@RequestMapping("/api/menu")
@Tag(name = "菜单管理", description = "系统菜单树及权限按钮的增删改查")
@Slf4j
public class SysMenuController {

    @Autowired
    private ISysMenuService iSysMenuService;

    // ======================== 1. 查询操作 (Read) ========================

    @GetMapping("/tree")
    @Operation(summary = "获取全量菜单树形列表", description = "用于菜单管理页面的树形表格展示，或分配权限时的树形复选框")
    @Loggable(title = "菜单管理-获取菜单树")
    public ApiResult<List<MenuTreeVO>> getMenuTree() {
        // 1. 获取所有菜单 (这里可以根据需求加入排序和状态过滤)
        List<SysMenu> allMenus = iSysMenuService.list();

        // 2. 转换为带有 Meta 信息的嵌套树形结构
        List<MenuTreeVO> treeList = iSysMenuService.buildMenuTree(allMenus);

        return ApiResult.successResult(treeList);
    }


    // ======================== 2. 新增操作 (Create) ========================

    @PostMapping("/add")
    @Operation(summary = "新增菜单/按钮")
    @Loggable(title = "菜单管理-新增菜单", logRequest = true)
    public ApiResult<Long> addMenu(@Validated @RequestBody MenuAddReqDTO reqDTO) {
        Long newMenuId = iSysMenuService.addMenu(reqDTO);
        return ApiResult.successResult(newMenuId);
    }


    // ======================== 3. 修改操作 (Update) ========================

    @PutMapping("/edit")
    @Operation(summary = "修改菜单/按钮信息")
    @Loggable(title = "菜单管理-修改菜单", logRequest = true)
    public ApiResult<Integer> editMenu(@Validated @RequestBody MenuEditReqDTO reqDTO) {
        Integer result = iSysMenuService.editMenu(reqDTO);
        return ApiResult.successResult(result);
    }


    // ======================== 4. 删除操作 (Delete) ========================

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除菜单/按钮")
    @Loggable(title = "菜单管理-单条删除")
    public ApiResult<Boolean> deleteMenuById(
            @Parameter(description = "菜单主键ID", required = true) @PathVariable("id") Long id) {
        boolean result = iSysMenuService.deleteMenuById(id);
        return ApiResult.successResult(result);
    }

}
