package com.salary.admin.model.dto.dicttype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型修改请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典类型修改请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictTypeUpdateReqDTO extends DictTypeAddReqDTO {

    @NotNull(message = "修改时字典ID不能为空")
    @Schema(description = "字典类型ID (主键)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
}