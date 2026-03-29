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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 员工入职定薪 DTO（初始化 V1）
 */
@Schema(name = "ArchiveInitReqDTO", description = "员工入职定薪请求参数")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchiveInitReqDTO {

    @Schema(description = "员工ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    @Schema(description = "基本工资(转正后底薪)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "baseSalary 不能为空")
    @DecimalMin(value = "0.00", inclusive = true, message = "baseSalary 必须 >= 0")
    private BigDecimal baseSalary;

    @Schema(description = "当前岗位/职级 (保存时同步更新员工档案)")
    private String jobTitle;

    @Schema(description = "生效日期(通常为入职日期 YYY-MM-DD 默认当天")
    private LocalDate effectiveDate;

    @Schema(description = "失效日期 (YYYY-MM-DD)，默认 9999-12-31")
    private LocalDate expiryDate;

    @Schema(description = "试用期底薪")
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal probationBaseSalary;

    @Schema(description = "结算币种，默认 CNY")
    @NotBlank(message = "currency 不能为空")
    private String currency;

    @Schema(description = "个税规则Code", defaultValue = "TAX_RESIDENT_CN")
    @NotBlank(message = "个税规则不能为空")
    private String taxRuleCode;

    @Schema(description = "调薪原因/说明")
    private String changeReason;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "附加薪资项列表 (如餐补、房补等)")
    @Valid // 🌟 关键：触发对 List 内部 ArchiveItemReqDTO 的属性校验
    private List<ArchiveItemReqDTO> archiveItems;
}

