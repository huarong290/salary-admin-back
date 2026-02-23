package com.salary.admin.filter;


import com.salary.admin.constants.redis.RedisCacheConstants;
import com.salary.admin.constants.security.JwtConstants;
import com.salary.admin.exception.JwtAuthenticationException;
import com.salary.admin.service.IRedisService;
import com.salary.admin.service.ISysMenuService;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    private final ISysMenuService iSysMenuService;

    private final IRedisService iRedisService;

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
        try {
            // 3.解析并初步校验签名/过期
            Claims claims = jwtUtil.parseToken(token);
            String userId = claims.get("userId", String.class);
            String jti = claims.get("jti", String.class);
            String username = claims.getSubject();
            // 4.校验 token 类型(确保不能用 refresh_token 来访问接口)
            String type = claims.get(JwtConstants.CLAIM_TOKEN_TYPE, String.class);
            if (!JwtConstants.TOKEN_TYPE_ACCESS.equals(type)) {
                throw new JwtAuthenticationException("非法 Token 类型");
            }

            // 5.校验是否过期
            if (claims.getExpiration().before(new Date())) {
                throw new JwtAuthenticationException("Token 已过期");
            }
            //6.校验活跃状态 (实现全端挤兑)

            if (StringUtils.isNotBlank(userId)) {
                String activeJti = redisTemplate.opsForValue().get(RedisCacheConstants.AUTH_USER_ACTIVE + userId);
                // 如果活跃 JTI 存在且不等于当前 JTI，说明该账号在别处登录了
                if (activeJti != null && !activeJti.equals(jti)) {
                    throw new JwtAuthenticationException("账号已在其他设备登录");
                }
            }
            //7.校验黑名单 (手动注销场景)
            Boolean isBlacklisted = redisTemplate.hasKey(RedisCacheConstants.AUTH_TOKEN_BLACKLIST + jti);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                throw new JwtAuthenticationException("Token 已失效，请重新登录");
            }

            // 8. 【补全】注入 Security 上下文

            if (StringUtils.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String permKey = RedisCacheConstants.AUTH_USER_PERMISSIONS + userId;
                Set<String> permissions = iRedisService.get(permKey, Set.class);
                if(permissions == null){
                    log.info("用户 {} 权限缓存失效，正在重新加载...", username);
                    permissions = iSysMenuService.getPermissionsByUserId(Long.valueOf(userId));
                    if (permissions != null) {
                        iRedisService.setEx(permKey, permissions, 7, TimeUnit.DAYS);
                    }
                }
                List<SimpleGrantedAuthority> authorities = permissions.stream() .map(SimpleGrantedAuthority::new) .toList();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 关键点：这一步决定了后面的接口能不能拿到用户信息
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("用户 {} 认证成功，权限：{}", username, authorities);
            }
        } catch (JwtAuthenticationException e) {
            log.warn("JWT 认证拦截: {} -> URL: {}", e.getMessage(), request.getRequestURI());
            SecurityContextHolder.clearContext();
            //  关键：将消息存入 request,供 EntryPoint 读取
            request.setAttribute("jwt_exception_msg", e.getMessage());
        } catch (Exception e) {
            log.error("安全过滤器未知异常", e);
            SecurityContextHolder.clearContext();
            //  关键：将消息存入 request
            request.setAttribute("jwt_exception_msg", "系统安全校验异常");
        }
        filterChain.doFilter(request, response);
    }
}