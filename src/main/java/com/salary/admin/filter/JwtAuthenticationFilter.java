package com.salary.admin.filter;

import com.salary.admin.constants.redis.RedisCacheConstants;
import com.salary.admin.constants.role.RoleConstants;
import com.salary.admin.constants.security.JwtConstants;
import com.salary.admin.exception.JwtAuthenticationException;
import com.salary.admin.model.dto.LoginUserDTO;
import com.salary.admin.property.SecurityWhiteListProperties;
import com.salary.admin.security.JwtAuthenticationEntryPoint;
import com.salary.admin.service.IRedisService;
import com.salary.admin.service.ISysMenuService;
import com.salary.admin.service.ISysRoleService;
import com.salary.admin.utils.JwtUtil;
import com.salary.admin.utils.UserContextUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * JWT 认证过滤器
 *
 * <p>
 * 核心职责：
 * </p>
 *
 * 1 解析 AccessToken
 * 2 校验 Token 合法性
 * 3 Redis 会话校验（防止账号被挤下线）
 * 4 黑名单 Token 校验
 * 5 加载用户角色与权限
 * 6 注入 Spring Security 认证信息
 * 7 注入 ThreadLocal 用户上下文
 *
 * <p>
 * 继承 OncePerRequestFilter 保证每个请求只执行一次
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ISysMenuService iSysMenuService;
    private final ISysRoleService iSysRoleService;
    private final IRedisService iRedisService;
    private final SecurityWhiteListProperties whiteListProperties;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Spring 推荐的路径匹配器
     */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    /**
     * Step 0：白名单判断
     *
     * 如果请求属于白名单接口，则跳过 JWT 认证过滤器
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String uri = request.getRequestURI();

        // RefreshToken 接口必须允许直接访问
        if ("/api/auth/refresh".equals(uri)) {
            return true;
        }

        List<String> whitelist = whiteListProperties.getWhitelist();

        return whitelist != null
                && whitelist.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }


    /**
     * Step 1 ~ Step 10：JWT 认证核心流程
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // =========================
        // Step 1：从请求头提取 Token
        // =========================
        String token = JwtUtil.extractBearerToken(
                request.getHeader(JwtConstants.JWT_HEADER)
        );

        // 如果请求没有 Token，则直接放行
        if (StringUtils.isBlank(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            // =========================
            // Step 2：解析 JWT Token
            // =========================
            Claims claims = jwtUtil.parseToken(token);

            // =========================
            // Step 3：Token 类型校验 (Fail-Fast)
            // 防止 RefreshToken 被当作 AccessToken 使用
            // =========================
            String tokenType = claims.get(JwtConstants.CLAIM_TOKEN_TYPE, String.class);

            if (!JwtConstants.TOKEN_TYPE_ACCESS.equals(tokenType)) {
                throw new JwtAuthenticationException("非法 Token 类型");
            }

            // =========================
            // Step 4：解析 Token 关键信息
            // =========================
            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();
            String deviceId = claims.get("deviceId", String.class);
            String jti = claims.getId();

            // 基础信息完整性校验
            if (userId == null || StringUtils.isBlank(deviceId)) {
                throw new JwtAuthenticationException("Token 信息不完整");
            }

            // =========================
            // Step 5：Redis Session 校验
            // 用于检测账号是否被其他设备挤下线
            // =========================
            String activeJti = iRedisService.get(
                    RedisCacheConstants.AUTH_USER_SESSION + userId,
                    String.class
            );

            if (activeJti != null && !activeJti.equals(jti)) {
                throw new JwtAuthenticationException("您的账号已在其他设备登录");
            }

            // =========================
            // Step 6：Token 黑名单校验
            // 用于 logout 或安全强制下线
            // =========================
            if (iRedisService.exists(
                    RedisCacheConstants.AUTH_TOKEN_BLACKLIST + jti
            ) > 0) {
                throw new JwtAuthenticationException("状态已失效");
            }

            // =========================
            // Step 7：如果 SecurityContext 尚未认证
            // 则进行用户权限加载
            // =========================
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                // Step 7.1：从 Redis 或数据库加载用户角色与权限
                LoginUserDTO userAuth = getAndCacheFullAuth(userId, username, deviceId);

                // Step 7.2：构建 Spring Security Authority
                List<SimpleGrantedAuthority> authorities =
                        new ArrayList<>(userAuth.getRoles().size()
                                + userAuth.getPermissions().size());

                userAuth.getRoles()
                        .forEach(role ->
                                authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));

                userAuth.getPermissions()
                        .forEach(perm ->
                                authorities.add(new SimpleGrantedAuthority(perm)));

                // Step 7.3：注入 ThreadLocal 用户上下文
                UserContextUtil.setUser(userAuth);

                // Step 7.4：构建 Spring Security Authentication 对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                authorities
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Step 7.5：注入 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // =========================
            // Step 8：继续执行后续过滤器链
            // =========================
            filterChain.doFilter(request, response);

        } catch (JwtAuthenticationException e) {

            // Step 9：JWT 认证异常处理
            handleException(request, response, e.getMessage());

        } catch (Exception e) {

            log.error("JWT 认证未知异常", e);

            handleException(request, response, "系统安全校验异常");

        } finally {

            // =========================
            // Step 10：清理 ThreadLocal
            // 防止线程复用导致用户信息串号
            // =========================
            UserContextUtil.clear();
        }
    }


    /**
     * 加载用户角色与权限（带缓存 + 防击穿）
     *
     * Redis Key：
     * auth:user:auth:{userId}
     */
    private LoginUserDTO getAndCacheFullAuth(Long userId,
                                             String username,
                                             String deviceId) {

        String key = RedisCacheConstants.AUTH_USER_AUTH + userId;

        // Step 1：先查 Redis
        LoginUserDTO cached = iRedisService.get(key, LoginUserDTO.class);

        if (cached != null) {
            return cached;
        }

        // Step 2：本地锁防止缓存击穿
        synchronized (String.valueOf(userId).intern()) {

            cached = iRedisService.get(key, LoginUserDTO.class);

            if (cached != null) {
                return cached;
            }

            // Step 3：查询数据库
            log.info("用户 {} 权限缓存失效，查库加载", username);

            Set<String> roles =
                    iSysRoleService.selectRoleCodesByUserId(userId);

            Set<String> perms =
                    iSysMenuService.selectPermissionsByUserId(userId);

            // Step 4：构建缓存对象
            // 在 getAndCacheFullAuth 的 Step 4 附近
            // 如果是 ID 为 1 的用户，强制赋予 SUPER_ADMIN 角色，防止数据库配置丢失导致超管进不去系统
            if (Long.valueOf(1L).equals(userId)) {
                roles.add(RoleConstants.SUPER_ADMIN);
                // 很多系统习惯用 *:*:* 代表拥有所有操作权限
                perms.add("*:*:*");
            }
            LoginUserDTO dto = LoginUserDTO.builder()
                    .userId(userId)
                    .username(username)
                    .deviceId(deviceId)
                    .roles(roles == null ? Collections.emptySet() : roles)
                    .permissions(perms == null ? Collections.emptySet() : perms)
                    .build();

            // Step 5：写入 Redis
            iRedisService.setEx(
                    key,
                    dto,
                    RedisCacheConstants.AUTH_PERMISSION_CACHE_TTL_DAYS,
                    TimeUnit.DAYS
            );

            return dto;
        }
    }


    /**
     * 统一异常处理入口
     */
    private void handleException(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String msg)
            throws IOException, ServletException {

        SecurityContextHolder.clearContext();

        request.setAttribute("jwt_exception_msg", msg);

        jwtAuthenticationEntryPoint.commence(
                request,
                response,
                new AuthenticationServiceException(msg)
        );
    }
}