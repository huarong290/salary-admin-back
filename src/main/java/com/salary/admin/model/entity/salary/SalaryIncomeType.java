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
 * 收入类型字典表
 *
 * @author system
 * @since 2026-03-11
 */
@Schema(name = "SalaryIncomeType", description = "收入类型字典表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_income_type")
public class SalaryIncomeType extends BaseEntity<SalaryIncomeType> {

    private static final long serialVersionUID = 1L;

    /**
     * 收入类型ID
     */
    @Schema(description = "收入类型ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 收入类型缩写简称
     */
    @Schema(description = "收入类型缩写简称")
    @TableField("type_code")
    private String typeCode;
    /**
     * 收入类型名称
     */
    @Schema(description = "收入类型名称")
    @TableField("type_name")
    private String typeName;
    /**
     * 拼音缩写
     */
    @Schema(description = "拼音缩写")
    @TableField("pinyin_code")
    private String pinyinCode;
    /**
     * 关联到全新的分类表
     */
    @Schema(description = "关联分类ID")
    @TableField("category_id")
    private Long categoryId;

    /**
     * 是否纳入个税计税基数: 0-否, 1-是
     */
    @Schema(description = "'是否纳入个税计税基数: 0-否, 1-是")
    @TableField("taxable_flag")
    private Integer taxableFlag;
    /**
     * 是否纳入个税计税基数: 0-否, 1-是
     */
    @Schema(description = "是否计入社保基数: 0-否, 1-是")
    @TableField("social_base_flag")
    private Integer socialBaseFlag;
    /**
     * 收入项说明
     */
    @Schema(description = "收入项说明")
    @TableField("description")
    private String description;
    /**
     * 是否属于奖金类 (用于年终奖独立计税等场景): 0-否, 1-是
     */
    @Schema(description = "是否属于奖金类 (用于年终奖独立计税等场景): 0-否, 1-是")
    @TableField("bonus_flag")
    private Integer bonusFlag;
    /**
     * 是否与考勤强相关 (决定是否按出勤天数折算): 0-否, 1-是
     */
    @Schema(description = "是否与考勤强相关 (决定是否按出勤天数折算): 0-否, 1-是")
    @TableField("attendance_related_flag")
    private Integer attendanceRelatedFlag;
    /**
     * 排序值 (数值越小越靠前)
     */
    @TableField("sort_value")
    private Integer sortValue;
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}