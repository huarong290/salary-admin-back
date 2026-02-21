package com.salary.admin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.ApiResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证失败处理器
 * - Token 无效 / 未登录 / Token 过期
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResult<?> result = ApiResult.failResult(ApiResultCode.UNAUTHORIZED, ApiResultCode.UNAUTHORIZED.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
