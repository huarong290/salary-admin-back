package com.salary.admin.model.dto.salary.archive;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.salary.archiveitem.ArchiveItemAddDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;



@Data
@Schema(description = "薪资档案保存请求(含明细)")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchiveAddReqDTO {

    @Schema(description = "员工ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    @Schema(description = "基本工资", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "基本工资不能为空")
    private BigDecimal baseSalary;

    @Schema(description = "全勤奖标准")
    private BigDecimal fullAttendanceBonus;

    @Schema(description = "试用期底薪")
    private BigDecimal probationBaseSalary;

    @Schema(description = "生效日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "生效日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveDate;

    @Schema(description = "结算币种")
    private String currency = "CNY";

    @Schema(description = "调薪原因")
    private String changeReason;

    /**
     * 版本号（如果是新增定薪，前端可能传0或空；如果是调薪，前端传当前版本）
     */
    @Schema(description = "版本号")
    private Integer version;
    /**
     * 计税方案: 0-不计税, 1-居民个人所得税, 2-劳务报酬税
     */
    @Schema(description = "计税方案: 0-不计税, 1-居民个人所得税, 2-劳务报酬税")
    private Integer taxScheme;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "薪资项明细列表")
    private List<ArchiveItemAddDTO> items;


}