package com.salary.admin.model.dto.salary.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "薪资配置新增参数")
public class SalaryConfigAddReqDTO extends SalaryConfigBaseDTO {

    @Schema(description = "配置键 (唯一标识)", example = "SETTLEMENT_CURRENCY")
    @NotBlank(message = "配置键不能为空")
    // 新增时必须传入 Key，一旦确定通常不改
    private String configKey;
}
