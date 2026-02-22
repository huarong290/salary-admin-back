package com.salary.admin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.salary.admin.constants.security.JwtConstants.JWT_BEARER_PREFIX;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "认证令牌响应对象")
public class TokenResDTO {

    /**
     * 新生成的访问令牌（AccessToken）
     * 有效期通常较短（如30分钟）
     */
    @Schema(description = "访问令牌 (AccessToken)")
    private String accessToken;

    /**
     * 刷新令牌（RefreshToken）
     * 当采用轮换刷新令牌策略时返回新令牌
     */
    @Schema(description = "刷新令牌 (RefreshToken)")
    private String refreshToken;

    /**
     * 建议：统一为秒级，与 JWT 标准对齐
     * AccessToken 过期时间（秒）
     * 前端用于倒计时或刷新策略
     */
    @Schema(description = "AccessToken 有效期 (秒)")
    private Long expiresIn;

    /**
     * 令牌类型，固定为 "Bearer"
     */
    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = JWT_BEARER_PREFIX.trim();

    // ============================
    // 🟡【优化新增】可选字段，前端/后台显示或风控
    // ============================

    /**
     * 登录设备ID（与RefreshToken绑定）
     */
    @Schema(description = "绑定的设备ID")
    private String deviceId;

    /**
     * 客户端类型：WEB / APP / MINI / OTHER
     */
    @Schema(description = "客户端类型")
    private String clientType;

    /**
     * 登录IP
     */
    @Schema(description = "本次登录IP")
    private String ip;
    /**
     * RefreshToken 剩余有效期(秒)
     */
    @Schema(description = "RefreshToken 剩余有效期 (秒)")
    private Long refreshExpiresIn;
}
