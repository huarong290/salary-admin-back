package com.salary.admin.model.dto.salary.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 薪资汇总初始化请求对象
 * 用于触发特定月份的薪资计算初始数据生成
 *
 */
@Data
@Schema(description = "薪资汇总初始化请求参数")
public class SummaryInitReqDTO {

    /**
     * 结算月份
     * 格式要求：YYYYMM，例如：202604
     */
    @NotBlank(message = "结算月份不能为空")
    @Schema(description = "结算月份", example = "202604", requiredMode = Schema.RequiredMode.REQUIRED)
    private String settlementMonth;

    /**
     * 指定员工ID列表
     * 1. 如果列表不为空，则仅初始化指定员工的数据
     * 2. 如果列表为空或为 null，则默认初始化全员数据
     */
    @Schema(description = "指定员工ID列表(为空则初始化全员)", example = "[1, 2, 3]")
    private List<Long> employeeIds;
}