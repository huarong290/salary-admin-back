package com.salary.admin.model.dto.salary.deductiondetail;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改扣款明细 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "修改扣款明细请求")
public class DeductionDetailUpdateReqDTO extends DeductionDetailAddReqDTO {

    @NotNull(message = "修改时明细ID不能为空")
    @Schema(description = "明细主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

}