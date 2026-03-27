package com.salary.admin.model.dto.dicttype;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型分页查询请求参数
 * 用于系统字典分类列表的搜索与筛选
 *
 * @author system
 * @since 2026-03-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典类型分页查询请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictTypeQueryReqDTO extends PageQueryDTO {

    @Schema(description = "字典类型名称 (支持模糊查询)", example = "币种")
    private String dictTypeName;

    @Schema(description = "字典类型编码 (支持模糊查询)", example = "currency")
    private String dictTypeCode;

    @Schema(description = "类别", example = "income/deduction/other")
    private String dictCategory;

    @Schema(description = "状态 (0:启用 1:停用)", example = "0")
    private Integer status;
}