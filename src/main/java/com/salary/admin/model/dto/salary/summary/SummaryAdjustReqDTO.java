package com.salary.admin.model.dto.salary.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "薪资单手工账调整请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryAdjustReqDTO {

    @NotNull(message = "汇总单ID不能为空")
    @Schema(description = "汇总单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @NotNull(message = "调整金额不能为空，若清空请填 0")
    @Schema(description = "手动发放总额 (正数补偿，负数扣回)", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal manualPaymentAmount;

    @Schema(description = "调整备注 (审计留痕)")
    private String remark;
}