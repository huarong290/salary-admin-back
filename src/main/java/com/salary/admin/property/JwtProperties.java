package com.salary.admin.property;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Component
@Validated // 开启参数校验
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥（建议使用 Base64 编码的 32 位以上字符串）
     */
    @NotBlank(message = "JWT 密钥不能为空")
    private String secret;

    /**
     * Access Token 有效期（毫秒）
     * 默认 3600000 (1小时)，最小 60000 (1分钟)
     */
    @Min(value = 60000, message = "AccessToken 有效期不能低于 1 分钟")
    private long accessTokenExpiration;

    /**
     * Refresh Token 有效期（毫秒）
     * 默认 604800000 (7天)
     */
    @Min(value = 86400000, message = "RefreshToken 有效期不能低于 1 天")
    private long refreshTokenExpiration;

    /**
     * 签名算法（默认 HS256，可配置 HS512）
     */
    private String algorithm = "HS256";
}
