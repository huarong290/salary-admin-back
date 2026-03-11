package com.salary.admin.model.dto.salary.deductiontype;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增扣款类型 DTO
 */
@Data
@Schema(description = "新增扣款类型请求")
public class DeductionTypeAddReqDTO implements Serializable {

    @NotBlank(message = "类型编码不能为空")
    @Schema(description = "类型编码 (唯一标识)")
    private String typeCode;

    @NotBlank(message = "类型名称不能为空")
    @Schema(description = "类型名称")
    private String typeName;

    @Schema(description = "扣款分类")
    private String category;

    @Schema(description = "扣款项说明")
    private String description;

    @NotNull(message = "排序值不能为空")
    @Schema(description = "排序值 (数值越小越靠前)")
    private Integer sortValue;
}
