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
 * 薪资计算流程管道表
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryCalcPipeline", description = "薪资计算流程管道表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_calc_pipeline")
public class SalaryCalcPipeline extends BaseEntity<SalaryCalcPipeline> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 流程编码（如：DEFAULT_PIPELINE）
     */
    @Schema(description = "流程编码（如：DEFAULT_PIPELINE）")
    @TableField("pipeline_code")
    private String pipelineCode;
    /**
     * 流程名称
     */
    @Schema(description = "流程名称")
    @TableField("pipeline_name")
    private String pipelineName;
    /**
     * 阶段（1基础 2补贴 3扣款 4税 5汇总）
     */
    @Schema(description = "阶段（1基础 2补贴 3扣款 4税 5汇总）")
    @TableField("stage")
    private Integer stage;
    /**
     * 规则编码（关联 salary_calc_rule）
     */
    @Schema(description = "规则编码（关联 salary_calc_rule）")
    @TableField("rule_code")
    private String ruleCode;
    /**
     * 执行顺序
     */
    @Schema(description = "执行顺序")
    @TableField("sort_order")
    private Integer sortOrder;
    /**
     * 状态 (1启用 0停用)
     */
    @Schema(description = "状态 (1启用 0停用)")
    @TableField("status")
    private Byte status;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}