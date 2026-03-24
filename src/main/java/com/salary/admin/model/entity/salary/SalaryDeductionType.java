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
 * 扣款类型字典表
 *
 * @author system
 * @since 2026-03-11
 */
@Schema(name = "SalaryDeductionType", description = "扣款类型字典表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_deduction_type")
public class SalaryDeductionType extends BaseEntity<SalaryDeductionType> {

    private static final long serialVersionUID = 1L;

    /**
     * 扣款类型ID
     */
    @Schema(description = "扣款类型ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 扣款类型缩写简称
     */
    @Schema(description = "扣款类型缩写简称")
    @TableField("type_code")
    private String typeCode;
    /**
     * 扣款类型名称
     */
    @Schema(description = "扣款类型名称")
    @TableField("type_name")
    private String typeName;
    /**
     * 拼音缩写
     */
    @Schema(description = "拼音缩写")
    @TableField("pinyin_code")
    private String pinyinCode;
    /**
     * 扣款分类关联分类ID
     */
    @Schema(description = "关联分类ID")
    @TableField("category_id")
    private Long categoryId;
    /**
     * 扣款项说明
     */
    @Schema(description = "扣款项说明")
    @TableField("description")
    private String description;
    /**
     * 是否固定扣款:0-否, 1-是
     */
    @Schema(description = "是否固定扣款:0-否, 1-是")
    @TableField("fixed_flag")
    private Integer fixedFlag;
    /**
     * 是否为税前合法扣除项(如五险一金): 0-否, 1-是
     */
    @Schema(description = "是否为税前合法扣除项(如五险一金): 0-否, 1-是")
    @TableField("tax_deductible_flag")
    private Integer taxDeductibleFlag;
    /**
     * 是否计入社保基数扣减: 0-否, 1-是
     */
    @Schema(description = "是否计入社保基数扣减: 0-否, 1-是")
    @TableField("social_base_flag")
    private Integer socialBaseFlag;
    /**
     * 是否影响奖金发放: 0-否, 1-是
     */
    @Schema(description = "是否影响奖金发放: 0-否, 1-是")
    @TableField("bonus_flag")
    private Integer bonusFlag;
    /**
     * 是否与考勤强相关 (如迟到早退扣款): 0-否, 1-是
     */
    @Schema(description = "是否与考勤强相关 (如迟到早退扣款): 0-否, 1-是")
    @TableField("attendance_related_flag")
    private Integer attendanceRelatedFlag;
    /**
     * 排序值 (数值越小越靠前)
     */
    @Schema(description = "排序值")
    @TableField("sort_value")
    private Integer sortValue;
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}