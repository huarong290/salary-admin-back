package com.salary.admin.model.dto.calcrule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算规则新增请求参数
 */
@Data
@Schema(description = "薪资计算规则新增请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalcRuleAddReqDTO {

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

    @Schema(description = "默认显示排序(仅用于字典列表展示)")
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
