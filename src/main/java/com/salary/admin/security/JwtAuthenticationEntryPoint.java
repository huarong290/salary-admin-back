package com.salary.admin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.ApiResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证失败处理器
 * - Token 无效 / 未登录 / Token 过期
 */
@Component
@RequiredArgsConstructor // 推荐构造器注入
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 注入容器中的 ObjectMapper
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 尝试获取 Filter 传递的精准异常消息
        String customMsg = (String) request.getAttribute("jwt_exception_msg");
        String finalMsg = StringUtils.isNotBlank(customMsg) ? customMsg : ApiResultCode.UNAUTHORIZED.getMessage();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResult<?> result = ApiResult.failResult(ApiResultCode.UNAUTHORIZED, finalMsg);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
