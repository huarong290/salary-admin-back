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
 * 统一收支明细表
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryItemDetail", description = "统一收支明细表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_item_detail")
public class SalaryItemDetail extends BaseEntity<SalaryItemDetail> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    @TableField("employee_id")
    private Long employeeId;
    /**
     * 薪资周期ID
     */
    @Schema(description = "薪资周期ID")
    @TableField("period_id")
    private Long periodId;
    /**
     * 汇总ID
     */
    @Schema(description = "汇总ID")
    @TableField("summary_id")
    private Long summaryId;
    /**
     * 项目分类: 1-收入, 2-扣款, 3-税费, 4-公司支出/补贴
     */
    @Schema(description = "项目分类: 1-收入, 2-扣款, 3-税费, 4-公司支出/补贴")
    @TableField("item_type")
    private Integer itemType;
    /**
     * 项目配置ID(关联salary_item_config.id)
     */
    @Schema(description = "项目配置ID(关联salary_item_config.id)")
    @TableField("item_config_id")
    private Long itemConfigId;
    /**
     * 类型编码快照
     */
    @Schema(description = "类型编码快照")
    @TableField("item_code")
    private String itemCode;
    /**
     * 类型名称快照
     */
    @Schema(description = "类型名称快照")
    @TableField("item_name")
    private String itemName;
    /**
     * 分类字典值快照
     */
    @Schema(description = "分类字典值快照")
    @TableField("category_dict_value")
    private String categoryDictValue;
    /**
     * 数据来源类型：1-薪资档案 2-引擎计算 3-手动调整 4-外部导入
     */
    @Schema(description = "数据来源类型：1-薪资档案 2-引擎计算 3-手动调整 4-外部导入")
    @TableField("source_type")
    private Integer sourceType;
    /**
     * 来源档案ID
     */
    @Schema(description = "来源档案ID")
    @TableField("archive_id")
    private Long archiveId;
    /**
     * 来源档案明细ID
     */
    @Schema(description = "来源档案明细ID")
    @TableField("archive_item_id")
    private Long archiveItemId;
    /**
     * 计算规则编码
     */
    @Schema(description = "计算规则编码")
    @TableField("rule_code")
    private String ruleCode;
    /**
     * 原始币种
     */
    @Schema(description = "原始币种")
    @TableField("original_currency")
    private String originalCurrency;
    /**
     * 原始金额
     */
    @Schema(description = "原始金额")
    @TableField("original_amount")
    private BigDecimal originalAmount;
    /**
     * 汇率
     */
    @Schema(description = "汇率")
    @TableField("exchange_rate")
    private BigDecimal exchangeRate;
    /**
     * 结算金额
     */
    @Schema(description = "结算金额")
    @TableField("settlement_amount")
    private BigDecimal settlementAmount;
    /**
     * 结算币种
     */
    @Schema(description = "结算币种")
    @TableField("settlement_currency")
    private String settlementCurrency;
    /**
     * 计算优先级
     */
    @Schema(description = "计算优先级")
    @TableField("calc_priority")
    private Integer calcPriority;
    /**
     * 计算快照（用于解释计算过程）
     */
    @Schema(description = "计算快照（用于解释计算过程）")
    @TableField("calc_snapshot")
    private String calcSnapshot;
    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    @TableField("remark")
    private String remark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}