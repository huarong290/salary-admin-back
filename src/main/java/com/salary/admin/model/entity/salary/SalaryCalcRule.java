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
 * 薪资计算规则库表
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryCalcRule", description = "薪资计算规则库表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_calc_rule")
public class SalaryCalcRule extends BaseEntity<SalaryCalcRule> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 规则编码
     */
    @Schema(description = "规则编码")
    @TableField("rule_code")
    private String ruleCode;
    /**
     * 规则名称
     */
    @Schema(description = "规则名称")
    @TableField("rule_name")
    private String ruleName;
    /**
     * 规则类型 (1:公式,2:函数)
     */
    @Schema(description = "规则类型 (1:公式,2:函数)")
    @TableField("rule_type")
    private Integer ruleType;
    /**
     * 表达式脚本
     */
    @Schema(description = "表达式脚本")
    @TableField("rule_script")
    private String ruleScript;
    /**
     * 返回值类型
     */
    @Schema(description = "返回值类型")
    @TableField("return_type")
    private String returnType;
    /**
     * 默认显示排序(仅用于字典列表展示)
     */
    @Schema(description = "默认显示排序(仅用于字典列表展示)")
    @TableField("sort_value")
    private Integer sortValue;
    /**
     * 状态 (1:启用,0:停用)
     */
    @Schema(description = "状态 (1:启用,0:停用)")
    @TableField("status")
    private Integer status;
    /**
     * 依赖变量
     */
    @Schema(description = "依赖变量")
    @TableField("depends_on")
    private String dependsOn;
    /**
     * 参数配置
     */
    @Schema(description = "参数配置")
    @TableField("param_json")
    private String paramJson;
    /**
     * 所属阶段
     */
    @Schema(description = "所属阶段")
    @TableField("stage")
    private Integer stage;
    /**
     * 备注
     */
    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}