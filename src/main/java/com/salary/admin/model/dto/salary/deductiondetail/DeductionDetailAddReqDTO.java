package com.salary.admin.model.dto.salary.deductiondetail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.00", message = "金额不能为负数")
    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String remark;
}


