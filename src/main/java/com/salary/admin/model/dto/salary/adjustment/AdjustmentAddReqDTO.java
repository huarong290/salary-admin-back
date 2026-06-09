package com.salary.admin.model.dto.salary.adjustment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "薪资专项调整-新增请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdjustmentAddReqDTO {

    @NotNull(message = "关联核算周期不可为空")
    private Long periodId;

    @NotNull(message = "员工ID不可为空")
    private Long employeeId;

    @NotBlank(message = "薪资项编码不可为空")
    private String itemCode;

    @NotBlank(message = "薪资项名称不可为空")
    private String itemName;

    @NotBlank(message = "原币种代码不可为空")
    private String currency;

    @NotNull(message = "原币金额不可为空")
    private BigDecimal originalAmount;

    @NotNull(message = "汇率不可为空")
    private BigDecimal exchangeRate;

    @NotNull(message = "调账类型不可为空(1-增加, 2-扣减)")
    private Integer adjustType;

    private String remark;
}