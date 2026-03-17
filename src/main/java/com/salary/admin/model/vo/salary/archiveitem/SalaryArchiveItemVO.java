package com.salary.admin.model.vo.salary.archiveitem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 薪资档案项明细视图对象
 * 用于展示具体的收入/扣款项及其计算规则与金额
 *
 * @author system
 * @since 2026-03-13
 */
@Data
@Schema(description = "薪资档案项明细视图对象")
public class SalaryArchiveItemVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 明细记录ID
     */
    @Schema(description = "明细记录ID")
    private Long id;
    /**
     * 对应的收入/扣款类型ID
     */
    @Schema(description = "对应的收入/扣款类型ID")
    private Long typeId;

    /**
     * 收入/扣款类型名称 (关联字典获取)
     */
    @Schema(description = "收入/扣款类型名称 (关联字典获取)")
    private String typeName;

    /**
     * 项目编码
     */
    @Schema(description = "项目编码")
    private String typeCode;
    /**
     * 业务分类名称 (如：津贴补贴、五险一金)
     */
    @Schema(description = "业务分类名称")
    private String categoryName;
    /**
     * 项目类型: 1-收入项, 2-扣款项
     */
    @Schema(description = "项目类型: 1-收入项, 2-扣款项")
    private Integer itemType;

    /**
     * 计算方式: 1-固定金额, 2-按基数比例
     */
    @Schema(description = "计算方式: 1-固定金额, 2-按基数比例")
    private Integer calcType;

    /**
     * 计算基数 (为空则默认取主表 base_salary)
     */
    @Schema(description = "计算基数 (为空则默认取主表 base_salary)")
    private BigDecimal baseAmount;

    /**
     * 固定金额 (若为比例计算，此字段可作为计算结果缓存)
     */
    @Schema(description = "固定金额 (若为比例计算，此字段可作为计算结果缓存)")
    private BigDecimal amount;

    /**
     * 计算比例 (如 0.0800 代表 8%)
     */
    @Schema(description = "计算比例 (如 0.0800 代表 8%)")
    private BigDecimal ratio;

    /**
     * 计算公式描述 (用于前端直观展示计算过程)
     */
    @Schema(description = "计算公式描述")
    private String formulaLabel;

    /**
     * 比例百分比标签 (用于前端展示，如：8%)
     */
    @Schema(description = "比例百分比标签")
    private String ratioLabel;
}
