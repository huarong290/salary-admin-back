package com.salary.admin.model.dto.salary.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 触发薪资计算请求 DTO
 */
@Data
@Schema(description = "触发薪资计算请求")
public class SummaryCalcReqDTO implements Serializable {

    @NotNull(message = "薪资周期ID不能为空")
    @Schema(description = "需要进行结算的薪资周期ID")
    private Long periodId;

    @Schema(description = "手工备注 (如: 2026年3月特殊结算)")
    private String remark;
}
