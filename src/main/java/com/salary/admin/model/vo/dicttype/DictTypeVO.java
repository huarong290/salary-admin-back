package com.salary.admin.model.vo.dicttype;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统字典类型视图对象
 * 用于对字典进行分类管理（如：币种类型、在职状态）
 *
 * @author system
 * @since 2026-03-20
 */
@Data
@Schema(description = "系统字典类型视图对象")
public class DictTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 字典类型编码 (如：currency_type)
     */
    @Schema(description = "字典类型编码 (如：currency_type)")
    private String dictTypeCode;

    /**
     * 字典类型名称 (如：币种类型)
     */
    @Schema(description = "字典类型名称 (如：币种类型)")
    private String dictTypeName;

    /**
     * 类别（如 income/deduction/other）
     */
    @Schema(description = "类别（如 income/deduction/other）")
    private String dictCategory;

    /**
     * 是否启用: 0-启用, 1-停用
     */
    @Schema(description = "是否启用: 0-启用, 1-停用")
    private Integer status;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    private String remark;
}