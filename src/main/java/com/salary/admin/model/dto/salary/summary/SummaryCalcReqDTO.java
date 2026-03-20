package com.salary.admin.model.dto.salary.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 触发薪资计算请求 DTO
 */
@Data
@Schema(description = "触发薪资计算请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryCalcReqDTO implements Serializable {

    @Schema(description = "需要进行结算的薪资周期ID (如果传了此项，则仅核算该周期)")
    private Long periodId;

    @Schema(description = "结算月份 (如: 202603，如果不传 periodId，则按月份核算全员)")
    private String settlementMonth;

    @Schema(description = "手工备注 (如: 2026年3月特殊结算)")
    private String remark;
}
