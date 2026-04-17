package com.salary.admin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * MFA 验证请求实体
 */
@Data
@Schema(description = "MFA二次验证请求参数")
public class MfaVerifyReqDTO {

    @NotBlank(message = "MFA临时令牌不能为空")
    @Schema(description = "第一步下发的 MFA 临时令牌")
    private String mfaToken;

    @NotBlank(message = "MFA类型不能为空")
    @Schema(description = "使用的验证类型 (GOOGLE / HAIYUE)")
    private String mfaType;

    @NotBlank(message = "动态验证码不能为空")
    @Schema(description = "用户输入的 6 位或 8 位动态口令")
    private String code;
}