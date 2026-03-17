package com.salary.admin.model.dto.salary.snapshot;


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
public class SalaryDetailItemDTO {

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
     * BASE-底薪计算, FIXED-档案固定, VARIABLE-月度变动, MANUAL-人工干预
     */
    private String source;

    /**
     * 计算快照:计算逻辑描述 (如：基数5000 * 比例0.08)
     */
    private String formula;
}
