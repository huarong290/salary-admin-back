package com.salary.admin.controller;


import cn.hutool.extra.servlet.ServletUtil;
import com.salary.admin.annotation.Logable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.model.dto.TokenRefreshReqDTO;
import com.salary.admin.model.dto.TokenResDTO;
import com.salary.admin.model.dto.UserLoginReqDTO;
import com.salary.admin.service.IAuthService;
import com.salary.admin.utils.IpUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、Token管理及用户信息查询")
public class AuthController {

    @Autowired
    private IAuthService iAuthService;

    @PostMapping("/login")
    @Logable(title = "用户登录")
    public ApiResult<TokenResDTO> login(@Validated @RequestBody UserLoginReqDTO loginDto, HttpServletRequest request) {
        // 1. 手动注入由后端控制的属性
        loginDto.setLoginIp(IpUtils.getClientIp(request));

        // 2. 调用自定义 AuthService
        return ApiResult.successResult(iAuthService.login(loginDto));
    }

    @PostMapping("/refresh")
    @Logable(title = "令牌续期", logResponse = false)
    public ApiResult<TokenResDTO> refresh(@Validated @RequestBody TokenRefreshReqDTO refreshDto, HttpServletRequest request) {
        // currentIp 必须由后端提取，不能信任前端传参
        String currentIp = IpUtils.getClientIp(request);

        return ApiResult.successResult(iAuthService.refreshToken(
                refreshDto.getRefreshToken(),
                refreshDto.getDeviceId(),
                currentIp
        ));
    }
}
