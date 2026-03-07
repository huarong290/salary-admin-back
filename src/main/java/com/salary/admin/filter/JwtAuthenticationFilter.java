package com.salary.admin.filter;


import com.salary.admin.constants.redis.RedisCacheConstants;
import com.salary.admin.constants.security.JwtConstants;
import com.salary.admin.exception.JwtAuthenticationException;
import com.salary.admin.model.dto.LoginUserDTO;
import com.salary.admin.property.SecurityWhiteListProperties;
import com.salary.admin.security.JwtAuthenticationEntryPoint;
import com.salary.admin.service.IRedisService;
import com.salary.admin.service.ISysMenuService;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
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
 * 核心职责：Token 校验、多端挤兑检查、黑名单拦截、用户上下文注入。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    private final ISysMenuService iSysMenuService;

    private final IRedisService iRedisService;

    //  注入白名单配置
    private final SecurityWhiteListProperties whiteListProperties;
    // 使用 Spring 官方推荐的路径匹配器
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    /**
     * 框架级跳过逻辑：白名单请求不走此过滤器
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();

        // 1. 显式排除刷新接口（确保它能带着 RefreshToken 到达 Controller）
        // 即使白名单没配这个，我们也建议硬编码或确保它在白名单内
        // 建议：使用 equals 或精准的 match，防止 URI 伪造绕过
        if ("/api/auth/refresh".equals(uri)) {
            return true;
        }

        // 2. 动态匹配 YAML 中的白名单
        List<String> whitelist = whiteListProperties.getWhitelist();
        if (whitelist == null || whitelist.isEmpty()) {
            return false;
        }

        return whitelist.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1.提取 Token (直接使用 static 方法，减少实例依赖)
        String token = JwtUtil.extractBearerToken(request.getHeader(JwtConstants.JWT_HEADER));
        // 2. 无 Token直接放行 交由 Security后续拦截器处理（如果是受保护接口，SecurityConfig中的EntryPoint 会拦截）
        if (StringUtils.isBlank(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            // 3.解析并初步校验签名/过期
            Claims claims = jwtUtil.parseToken(token);
            String userId = claims.get("userId", String.class);
            String jti = claims.getId(); // 建议直接用 getId()
            String deviceId = claims.get("deviceId", String.class);
            String username = claims.getSubject();
            // 4. 【安全加固】设备指纹强校验：防止 Token 被脱离设备环境使用
            if (StringUtils.isBlank(deviceId)) {
                throw new JwtAuthenticationException("非法凭证：设备指纹缺失");
            }
            // 5. 【token类型校验】确保不能用 RefreshToken 当 AccessToken 使用
            String type = claims.get(JwtConstants.CLAIM_TOKEN_TYPE, String.class);
            if (!JwtConstants.TOKEN_TYPE_ACCESS.equals(type)) {
                throw new JwtAuthenticationException("非法 Token 类型");
            }
            // 5.【时效校验】校验是否过期
            if (claims.getExpiration().before(new Date())) {
                throw new JwtAuthenticationException("Token 已过期");
            }
            // 7. 【挤兑校验】全端/同端唯一性检查
            // 如果 Redis 中的最新 JTI 与当前 Token 的 JTI 不符，说明该账号已在别处重新登录或已刷新 Token
            if (StringUtils.isNotBlank(userId)) {
                String activeJti = iRedisService.get(RedisCacheConstants.AUTH_USER_ACTIVE + userId, String.class);
                // 如果活跃 JTI 存在且不等于当前 JTI，说明该账号在别处登录了或者是同设备重新登录了
                if (activeJti != null && !activeJti.equals(jti)) {
                    throw new JwtAuthenticationException("您的账号已在其他设备登录或已失效");
                }
            }
            // 8. 【黑名单校验】拦截主动注销的 Token
            if (iRedisService.exists(RedisCacheConstants.AUTH_TOKEN_BLACKLIST + jti) > 0) {
                throw new JwtAuthenticationException("会话已安全退出，请重新登录");
            }

            // 9. 【补全】注入 Security 上下文
            if (StringUtils.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 加载权限（含缓存击穿保护逻辑）
                Set<String> permissions = getAndCachePermissions(userId, username);
                List<SimpleGrantedAuthority> authorities = permissions.stream().map(SimpleGrantedAuthority::new).toList();
                // A. 注入业务上下文 (供 Service 层使用，如 logout 方法)
                UserContextUtil.setUser(LoginUserDTO.builder()
                        .userId(Long.valueOf(userId))
                        .username(username)
                        .deviceId(deviceId)
                        .build());
                // B. 注入 Security 上下文 (供权限注解 @PreAuthorize 使用)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 关键点：这一步决定了后面的接口能不能拿到用户信息
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("用户 {} 认证成功，权限：{}", username, authorities);
            }
            //  核心改动点：在这里执行 filterChain
            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException e) {
            log.warn("JWT 认证拦截: {} -> URL: {}", e.getMessage(), request.getRequestURI());
            handleException(request, response, e.getMessage());
        } catch (Exception e) {
            log.error("安全过滤器未知异常", e);
            handleException(request, response, "系统安全校验异常");
        } finally {
            // 10. 【灵魂清理】严防 ThreadLocal 内存泄漏，无论请求成功还是异常，必须清理
            UserContextUtil.clear();
        }
    }

    /**
     * 加载并缓存用户权限 (带缓存击穿防护)
     */
    private Set<String> getAndCachePermissions(String userId, String username) {
        String permKey = RedisCacheConstants.AUTH_USER_PERMISSIONS + userId;
        Set<String> permissions = iRedisService.get(permKey, Set.class);

        if (permissions == null) {
            log.info("用户 {} 权限缓存失效，正在重新加载...", username);
            permissions = iSysMenuService.selectPermissionsByUserId(Long.valueOf(userId));

            // 💡 即使权限为空，也缓存一个空集合（或者设置较短过期时间），防止频繁查询数据库
            Set<String> cacheValue = (permissions == null) ? Collections.emptySet() : permissions;
            iRedisService.setEx(permKey, cacheValue, 7, TimeUnit.DAYS);
            return cacheValue;
        }
        return permissions;
    }

    /**
     * 统一异常出口处理
     */
    private void handleException(HttpServletRequest request, HttpServletResponse response, String msg) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        //  关键：将消息存入 request
        request.setAttribute("jwt_exception_msg", msg);
        // 💡 手动调用 EntryPoint，利用它将 ApiResult 写回前端处理未知异常的响应
        jwtAuthenticationEntryPoint.commence(request, response, new AuthenticationServiceException(msg));
    }
}