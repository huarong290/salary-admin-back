package com.salary.admin.controller;


import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.model.dto.TokenRefreshReqDTO;
import com.salary.admin.model.dto.TokenResDTO;
import com.salary.admin.model.dto.UserLoginReqDTO;
import com.salary.admin.model.dto.captcha.CaptchaResDTO;
import com.salary.admin.service.IAuthService;
import com.salary.admin.service.ICaptchaService;
import com.salary.admin.utils.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、Token管理及用户信息查询")
public class AuthController {

    @Autowired
    private IAuthService iAuthService;
    @Autowired
    private ICaptchaService iCaptchaService;

    @GetMapping("/captcha")
    @Operation(summary = "获取图形验证码")
    @Loggable(title = "获取图形验证码", logResponse = false)
    public ApiResult<CaptchaResDTO> getCaptcha() {

        return ApiResult.successResult(iCaptchaService.generateCaptcha());
    }
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    @Loggable(title = "用户登录")
    public ApiResult<TokenResDTO> login(@Validated @RequestBody UserLoginReqDTO loginDto, HttpServletRequest request) {
        // 1. 手动注入由后端控制的属性
        loginDto.setLoginIp(IpUtils.getClientIp(request));

        // 2. 调用自定义 AuthService
        return ApiResult.successResult(iAuthService.login(loginDto));
    }

    @PostMapping("/refresh")
    @Operation(summary = "令牌续期")
    @Loggable(title = "令牌续期", logResponse = false)
    public ApiResult<TokenResDTO> refresh(@Validated @RequestBody TokenRefreshReqDTO refreshDto, HttpServletRequest request) {
        // currentIp 必须由后端提取，不能信任前端传参
        String currentIp = IpUtils.getClientIp(request);

        return ApiResult.successResult(iAuthService.refreshToken(
                refreshDto.getRefreshToken(),
                refreshDto.getDeviceId(),
                currentIp
        ));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    @Loggable(title = "用户登出")
    public ApiResult<Void> logout() {
        // 1. 从请求头获取当前 Token (或通过 SecurityContext 获取)
        // 2. 调用 AuthService 执行逻辑：
        //    - 使当前的 AccessToken 失效
        //    - 删除 Redis/DB 中的 RefreshToken
        iAuthService.logout();
        return ApiResult.successResult(null);
    }
}
