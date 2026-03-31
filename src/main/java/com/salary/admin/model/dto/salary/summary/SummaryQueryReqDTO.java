package com.salary.admin.model.dto.salary.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资汇总列表查询 DTO
 */
@Data
@Schema(description = "分页查询薪资汇总请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryQueryReqDTO extends PageQueryDTO {
    @Schema(description = "模糊搜索关键词 (姓名或工号)")
    private String keyword;
    @Schema(description = "指定查询的薪资周期ID")
    private Long periodId;

    @Schema(description = "结算月份 (如: 202603)")
    private String settlementMonth;

    @Schema(description = "员工ID (用于查询某员工的历史工资单)")
    private Long employeeId;

    @Schema(description = "计算状态:0-未计算 1-成功 2-失败")
    private Integer calcStatus;

    @Schema(description = "发放状态:0-未支付 1-已支付 2-支付失败")
    private Integer paymentStatus;
}
