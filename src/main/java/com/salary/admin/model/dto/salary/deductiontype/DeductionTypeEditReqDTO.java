package com.salary.admin.model.dto.salary.deductiontype;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 修改扣款类型 DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "修改扣款类型请求")
public class DeductionTypeEditReqDTO extends DeductionTypeAddReqDTO {
    @NotNull(message = "ID不能为空")
    @Schema(description = "类型ID")
    private Long id;
}
