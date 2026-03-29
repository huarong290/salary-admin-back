package com.salary.admin.model.vo.salary.itemconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 薪资项目配置下拉选项 VO
 */
@Data
@Schema(description = "薪资项目下拉选项")
public class ItemConfigOptionVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "薪资项编码")
    private String itemCode;

    @Schema(description = "项目名称")
    private String itemName;

    @Schema(description = "项目分类 (1-收入, 2-扣款, 3-税费, 4-公司支出/补贴)")
    private Integer itemCategory;

    @Schema(description = "业务分类字典值 (如: allowance)")
    private String categoryDictValue;
    /**
     * 引擎上下文变量名
     */
    @Schema(description = "引擎上下文变量名")
    private String envVarName;
}