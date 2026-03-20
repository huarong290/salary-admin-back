package com.salary.admin.model.dto.salary.deductiontype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeductionTypeEditReqDTO extends DeductionTypeAddReqDTO {
    @NotNull(message = "ID不能为空")
    @Schema(description = "类型ID")
    private Long id;
}
