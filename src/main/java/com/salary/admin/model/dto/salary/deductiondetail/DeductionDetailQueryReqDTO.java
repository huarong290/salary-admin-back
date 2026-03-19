package com.salary.admin.model.dto.salary.deductiondetail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页查询扣款明细 DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "分页查询扣款明细请求")
// 架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeductionDetailQueryReqDTO extends PageQueryDTO {
    /**
     * 周期ID
     */
    @Schema(description = "周期ID")
    private Long periodId;
    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    private Long employeeId;

    /**
     * 扣款类型ID
     */
    @Schema(description = "扣款类型ID (关联 salary_deduction_type)")
    private Long deductionTypeId;

    /**
     * 结算月份
     */
    @Schema(description = "结算月份")
    private String settlementMonth;
}
