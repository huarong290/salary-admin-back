package com.salary.admin.model.dto.salary.summary;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 薪资汇总操作请求对象
 */
@Data
@Schema(description = "薪资单状态变更请求对象")
public class SalarySummaryOperateDTO {

    @Schema(description = "待操作的 ID 集合")
    @NotEmpty(message = "请至少选择一条记录")
    private List<Long> ids;

    @Schema(description = "目标锁定状态：0-解锁，1-锁定")
    @NotNull(message = "操作指令不能为空")
    private Integer lockFlag;

    @Schema(description = "操作备注 (可选，用于审计日志)")
    private String remark;
}
