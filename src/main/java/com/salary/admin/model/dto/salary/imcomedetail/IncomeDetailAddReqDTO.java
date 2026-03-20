package com.salary.admin.model.dto.salary.imcomedetail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 新增收入明细 DTO
 */
@Data
@Schema(description = "新增收入明细请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeDetailAddReqDTO implements Serializable {
    @NotNull(message = "薪资周期ID不能为空")
    @Schema(description = "薪资周期ID")
    private Long periodId;

    @NotNull(message = "员工Id不能为空")
    @Schema(description = "员工Id")
    private Long employeeId;

    @NotNull(message = "收入类型不能为空")
    @Schema(description = "收入类型ID")
    private Long incomeTypeId;

    @Schema(description = "原币种 (如 CNY, PHP)", defaultValue = "CNY")
    private String currency = "CNY";

    @NotNull(message = "原币金额不能为空")
    @DecimalMin(value = "0.00", message = "金额不能为负数")
    @Schema(description = "原币金额")
    private BigDecimal originalAmount;

    @NotNull(message = "汇率不能为空")
    @Schema(description = "汇率 (原币兑本币，如 PHP兑CNY 约为0.125)", defaultValue = "1.00")
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String remark;
}