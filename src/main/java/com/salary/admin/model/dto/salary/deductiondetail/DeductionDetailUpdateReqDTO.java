package com.salary.admin.model.dto.salary.deductiondetail;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeductionDetailUpdateReqDTO extends DeductionDetailAddReqDTO {

    @NotNull(message = "修改时明细ID不能为空")
    @Schema(description = "明细主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

}