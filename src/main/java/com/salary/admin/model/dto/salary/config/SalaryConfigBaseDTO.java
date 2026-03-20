package com.salary.admin.model.dto.salary.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "薪资配置基础参数")
public class SalaryConfigBaseDTO {

    @Schema(description = "配置名称", example = "本位币设置")
    @NotBlank(message = "配置名称不能为空")
    private String configName;

    @Schema(description = "配置值", example = "USDT")
    @NotBlank(message = "配置值不能为空")
    private String configValue;

    @Schema(description = "激活状态 (0停用 1正常)")
    @NotNull(message = "状态不能为空")
    private Integer activeFlag;

    @Schema(description = "备注")
    private String remark;
}