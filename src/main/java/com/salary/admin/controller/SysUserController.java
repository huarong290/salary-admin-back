package com.salary.admin.controller;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.user.*;
import com.salary.admin.model.vo.user.SysUserVO;
import com.salary.admin.service.ISysUserService;
import com.salary.admin.utils.UserContextUtil;
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
 * 用户表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户增删改查接口")
@Slf4j
public class SysUserController {

    @Autowired
    private ISysUserService iSysUserService;
    // ======================== 1. 新增操作 (Create) ========================
    @PostMapping("/add")
    @Operation(summary = "新增用户")
    @Loggable(title = "用户管理-新增用户")
    public ApiResult<Long> addUser(@Validated @RequestBody UserAddReqDTO reqDTO) {
        // 返回新生成的 ID 给前端，前端直呼内行！
        Long newUserId = iSysUserService.addUser(reqDTO);
        return ApiResult.successResult(newUserId);
    }
    // ======================== 2. 删除操作 (Delete) ========================

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "单条删除用户")
    @Loggable(title = "用户管理-单条删除")
    public ApiResult<Boolean> deleteUserById(
            @Parameter(description = "用户ID", required = true) @PathVariable("id") Long id,
            @Parameter(description = "是否逻辑删除(默认true)") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        boolean result = iSysUserService.deleteUserById(id, logicalDelete);
        return ApiResult.successResult(result);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "批量删除用户")
    @Loggable(title = "用户管理-批量删除")
    public ApiResult<Boolean> deleteUserByIds(
            @Parameter(description = "用户ID列表", required = true) @RequestBody List<Long> ids,
            @Parameter(description = "是否逻辑删除(默认true)") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        boolean result = iSysUserService.deleteUserByIds(ids, logicalDelete);
        return ApiResult.successResult(result);
    }
    // ======================== 3. 修改操作 (Update) ========================
    @PutMapping("/edit")
    @Operation(summary = "修改用户基本信息")
    @Loggable(title = "用户管理-修改用户")
    public ApiResult<Integer> editUser(@Validated @RequestBody UserEditReqDTO reqDTO) {
        Integer result = iSysUserService.editUser(reqDTO);
        return ApiResult.successResult(result);
    }

    @PutMapping("/resetPwd")
    @Operation(summary = "重置用户密码")
    @Loggable(title = "用户管理-重置密码")
    public ApiResult<Boolean> resetPwd(@Validated @RequestBody UserResetPwdReqDTO reqDTO) {
        boolean result = iSysUserService.resetUserPwd(reqDTO);
        return ApiResult.successResult(result);
    }
    // ======================== 4. 查询操作 (Read) ========================
    @PostMapping("/page")
    @Operation(summary = "分页查询用户列表")
    @Loggable(title = "用户管理-分页查询")
    // 💡 注意：复杂查询通常包含多个参数，推荐用 POST 搭配 @RequestBody，比 GET 拼 URL 参数更优雅
    public ApiResult<PageResult<SysUserVO>> getUserPage(@RequestBody UserQueryReqDTO reqDTO) {
        PageResult<SysUserVO> pageResult = iSysUserService.selectUserListByPage(reqDTO);
        return ApiResult.successResult(pageResult);
    }
    @GetMapping("/userInfo")
    @Operation(summary = "获取当前登录用户信息(聚合接口)")
    @Loggable(title = "当前登录用户信息")
    public ApiResult<UserInfoDTO> getCurrentUserInfo() {
        // 从当前上下文（ThreadLocal）获取 userId，这是你之前写好的基建
        Long userId = UserContextUtil.getUserId();
        return ApiResult.successResult(iSysUserService.selectUserInfoAggregation(userId));
    }



}
