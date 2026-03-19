package com.salary.admin.model.dto.salary.imcomedetail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询收入明细 DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "分页查询收入明细请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeDetailQueryReqDTO extends PageQueryDTO {
    /**
     * 周期ID
     */
    @Schema(description = "周期ID")
    private Long periodId;
    /**
     * 员工ID
     */
    @Schema(description = "员工ID (用于查询某人的所有收入流水)")
    private Long employeeId;

    /**
     * 收入类型ID
     */
    @Schema(description = "收入类型ID (关联 salary_income_type)")
    private Long incomeTypeId;
    /**
     * 结算月份
     */
    @Schema(description = "结算月份")
    private String settlementMonth;
}
