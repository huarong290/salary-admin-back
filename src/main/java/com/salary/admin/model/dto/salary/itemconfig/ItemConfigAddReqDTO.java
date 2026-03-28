package com.salary.admin.model.dto.salary.itemconfig;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

/**
 * 薪资项目统一配置 新增请求 DTO
 * 用于定义薪资组成项、计算优先级及脚本引擎变量
 */
@Data
@Schema(description = "新增薪资项目配置请求")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemConfigAddReqDTO implements Serializable {

    /**
     * 项编码 (唯一标识)
     */
    @NotBlank(message = "项目编码不能为空")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "项目编码必须为大写字母、数字或下划线组合")
    @Schema(description = "项编码 (如: BASIC_SALARY, OVERTIME_PAY)")
    private String itemCode;

    /**
     * 项名称
     */
    @NotBlank(message = "项目名称不能为空")
    @Schema(description = "项目名称 (如: 基本工资, 加班费)")
    private String itemName;

    /**
     * 项目大类
     */
    @NotNull(message = "项目分类不能为空")
    @Min(value = 1, message = "非法分类值")
    @Max(value = 4, message = "非法分类值")
    @Schema(description = "项目分类: 1-收入, 2-扣款, 3-税费, 4-公司支出/补贴")
    private Integer itemCategory;

    /**
     * 引擎上下文变量名
     */
    @NotBlank(message = "环境变量名不能为空")
    @Pattern(regexp = "^[a-z][a-zA-Z0-9]*$", message = "变量名需符合小驼峰命名规范 (如: otPay)")
    @Schema(description = "引擎上下文变量名 (Groovy脚本中引用的变量)")
    private String envVarName;

    /**
     * 默认表达式脚本模板
     */
    @Schema(description = "默认表达式脚本模板 (Groovy 语法)")
    private String defaultRuleScript;

    /**
     * 计算优先级
     */
    @NotNull(message = "计算优先级不能为空")
    @Schema(description = "计算优先级 (数值越小越靠前，建议 10, 20, 30 间隔分布)")
    private Integer calcPriority;

    /**
     * 业务分类字典值
     */
    @NotBlank(message = "业务分类不能为空")
    @Schema(description = "业务分类字典值 (如: allowance, insurance, tax)")
    private String categoryDictValue;

    /**
     * 是否计税
     */
    @NotNull(message = "计税标识不能为空")
    @Schema(description = "是否计税 (仅对收入有效): 0-否, 1-是")
    private Integer taxableFlag;

    /**
     * 是否税前扣除
     */
    @NotNull(message = "税前扣除标识不能为空")
    @Schema(description = "是否税前扣除 (仅对扣款有效): 0-否, 1-是")
    private Integer taxDeductibleFlag;

    /**
     * 是否固定项
     */
    @NotNull(message = "固定项标识不能为空")
    @Schema(description = "是否固定项: 0-动态计算, 1-固定录入项")
    private Integer fixedFlag;

    /**
     * 状态
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;

    /**
     * 排序值
     */
    @Schema(description = "显示排序值")
    private Integer sortValue;

    /**
     * 备注
     */
    @Schema(description = "备注说明")
    private String remark;
}