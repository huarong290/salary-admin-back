package com.salary.admin.model.dto.salary.imcometype;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "修改收入类型请求")
public class IncomeTypeEditReqDTO extends IncomeTypeAddReqDTO {
    @NotNull(message = "ID不能为空")
    @Schema(description = "类型ID")
    private Long id;
}
