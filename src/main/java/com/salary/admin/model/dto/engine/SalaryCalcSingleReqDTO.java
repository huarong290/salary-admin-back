package com.salary.admin.model.dto.engine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true) // 开启链式编程，方便构造
@Schema(description = "引擎-单人薪资核算指令参数")
public class SalaryCalcSingleReqDTO {

    @NotNull(message = "薪资单据(账套)ID不能为空")
    @Schema(description = "待核算的薪资汇总单ID (对应 salary_summary.id)")
    private Long summaryId;


    @Schema(description = "薪资周期ID")
    private Long periodId;

    @Schema(description = "员工ID")
    private Long employeeId;
    // ===============================================
    // 管道参数改为【非必填】
    // ===============================================

    @Schema(description = "核算管道编码")
    private String pipelineCode;

    @Schema(description = "管道版本号")
    private Integer pipelineVersion;

    // 💡 架构师预留字段
    @Schema(description = "是否强制重算 (无视锁定状态，仅超管可用)")
    private Boolean isForceRetry;

    // ===============================================
    // 🌟 新增：时间旅行/历史追溯重算 参数
    // ===============================================

    @Schema(description = "指定的薪资档案ID (选填，用于指定历史版本算薪。不传则默认取当前最新生效档案)")
    private Long archiveId;
    // private String operator;      // 触发计算的操作人
}
