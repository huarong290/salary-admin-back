package com.salary.admin.model.vo.salary.period;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "薪资周期简易选项对象")
public class PeriodOptionVO {
    @Schema(description = "周期ID (可选，如果按月批量核算则传月份即可)")
    private Long id;

    @Schema(description = "展示文本 (如：2026-03)")
    private String label;

    @Schema(description = "实际值 (如：202603)")
    private String value;
}
