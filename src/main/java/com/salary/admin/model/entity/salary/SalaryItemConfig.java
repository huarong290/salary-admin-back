package com.salary.admin.model.entity.salary;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.salary.admin.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 薪资项目统一配置表
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryItemConfig", description = "薪资项目统一配置表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_item_config")
public class SalaryItemConfig extends BaseEntity<SalaryItemConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 项编码 (如：BASIC_SALARY, LATE_DEDUCTION)
     */
    @Schema(description = "项编码 (如：BASIC_SALARY, LATE_DEDUCTION)")
    @TableField("item_code")
    private String itemCode;
    /**
     * 项名称
     */
    @Schema(description = "项名称")
    @TableField("item_name")
    private String itemName;
    /**
     * 项目分类: 1-收入, 2-扣款, 3-税费, 4-公司支出/补贴
     */
    @Schema(description = "项目分类: 1-收入, 2-扣款, 3-税费, 4-公司支出/补贴")
    @TableField("item_category")
    private Integer itemCategory;
    /**
     * 引擎上下文变量名
     */
    @Schema(description = "引擎上下文变量名")
    @TableField("env_var_name")
    private String envVarName;
    /**
     * 默认表达式脚本模板
     */
    @Schema(description = "默认表达式脚本模板")
    @TableField("default_rule_script")
    private String defaultRuleScript;
    /**
     * 计算优先级 (数值越小越靠前)
     */
    @Schema(description = "计算优先级 (数值越小越靠前)")
    @TableField("calc_priority")
    private Integer calcPriority;
    /**
     * 业务分类字典值 (如: allowance, insurance)
     */
    @Schema(description = "业务分类字典值 (如: allowance, insurance)")
    @TableField("category_dict_value")
    private String categoryDictValue;
    /**
     * 是否计税 (仅对收入有效)
     */
    @Schema(description = "是否计税 (仅对收入有效)")
    @TableField("taxable_flag")
    private Integer taxableFlag;
    /**
     * 是否税前扣除 (仅对扣款有效)
     */
    @Schema(description = "是否税前扣除 (仅对扣款有效)")
    @TableField("tax_deductible_flag")
    private Integer taxDeductibleFlag;
    /**
     * 是否固定项
     */
    @Schema(description = "是否固定项")
    @TableField("fixed_flag")
    private Integer fixedFlag;
    /**
     * 拼音缩写
     */
    @Schema(description = "拼音缩写")
    @TableField("pinyin_code")
    private String pinyinCode;
    /**
     * 显示排序
     */
    @Schema(description = "显示排序")
    @TableField("sort_value")
    private Integer sortValue;
    /**
     * 状态 (1:启用, 0:禁用)
     */
    @Schema(description = "状态 (1:启用, 0:禁用)")
    @TableField("status")
    private Integer status;
    /**
     * 备注
     */
    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}