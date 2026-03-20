package com.salary.admin.model.vo.dictitem;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统字典项视图对象
 * 用于具体的下拉框选项展示（如：USDT、CNY）
 *
 * @author system
 * @since 2026-03-20
 */
@Data
@Schema(description = "系统字典项视图对象")
public class DictItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 所属字典类型编码
     */
    @Schema(description = "所属字典类型编码")
    private String dictTypeCode;

    /**
     * 字典项标签 (展示用，如：泰达币)
     */
    @Schema(description = "字典项标签 (展示用，如：泰达币)")
    private String dictItemLabel;

    /**
     * 字典项值 (存库用，如：USDT)
     */
    @Schema(description = "字典项值 (存库用，如：USDT)")
    private String dictItemValue;

    /**
     * 排序值 (数值越小越靠前)
     */
    @Schema(description = "排序值 (数值越小越靠前)")
    private Integer sort;

    /**
     * 是否启用: 1-启用, 0-停用
     */
    @Schema(description = "是否启用: 1-启用, 0-停用")
    private Integer status;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    private String remark;
}