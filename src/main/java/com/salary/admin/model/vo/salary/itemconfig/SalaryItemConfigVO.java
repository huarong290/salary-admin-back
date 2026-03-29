package com.salary.admin.model.vo.salary.itemconfig;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 薪资项目统一配置 视图对象
 * 承载计算引擎配置、计税逻辑及前端展示属性
 */
@Data
@Schema(description = "薪资项目配置展示对象")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalaryItemConfigVO implements Serializable {
    /**
     * 主键ID (雪花算法生成)
     */
    @Schema(description = "主键ID (雪花算法生成)")
    private Long id;
    /**
     * 项编码 (业务唯一标识，如：BASIC_SALARY)
     */
    @Schema(description = "项编码 (业务唯一标识，如：BASIC_SALARY)")
    private String itemCode;
    /**
     * 项名称 (界面展示名称)
     */
    @Schema(description = "项名称 (界面展示名称)")
    private String itemName;
    /**
     * 项目大类: 1-收入, 2-扣款, 3-税费, 4-公司支出/补贴
     */
    @Schema(description = "项目大类: 1-收入, 2-扣款, 3-税费, 4-公司支出/补贴")
    private Integer itemCategory;
    /**
     * 引擎上下文变量名 (Groovy 脚本中引用的变量名，需符合 Java 变量命名规范)
     */
    @Schema(description = "引擎上下文变量名 (Groovy 脚本中引用的变量名，需符合 Java 变量命名规范)")
    private String envVarName;
    /**
     * 默认表达式脚本模板 (支持 Groovy 语法，如: basicSalary / 21.75 * otHours)
     */
    @Schema(description = "默认表达式脚本模板 (支持 Groovy 语法，如: basicSalary / 21.75 * otHours)")
    private String defaultRuleScript;
    /**
     * 计算优先级 (数值越小越靠前，确保前置变量先于计算项执行)
     */
    @Schema(description = "计算优先级 (数值越小越靠前，确保前置变量先于计算项执行)")
    private Integer calcPriority;
    /**
     * 业务分类字典值 (关联业务字典，如: allowance, insurance)
     */
    @Schema(description = "业务分类字典值 (关联业务字典，如: allowance, insurance)")
    private String categoryDictValue;
    /**
     * 保留小数位数
     */
    @Schema(description = "保留小数位数")
    private Integer decimalPlaces;
    /**
     * 舍入规则: HALF_UP(四舍五入), DOWN(截断), UP(向上进位)
     */
    @Schema(description = "舍入规则: HALF_UP(四舍五入), DOWN(截断), UP(向上进位)")
    private String roundingMode;
    /**
     * 计税标识: 0-不计税, 1-计入个税基数 (仅对收入类有效)
     */
    @Schema(description = "计税标识: 0-不计税, 1-计入个税基数 (仅对收入类有效)")
    private Integer taxableFlag;
    /**
     * 税前扣除标识: 0-否, 1-是 (仅对扣款类有效，如社保公积金)
     */
    @Schema(description = "税前扣除标识: 0-否, 1-是 (仅对扣款类有效，如社保公积金)")
    private Integer taxDeductibleFlag;
    /**
     * 是否固定项: 0-动态计算, 1-固定录入项
     */
    @Schema(description = "是否固定项: 0-动态计算, 1-固定录入项")
    private Integer fixedFlag;
    /**
     * 拼音缩写 (支持前端快速首字母检索)
     */
    @Schema(description = "拼音缩写 (支持前端快速首字母检索)")
    private String pinyinCode;
    /**
     * 状态: 0-禁用, 1-启用
     */
    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;
    /**
     * 显示排序 (数值越小越靠前)
     */
    @Schema(description = "显示排序 (数值越小越靠前)")
    private Integer sortValue;
    /**
     * 配置备注/业务说明
     */
    @Schema(description = "配置备注/业务说明")
    private String remark;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @Schema(description = "项目分类显示名称 (如: 固定工资, 社会保险)")
    private String categoryLabel;
}