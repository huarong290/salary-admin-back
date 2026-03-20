package com.salary.admin.model.dto.salary.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "薪资配置修改参数")
public class SalaryConfigEditReqDTO extends SalaryConfigBaseDTO {

    @Schema(description = "主键ID", required = true)
    @NotNull(message = "配置ID不能为空")
    private Long id;

    // 修改时通常不传 configKey，或者将其设为只读，防止业务逻辑崩坏
}