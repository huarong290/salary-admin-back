package com.salary.admin.filter;


import com.salary.admin.constants.security.JwtConstants;
import com.salary.admin.exception.JwtAuthenticationException;
import com.salary.admin.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

/**
 * JWT 认证过滤器
 * 继承 OncePerRequestFilter 确保每个请求只走一次过滤逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1.提取 Token (直接使用 static 方法，减少实例依赖)
        String token = JwtUtil.extractBearerToken(request.getHeader(JwtConstants.JWT_HEADER));
        // 2.空Token 直接放行，交给 SecurityConfig 决定是否拦截
        if (StringUtils.isBlank(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        try{
            // 3.解析并初步校验签名/过期
            Claims claims = jwtUtil.parseToken(token);
            // 4.校验 token 类型(确保不能用 refresh_token 来访问接口)
            if (!JwtConstants.TOKEN_TYPE_ACCESS.equals(claims.get(JwtConstants.CLAIM_TOKEN_TYPE, String.class))) {
                throw new JwtAuthenticationException("非法 Token 类型");
            }

            // 5.校验是否过期
            if (claims.getExpiration().before(new Date())) {
                throw new JwtAuthenticationException("Token 已过期");
            }
            // 6 校验 jti 是否拉黑
            String jti = claims.get("jti", String.class);
            Boolean isBlacklisted = redisTemplate.hasKey(JwtConstants.JTI_BLACKLIST_PREFIX + jti);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                throw new JwtAuthenticationException("Token 已失效，请重新登录");
            }

            // 7. 【补全】注入 Security 上下文
            String username = claims.getSubject();
            if (StringUtils.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 后续 RBAC 完善后，这里应传入从数据库/缓存读取的真实权限 Authorities
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.emptyList()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 关键点：这一步决定了后面的接口能不能拿到用户信息
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("用户 {} 认证成功", username);
            }
        }catch (JwtAuthenticationException e) {
            log.warn("JWT 校验失败: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            // 注意：这里不要 throw，也不要直接 doFilter
            // 如果你希望由 EntryPoint 返回标准 JSON，直接继续 filterChain 即可
        } catch (Exception e) {
            log.error("安全过滤器未知异常", e);
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}