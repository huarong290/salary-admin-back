package com.salary.admin.utils;

import com.salary.admin.constants.security.JwtConstants;
import com.salary.admin.exception.JwtAuthenticationException;
import com.salary.admin.property.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


/**
 * JWT 工具类 - JDK 21 优化版
 * <p>
 * 基于 jjwt 0.13.x
 * 使用同步阻塞方式，适用于传统 Spring MVC 架构
 *
 * 功能：
 * 1. 生成 AccessToken / RefreshToken
 * 2. 解析 JWT 获取 Claims
 * 3. 校验 Token 是否有效
 * 4. 获取 Token 剩余有效时间
 * 5. 从 Authorization Header 提取 Bearer Token
 */
@Slf4j
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    /**
     * HMAC 密钥
     */
    private SecretKey secretKey;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }
    /**
     * 初始化 SecretKey
     * 使用 HS256 算法
     */
    @PostConstruct
    public void init() {
        // HS256 算法要求密钥至少 256 位 (32字节)
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            log.error("JWT Secret 长度不足 32 字节，当前长度: {}", keyBytes.length);
            throw new IllegalArgumentException("JWT Secret must be at least 32 characters long");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // ===================== Token 生成 =====================
    /**
     * 生成 AccessToken
     *
     * @param username    用户名
     * @param extraClaims 扩展 Claims（角色、权限、租户等）
     * @return AccessToken
     */
    public String generateAccessToken(String username, Map<String, Object> extraClaims) {
        return generateToken(username, "access", jwtProperties.getAccessTokenExpiration(), extraClaims);
    }
    /**
     * 生成 RefreshToken
     *
     * @param username 用户名
     * @return RefreshToken
     */
    public String generateRefreshToken(String username) {
        return generateToken(username, "refresh", jwtProperties.getRefreshTokenExpiration(), null);
    }
    /**
     * 通用生成 Token 方法
     */
    private String generateToken(String username, String type, long expMillis, Map<String, Object> extra) {
        // 使用 JDK 21 Instant 处理时间，更精准且线程安全
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expMillis);

        return Jwts.builder()
                .id(UUID.randomUUID().toString()) // Jti 用于 Redis 黑名单校验
                .subject(username)
                .claim("type", type)
                .claims(extra != null ? extra : Map.of()) // JDK 21 Map.of 空映射
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    // ===================== Token 解析 =====================

    /**
     * 解析并校验 Token
     * 核心优化：将所有 JJWT 异常统一收敛到自定义的 JwtAuthenticationException
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("Token已过期");
        } catch (SignatureException e) {
            throw new JwtAuthenticationException("Token签名无效");
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("Token格式非法");
        } catch (Exception e) {
            log.error("JWT解析未知错误", e);
            throw new JwtAuthenticationException("Token解析失败");
        }
    }

    // ===================== 快捷工具方法 =====================

    /**
     * 从 Authorization Header 提取 Bearer Token
     */
    public static String extractBearerToken(String authHeader) {
        if (authHeader != null
                && authHeader.startsWith(JwtConstants.JWT_BEARER_PREFIX)) {
            return authHeader.substring(JwtConstants.JWT_BEARER_PREFIX.length());
        }
        return "";
    }

    /**
     * 获取 Token 剩余寿命 (秒)，用于存入 Redis 黑名单时设置 TTL
     */
    public long getRemainingSeconds(Claims claims) {
        long exp = claims.getExpiration().getTime();
        long now = System.currentTimeMillis();
        return Math.max(0, (exp - now) / 1000);
    }
}