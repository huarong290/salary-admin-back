package com.salary.admin.controller;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.model.dto.user.UserAddReqDTO;
import com.salary.admin.model.dto.user.UserEditReqDTO;
import com.salary.admin.model.dto.user.UserInfoDTO;
import com.salary.admin.service.ISysUserService;
import com.salary.admin.utils.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/userInfo")
    @Operation(summary = "获取当前登录用户信息(聚合接口)")
    @Loggable(title = "当前登录用户信息")
    public ApiResult<UserInfoDTO> getCurrentUserInfo() {
        // 从当前上下文（ThreadLocal）获取 userId，这是你之前写好的基建
        Long userId = UserContextUtil.getUserId();
        return ApiResult.successResult(iSysUserService.getUserInfoAggregation(userId));
    }
    @PostMapping("/add")
    @Operation(summary = "新增用户")
    @Loggable(title = "用户管理-新增用户")
    public ApiResult<Long> addUser(@Validated @RequestBody UserAddReqDTO reqDTO) {
        // 返回新生成的 ID 给前端，前端直呼内行！
        Long newUserId = iSysUserService.addUser(reqDTO);
        return ApiResult.successResult(newUserId);
    }

    @PutMapping("/edit")
    @Operation(summary = "修改用户")
    @Loggable(title = "用户管理-修改用户")
    public ApiResult<Integer> editUser(@Validated @RequestBody UserEditReqDTO reqDTO) {
        Integer result=iSysUserService.editUser(reqDTO);
        // 修改操作前端通常不需要返回值，弹个成功的 Toast 就行了
        return ApiResult.successResult(result);
    }
}
