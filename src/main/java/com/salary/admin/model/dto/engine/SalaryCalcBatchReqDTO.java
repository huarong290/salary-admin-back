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

    @NotEmpty(message = "请至少选择一条需要核算的薪资单据")
    @Schema(description = "待核算的薪资汇总单ID集合 (对应 salary_summary.id)")
    private List<Long> summaryIds;

    @NotBlank(message = "管道编码不能为空")
    @Schema(description = "核算管道编码")
    private String pipelineCode;

    @NotNull(message = "管道版本号不能为空")
    @Schema(description = "管道版本号")
    private Integer pipelineVersion;
}
