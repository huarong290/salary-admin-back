package com.salary.admin.model.dto.salary.snapshot;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
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
     * 项目编码 (如: BASE_SALARY, 代替极易变动的 ID)
     */
    @Schema(description = "项目编码 (如: BASE_SALARY, 代替极易变动的 ID)")
    private String itemCode;
    /**
     * 项目名称 (如: 基本工资)
     */
    @Schema(description = "项目名称 (如: 基本工资)")
    private String itemName;
    /**
     * 计算结果金额
     */
    @Schema(description = "计算结果金额")
    private BigDecimal settlementAmount;
    /**
     * 原始金额
     */
    @Schema(description = "原始金额 (换算前)")
    private BigDecimal originalAmount;
    /**
     * 原始币种
     */
    @Schema(description = "原始币种 (如 CNY)")
    private String originalCurrency;
    /**
     * 业务分类字典值
     */
    @Schema(description = "业务分类字典值 (如: allowance, bonus)")
    private String categoryDictValue;


    /**
     * 来源标识 (Source)
     * BASE-底薪计算, FIXED-档案固定, VARIABLE-月度变动, MANUAL-人工干预，SYSTEM_CALC-系统算税/全勤
     */
    private String source;
    /**
     *计算过程快照/公式日志
     *
     */
    @Schema(description = "计算过程快照/公式日志 (如: 15000 * (21.5 / 21.75))")
    private String calcLog;
    /**
     *排序号
     *
     */
    @Schema(description = "展示排序号 (决定工资单上的显示顺序)")
    private Integer sort;

    // =========================================================================
    // 🌟 引擎内部流转扩展字段 (核心修复点)
    // =========================================================================

    /**
     * 扩展字段：暂存字典配置的主键 ID。
     * 仅供后端 Persistence 处理器使用，映射落库实体，绝对禁止序列化成 JSON。
     */
    @JsonIgnore
    @Schema(hidden = true)
    private Long extConfigId;

    /**
     * 扩展字段：暂存动态推导后的收支类型 (1-收入, 2-扣款, 3-税费, 4-公司支出)。
     * 仅供后端 Persistence 处理器使用，映射落库实体，绝对禁止序列化成 JSON。
     */
    @JsonIgnore
    @Schema(hidden = true)
    private Integer extItemType;
}
