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
 * 薪资计算管道步骤表
 *
 * 存储管道的执行步骤：规则快照、阶段、顺序、执行控制等
 * 支持条件表达式、阻断策略、跳过策略等灵活配置
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryCalcPipelineStep", description = "薪资计算管道步骤表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_calc_pipeline_step")
public class SalaryCalcPipelineStep extends BaseEntity<SalaryCalcPipelineStep> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属管道编码
     */
    @Schema(description = "所属管道编码")
    @TableField("pipeline_code")
    private String pipelineCode;

    /**
     * 管道版本
     */
    @Schema(description = "管道版本")
    @TableField("pipeline_version")
    private Integer pipelineVersion;

    /**
     * 规则编码
     */
    @Schema(description = "规则编码")
    @TableField("rule_code")
    private String ruleCode;

    /**
     * 规则名称快照
     */
    @Schema(description = "规则名称快照")
    @TableField("rule_name")
    private String ruleName;

    /**
     * 规则类型快照 (1公式 2函数)
     */
    @Schema(description = "规则类型快照 (1公式 2函数)")
    @TableField("rule_type")
    private Integer ruleType;

    /**
     * 执行条件表达式
     */
    @Schema(description = "执行条件表达式")
    @TableField("condition_script")
    private String conditionScript;

    /**
     * 阶段 (1基础 2补贴 3扣款 4税 5汇总)
     */
    @Schema(description = "阶段 (1基础 2补贴 3扣款 4税 5汇总)")
    @TableField("stage")
    private Integer stage;

    /**
     * 执行顺序
     */
    @Schema(description = "执行顺序")
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 失败是否阻断 (1阻断 0不中断)
     */
    @Schema(description = "失败是否阻断 (1阻断 0不中断)")
    @TableField("block_flag")
    private Integer blockFlag;

    /**
     * 结果为空是否跳过 (1跳过 0不跳过)
     */
    @Schema(description = "结果为空是否跳过 (1跳过 0不跳过)")
    @TableField("skip_if_null")
    private Integer skipIfNull;

    /**
     * 状态 (1启用 0停用)
     */
    @Schema(description = "状态 (1启用 0停用)")
    @TableField("status")
    private Integer status;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
