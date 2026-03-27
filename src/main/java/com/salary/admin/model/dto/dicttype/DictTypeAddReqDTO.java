package com.salary.admin.model.dto.dicttype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Schema(description = "字典类型保存请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictTypeAddReqDTO {

    @NotBlank(message = "字典类型编码不能为空")
    @Size(max = 50, message = "编码长度不能超过50个字符")
    @Schema(description = "字典类型编码", example = "currency_type", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictTypeCode;

    @NotBlank(message = "字典类型名称不能为空")
    @Size(max = 100, message = "名称长度不能超过100个字符")
    @Schema(description = "字典类型名称", example = "币种类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictTypeName;

    @Schema(description = "类别", example = "income/deduction/other")
    private String dictCategory;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态 (0:启用 1:停用)", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Size(max = 255, message = "备注长度不能超过255个字符")
    @Schema(description = "备注说明")
    private String remark;
}