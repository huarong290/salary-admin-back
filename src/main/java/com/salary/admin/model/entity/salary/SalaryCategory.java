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
 * 薪资统一分类表 (L2 结构层)
 *
 * @author system
 * @since 2026-03-24
 */
@Schema(name = "SalaryCategory", description = "薪资统一分类表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_category")
public class SalaryCategory extends BaseEntity<SalaryCategory> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "分类编码（如: BONUS / BASIC / LIFE）")
    @TableField("category_code")
    private String categoryCode;

    @Schema(description = "分类名称")
    @TableField("category_name")
    private String categoryName;

    @Schema(description = "分类类型: 1-收入分类, 2-扣款分类")
    @TableField("category_type")
    private Integer categoryType;

    @Schema(description = "父级分类ID（用于二级分类，0为顶级）")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "排序值")
    @TableField("sort_value")
    private Integer sortValue;

    @Schema(description = "状态: 1-正常, 0-停用")
    @TableField("status")
    private Integer status;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}