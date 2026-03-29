package com.salary.admin.model.vo.salary.period;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 薪资周期下拉选项 VO - 轻量化对象
 */
@Data
@Schema(description = "薪资周期下拉选项")
public class PeriodOptionVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "周期ID")
    private Long id;

    @Schema(description = "结算月份 (格式：202603)")
    private String settlementMonth;

    @Schema(description = "在岗月份 (格式：2026-03)")
    private Integer workMonth;

    @Schema(description = "开始日期 (YYYY-MM-DD)")
    private String startDate;

    @Schema(description = "结束日期 (YYYY-MM-DD)")
    private String endDate;
}
