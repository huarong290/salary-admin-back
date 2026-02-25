package com.salary.admin.model.dto.captcha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码 数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "验证码响应实体")
public class CaptchaResDTO {
    /**
     * 一个唯一的 captchaId（方便后端存储和校验）
     */
    @Schema(description = "验证码唯一标识 (对应后端的 Redis Key)")
    private String captchaId;
    /**
     *一个 Base64 图片字符串
     */
    @Schema(description = "验证码图片 (Base64字符串，前端直接放入 img 的 src)")
    private String captchaImage;
    /**
     * 一个 过期时间
     */
    @Schema(description = "过期时间 (单位：秒/毫秒，建议统一格式)")
    private Long expireTime;
    /**
     * 一个 是否启用标志
     */
    @Schema(description = "是否启用验证码 (true:需输入, false:隐藏验证码模块)")
    private Boolean captchaEnabled;
}