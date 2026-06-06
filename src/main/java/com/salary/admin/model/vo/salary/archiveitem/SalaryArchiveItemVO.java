package com.salary.admin.model.vo.salary.archiveitem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 薪资档案项明细视图对象
 * 用于展示具体的收入/扣款项及其计算规则与金额
 *
 * @author system
 * @since 2026-03-13
 */
/**
 * 薪资档案固定项明细 VO
 * 用于展示员工档案中关联的各项津贴、补贴、扣款等固定金额配置
 */
@Schema(name = "SalaryArchiveItemVO", description = "员工薪资档案明细视图")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryArchiveItemVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 明细记录ID
     */
    @Schema(description = "明细记录ID")
    private Long id;
    /**
     * 所属档案ID
     */
    @Schema(description = "所属档案ID")
    private Long archiveId;
    /**
     * 项目类型 (1:收入项, 2:扣款项)
     */
    @Schema(description = "项目类型 (1:收入项, 2:扣款项)", example = "1")
    private Integer itemType;
    /**
     * 项目类型文本
     */
    @Schema(description = "项目类型文本 (字典翻译: 收入/扣款)")
    private String itemTypeLabel;
    /**
     * 关联配置ID
     */
    @Schema(description = "关联配置ID (salary_item_config.id)")
    private Long itemConfigId;
    /**
     * 项目名称快照
     */
    @Schema(description = "项目名称快照 (如：餐补、全勤奖)", example = "餐补")
    private String typeName;
    /**
     * 计算模式
     */
    @Schema(description = "计算模式 (1:按月固定, 2:按出勤天数, 3:按现场出勤, 4:按居家出勤)", example = "1")
    private Integer calcMode;

    /**
     * 计算模式文本
     */
    @Schema(description = "计算模式文本 (字典翻译: 按月固定/按出勤天数等)", example = "按出勤天数计算")
    private String calcModeLabel;
    /**
     * 保留小数位数 (来源于配置表快照)
     */
    @Schema(description = "保留小数位数")
    private Integer decimalPlaces;

    /**
     * 舍入规则 (来源于配置表快照)
     */
    @Schema(description = "舍入规则: HALF_UP, DOWN, UP")
    private String roundingMode;
    /**
     * 分类字典值快照
     */
    @Schema(description = "分类字典值快照 (如：INC_BASE, DED_ABSENT)", example = "INC_MEAL")
    private String categoryDictValue;
    /**
     * 分类字典标签
     */
    @Schema(description = "分类字典标签 (字典翻译: 基本工资项、缺勤扣款项)")
    private String categoryDictLabel;
    /**
     * 💡 基准标准金额 (若按月固定则代表月总额；若按天计算则代表日单价
     */
    @Schema(description = "基准标准金额 (若按月固定则代表月总额；若按天计算则代表日单价)", example = "500.00")
    private BigDecimal amount;
    /**
     *表达式脚本快照
     */
    @Schema(description = "表达式脚本快照 (计算引擎执行逻辑)", example = "fixed_amount * actual_days / standard_days")
    private String ruleScript;
    /**
     *排序值
     */
    @Schema(description = "排序值")
    private Integer sort;
}
