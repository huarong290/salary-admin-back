package com.salary.admin.model.dto.salary.itemconfig;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资项目配置 分页查询请求 DTO
 */
@Data
@Schema(description = "薪资项目配置查询参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemConfigQueryReqDTO extends PageQueryDTO {

    @Schema(description = "全字段搜索关键字 (支持名称、编码、拼音缩写)")
    private String searchText;

    @Schema(description = "项目分类过滤: 1-收入, 2-扣款, 3-税费, 4-公司支出")
    private Integer itemCategory;

    @Schema(description = "业务分类字典值过滤 (如: insurance)")
    private String categoryDictValue;

    @Schema(description = "启用状态过滤: 0-禁用, 1-启用")
    private Integer status;

    @Schema(description = "是否固定项过滤: 0-否, 1-是")
    private Integer fixedFlag;
}
