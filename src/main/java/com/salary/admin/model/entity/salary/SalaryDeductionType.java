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
     * 扣款分类
     */
    @Schema(description = "扣款分类")
    @TableField("category")
    private String category;
    /**
     * 扣款项说明
     */
    @Schema(description = "扣款项说明")
    @TableField("description")
    private String description;
    /**
     * 是否固定扣款
     */
    @Schema(description = "是否固定扣款")
    @TableField("is_fixed")
    private Boolean isFixed;
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