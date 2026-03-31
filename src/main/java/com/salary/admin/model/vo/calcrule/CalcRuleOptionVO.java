package com.salary.admin.model.vo.calcrule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 薪资计算规则下拉选项对象
 */
@Data
@Schema(description = "薪资计算规则下拉选项对象")
public class CalcRuleOptionVO {

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "规则名称")
    private String ruleName;
}
