package com.salary.admin.model.vo.calcrule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪资计算规则视图对象
 */
@Data
@Schema(description = "薪资计算规则视图对象")
public class CalcRuleVO {
    /**
     * 主键ID
     */
    @Schema(description = "规则主键ID")
    private Long id;
    /**
     * 规则编码
     */
    @Schema(description = "规则编码")
    private String ruleCode;
    /**
     * 规则名称
     */
    @Schema(description = "规则名称")
    private String ruleName;
    /**
     * 规则类型 (1:公式, 2:函数)
     */
    @Schema(description = "规则类型 (1:公式, 2:函数)")
    private Integer ruleType;
    /**
     * 表达式脚本
     */
    @Schema(description = "表达式脚本")
    private String ruleScript;
    /**
     * 返回值类型
     */
    @Schema(description = "返回值类型")
    private String returnType;
    /**
     * 默认显示排序(仅用于字典列表展示)
     */
    @Schema(description = "默认显示排序(仅用于字典列表展示)")
    private Integer sortValue;
    /**
     * 状态 (1:启用, 0:停用)
     */
    @Schema(description = "状态 (1:启用, 0:停用)")
    private Integer status;
    /**
     * 依赖变量
     */
    @Schema(description = "依赖变量")
    private String dependsOn;
    /**
     * 参数配置 (JSON字符串)
     */
    @Schema(description = "参数配置 (JSON字符串)")
    private String paramJson;
    /**
     * 所属阶段 (1基础 2补贴 3扣款 4税 5汇总)
     */
    @Schema(description = "所属阶段 (1基础 2补贴 3扣款 4税 5汇总)")
    private Integer stage;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}

