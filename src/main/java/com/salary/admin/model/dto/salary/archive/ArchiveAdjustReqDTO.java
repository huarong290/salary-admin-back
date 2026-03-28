package com.salary.admin.model.dto.salary.archive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.salary.archiveitem.ArchiveItemReqDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 员工调薪申请请求参数
 */
@Data
@Schema(description = "员工调薪申请请求参数")
@Builder
@NoArgsConstructor
@AllArgsConstructor
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchiveAdjustReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "员工ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    @Schema(description = "调整后-基本工资", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "调整后基本工资不能为空")
    @DecimalMin(value = "0.00", message = "基本工资不能为负数")
    private BigDecimal baseSalary;

    @Schema(description = "调整后-生效日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "薪资生效日期不能为空")
    private LocalDate effectiveDate;

    @Schema(description = "调整后-失效日期 (YYYY-MM-DD)，默认 9999-12-31")
    private LocalDate expiryDate;


    @Schema(description = "调整后-试用期底薪")
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal probationBaseSalary;

    @Schema(description = "调整后-结算币种", defaultValue = "CNY")
    @NotBlank(message = "结算币种不能为空")
    private String currency;

    @Schema(description = "调整后-个税规则Code")
    @NotBlank(message = "个税规则不能为空")
    private String taxRuleCode;

    @Schema(description = "调薪原因 (如：年度普调、晋升)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "调薪原因不能为空，必须记录审计台账")
    private String changeReason;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "调整后的附加薪资项列表 (必须传全量最新的明细项)")
    @Valid // 🌟 关键：触发对 List 内部元素的级联校验
    private List<ArchiveItemReqDTO> archiveItems;
}
