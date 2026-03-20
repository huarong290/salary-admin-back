package com.salary.admin.model.dto.salary.deductiontype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页查询扣款类型 DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "分页查询扣款类型请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeductionTypeQueryReqDTO extends PageQueryDTO {

    @Schema(description = "关键词 (编码或名称)")
    private String keyword;
}
