package com.salary.admin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录混合响应数据传输对象
 * 兼容直接下发 Token 或触发 MFA 两阶段验证
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // 继承 TokenResDTO 的属性
@Schema(description = "登录混合响应实体 (支持MFA)")
public class LoginResultDTO extends TokenResDTO {

    @Schema(description = "是否需要进行两阶段验证 (MFA)")
    private Boolean requireMfa;

    @Schema(description = "MFA 临时令牌 (用于第二步换取真实 Token，有效时间建议设为5分钟)")
    private String mfaToken;

    @Schema(description = "当前用户支持的 MFA 类型列表，例如 [\"GOOGLE\", \"HAIYUE\"]")
    private List<String> supportedTypes;
}