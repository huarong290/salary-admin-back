package com.salary.admin.model.dto.salary.deductiondetail;

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
}
