package com.salary.admin.model.dto.dictitem;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典项修改请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典项修改请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictItemUpdateReqDTO extends DictItemAddReqDTO {

    @NotNull(message = "修改时字典项ID不能为空")
    @Schema(description = "字典项ID (主键)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
}