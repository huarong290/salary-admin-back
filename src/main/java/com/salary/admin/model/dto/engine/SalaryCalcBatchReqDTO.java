package com.salary.admin.model.dto.engine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "引擎-批量薪资核算指令参数")
public class SalaryCalcBatchReqDTO {

    @NotNull(message = "薪资周期ID不能为空")
    @Schema(description = "薪资周期ID")
    private Long periodId;

    @NotEmpty(message = "执行核算的员工列表不能为空")
    @Schema(description = "需要核算的员工ID集合")
    private List<Long> employeeIds;

    @NotBlank(message = "管道编码不能为空")
    @Schema(description = "核算管道编码")
    private String pipelineCode;

    @NotNull(message = "管道版本号不能为空")
    @Schema(description = "管道版本号")
    private Integer pipelineVersion;
}
