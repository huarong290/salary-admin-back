package com.salary.admin.model.entity.salary;

import com.baomidou.mybatisplus.annotation.*;
import com.salary.admin.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 薪资计算上下文快照表
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryCalcContext", description = "薪资计算上下文快照表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_calc_context")
public class SalaryCalcContext extends BaseEntity<SalaryCalcContext> {

    private static final long serialVersionUID = 1L;

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
     * 上下文变量JSON（ctx）
     */
    @Schema(description = "上下文变量JSON（ctx）")
    @TableField("context_json")
    private String contextJson;
    /**
     * 使用的流程编码
     */
    @Schema(description = "使用的流程编码")
    @TableField("pipeline_code")
    private String pipelineCode;
    /**
     * 版本号
     */
    @Schema(description = "版本号")
    @Version
    @TableField("version")
    private Integer version;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}