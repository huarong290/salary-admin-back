package com.salary.admin.model.dto.engine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true) // 开启链式编程，方便构造
@Schema(description = "引擎-单人薪资核算指令参数")
public class SalaryCalcSingleReqDTO {

    @NotNull(message = "薪资周期ID不能为空")
    @Schema(description = "薪资周期ID")
    private Long periodId;

    @NotNull(message = "员工ID不能为空")
    @Schema(description = "员工ID")
    private Long employeeId;

    @NotBlank(message = "管道编码不能为空")
    @Schema(description = "核算管道编码")
    private String pipelineCode;

    @NotNull(message = "管道版本号不能为空")
    @Schema(description = "管道版本号")
    private Integer pipelineVersion;

    // 💡 架构师预留字段 (未来扩展无需改接口)
    // private Boolean isForceRetry; // 是否强制重算
    // private String operator;      // 触发计算的操作人
}
