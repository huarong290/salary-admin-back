package com.salary.admin.model.vo.calcrule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算规则视图对象
 */
@Data
@Schema(description = "薪资计算规则视图对象")
public class CalcRuleVO {

    @Schema(description = "规则主键ID")
    private Long id;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则类型 (1:公式, 2:函数)")
    private Integer ruleType;

    @Schema(description = "表达式脚本")
    private String ruleScript;

    @Schema(description = "返回值类型")
    private String returnType;

    @Schema(description = "执行优先级")
    private Integer sortValue;

    @Schema(description = "状态 (1:启用, 0:停用)")
    private Integer status;

    @Schema(description = "依赖变量")
    private String dependsOn;

    @Schema(description = "参数配置 (JSON字符串)")
    private String paramJson;

    @Schema(description = "所属阶段 (1基础 2补贴 3扣款 4税 5汇总)")
    private Integer stage;

    @Schema(description = "备注")
    private String remark;
}

