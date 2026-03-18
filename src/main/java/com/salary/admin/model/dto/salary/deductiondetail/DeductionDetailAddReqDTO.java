package com.salary.admin.model.dto.salary.deductiondetail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 新增扣款明细 DTO
 */
@Data
@Schema(description = "新增扣款明细请求")
public class DeductionDetailAddReqDTO implements Serializable {

    @NotNull(message = "薪资周期ID不能为空")
    @Schema(description = "薪资周期ID")
    private Long periodId;

    @NotNull(message = "扣款类型不能为空")
    @Schema(description = "扣款类型ID")
    private Long deductionTypeId;

    @Schema(description = "原币种 (如 CNY, PHP)", defaultValue = "CNY")
    private String currency = "CNY";

    @NotNull(message = "原币金额不能为空")
    @DecimalMin(value = "0.00", message = "金额不能为负数")
    @Schema(description = "原币金额")
    private BigDecimal originalAmount;

    @NotNull(message = "汇率不能为空")
    @Schema(description = "汇率 (原币兑本币，如 PHP兑CNY 约为0.125)", defaultValue = "1.00")
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.00", message = "金额不能为负数")
    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String remark;
}


