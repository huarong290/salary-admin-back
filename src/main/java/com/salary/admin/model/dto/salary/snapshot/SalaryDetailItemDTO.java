package com.salary.admin.model.dto.salary.snapshot;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 薪资明细项规范结构 (存储于 detail_json 数组)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalaryDetailItemDTO {
    /**
     * 项目字典ID (如果是底薪/个税等系统内置项，可为空或设为特定负数)
     * 作用：避免 HR 修改了字典名称后，历史快照无法被精准分类聚合
     */
    private Long typeId;
    /**
     * 项目名称 (如：基本工资、住房补贴、养老保险)
     */
    private String itemName;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 1-收入项，2-扣款项
     */
    private Integer itemType;

    /**
     * 业务分类 (对应具体的收入/扣款类型名称)
     *  例如：津贴、补贴、奖金、社保、公积金、考勤扣款
     */
    private String category;

    /**
     * 来源标识 (Source)
     * BASE-底薪计算, FIXED-档案固定, VARIABLE-月度变动, MANUAL-人工干预，SYSTEM_CALC-系统算税/全勤
     */
    private String source;

    /**
     * 计算快照:计算逻辑描述 (如：基数5000 * 比例0.08)
     */
    private String formula;
}
