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
 * @since 2026-03-13
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
     * 关联具体的某一个版本的档案ID
     */
    @Schema(description = "关联具体的某一个版本的档案ID")
    @TableField("archive_id")
    private Long archiveId;
    /**
     * 项目类型: 1-收入项, 2-扣款项
     */
    @Schema(description = "项目类型: 1-收入项, 2-扣款项")
    @TableField("item_type")
    private Integer itemType;
    /**
     * 对应的收入/扣款类型ID
     */
    @Schema(description = "对应的收入/扣款类型ID")
    @TableField("type_id")
    private Long typeId;
    /**
     * 收入/扣款项名称快照 (如：基本工资、养老保险)
     */
    @Schema(description = "收入/扣款项名称快照")
    @TableField("type_name")
    private String typeName;
    /**
     * 分类名称快照 (如：津贴补贴、五险一金)
     */
    @Schema(description = "分类名称快照")
    @TableField("category_name")
    private String categoryName;
    /**
     * 计算方式: 1-固定金额, 2-按基数比例
     */
    @Schema(description = "计算方式: 1-固定金额, 2-按基数比例")
    @TableField("calc_type")
    private Integer calcType;
    /**
     * 计算基数 (为空则默认取主表base_salary)
     */
    @Schema(description = "计算基数 (为空则默认取主表base_salary)")
    @TableField("base_amount")
    private BigDecimal baseAmount;
    /**
     * 固定金额 (若为比例计算，此字段可作为计算结果缓存)
     */
    @Schema(description = "固定金额 (若为比例计算，此字段可作为计算结果缓存)")
    @TableField("amount")
    private BigDecimal amount;
    /**
     * 计算比例 (如 0.0800 代表 8%)
     */
    @Schema(description = "计算比例 (如 0.0800 代表 8%)")
    @TableField("ratio")
    private BigDecimal ratio;


    @Override
    public Serializable pkVal() {
        return this.id;
    }
}