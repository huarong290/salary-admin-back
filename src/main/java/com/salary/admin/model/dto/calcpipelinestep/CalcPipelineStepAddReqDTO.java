package com.salary.admin.model.dto.calcpipelinestep;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算管道步骤新增请求参数
 *
 * 用于新增管道执行步骤：规则快照、阶段、顺序、执行控制等
 */
@Data
@Schema(description = "薪资计算管道步骤新增请求参数")
public class CalcPipelineStepAddReqDTO {

    /** 所属管道编码 */
    @Schema(description = "所属管道编码")
    private String pipelineCode;

    /** 管道版本 */
    @Schema(description = "管道版本")
    private Integer pipelineVersion;

    /** 规则编码 */
    @Schema(description = "规则编码")
    private String ruleCode;

    /** 规则名称快照 */
    @Schema(description = "规则名称快照")
    private String ruleName;

    /** 规则类型快照 (1公式 2函数) */
    @Schema(description = "规则类型快照 (1公式 2函数)")
    private Integer ruleType;

    /** 执行条件表达式 */
    @Schema(description = "执行条件表达式")
    private String conditionScript;

    /** 阶段 (1基础 2补贴 3扣款 4税 5汇总) */
    @Schema(description = "阶段 (1基础 2补贴 3扣款 4税 5汇总)")
    private Integer stage;

    /** 执行顺序 */
    @Schema(description = "执行顺序")
    private Integer sortOrder;

    /** 失败是否阻断 (1阻断 0不中断) */
    @Schema(description = "失败是否阻断 (1阻断 0不中断)")
    private Integer blockFlag;

    /** 结果为空是否跳过 (1跳过 0不跳过) */
    @Schema(description = "结果为空是否跳过 (1跳过 0不跳过)")
    private Integer skipIfNull;

    /** 状态 (1启用 0停用) */
    @Schema(description = "状态 (1启用 0停用)")
    private Integer status;
}

