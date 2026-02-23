package com.salary.admin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.ApiResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 权限不足处理器
 * - 角色不匹配 / 禁止访问
 */
@Component
@RequiredArgsConstructor // 推荐构造器注入
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    // 注入容器中的 ObjectMapper
    private final ObjectMapper objectMapper;
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {


        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ApiResult<?> result = ApiResult.failResult(ApiResultCode.FORBIDDEN, ApiResultCode.FORBIDDEN.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

