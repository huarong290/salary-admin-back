package com.salary.admin.model.dto.dictitem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Schema(description = "字典项保存请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictItemAddReqDTO {

    @NotBlank(message = "所属字典类型编码不能为空")
    @Schema(description = "字典类型编码", example = "currency_type", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictTypeCode;

    @NotBlank(message = "字典项键值不能为空")
    @Size(max = 100, message = "键值长度不能超过100个字符")
    @Schema(description = "字典项键值 (存库用)", example = "USDT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictItemValue;

    @NotBlank(message = "字典项标签不能为空")
    @Size(max = 100, message = "标签长度不能超过100个字符")
    @Schema(description = "字典项标签 (展示用)", example = "泰达币 (USDT)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictItemLabel;

    @NotNull(message = "显示顺序不能为空")
    @Schema(description = "排序值 (越小越靠前)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer dictItemSort;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态 (1:启用 0:停用)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Size(max = 255, message = "备注长度不能超过255个字符")
    @Schema(description = "备注说明")
    private String remark;
}