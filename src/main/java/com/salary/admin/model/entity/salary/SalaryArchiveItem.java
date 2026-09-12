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
import java.math.BigDecimal;

/**
 * 薪资档案固定项明细表
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryArchiveItem", description = "薪资档案固定项明细表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_archive_item")
public class SalaryArchiveItem extends BaseEntity<SalaryArchiveItem> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 档案ID
     */
    @Schema(description = "档案ID")
    @TableField("archive_id")
    private Long archiveId;
    /**
     * 项目类型: 1-收入项, 2-扣款项
     */
    @Schema(description = "项目类型: 1-收入项, 2-扣款项")
    @TableField("item_type")
    private Integer itemType;
    /**
     * 关联salary_item_config.id
     */
    @Schema(description = "关联salary_item_config.id")
    @TableField("item_config_id")
    private Long itemConfigId;
    /**
     * 项目名称快照
     */
    @Schema(description = "项目名称快照")
    @TableField("type_name")
    private String typeName;
    /**
     * 分类字典值快照
     */
    @Schema(description = "分类字典值快照")
    @TableField("category_dict_value")
    private String categoryDictValue;
    /**
     * 计算模式
     * 1-按月固定, 2-按出勤天数计算, 3-按现场出勤天数计算, 4-按居家出勤天数计算
     */
    @Schema(description = "计算模式: 1-按月固定, 2-按出勤天数计算, 3-按现场出勤天数计算, 4-按居家出勤天数计算", example = "1")
    @TableField("calc_mode")
    private Integer calcMode;
    /**
     * 表达式脚本
     */
    @Schema(description = "表达式脚本")
    @TableField("rule_script")
    private String ruleScript;
    /**
     * 基准标准金额
     * 若按月固定则代表月总额（如500）；若按天计算则代表日单价（如20）
     */
    @Schema(description = "基准标准金额 (若按月固定则代表月总额；若按天计算则代表日单价)", example = "500.00")
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 计税标识: NULL-继承全局配置, 0-不计税, 1-计税
     */
    @Schema(description = "计税标识: NULL-继承全局配置, 0-不计税, 1-计税")
    @TableField("taxable_flag")
    private Integer taxableFlag;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}