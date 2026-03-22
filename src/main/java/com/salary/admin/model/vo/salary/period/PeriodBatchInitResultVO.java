package com.salary.admin.model.vo.salary.period;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 薪资周期批量初始化结果 VO
 *
 * 用途：
 * - HR 在周期管理页面点击“初始化本月账套”后，返回本次操作的统计结果
 * - 不直接返回所有周期数据，避免大数据量传输
 * - 前端可根据结果提示用户，并调用分页查询接口刷新列表
 */
@Data
@Builder
@Schema(name = "PeriodBatchInitResultVO", description = "薪资周期批量初始化结果")
public class PeriodBatchInitResultVO {

    /**
     * 目标总人数（本次尝试初始化的员工数）
     */
    @Schema(description = "目标总人数")
    private Integer totalCount;

    /**
     * 成功初始化人数（实际新增周期数）
     */
    @Schema(description = "成功初始化人数")
    private Integer successCount;

    /**
     * 自动跳过人数（已存在周期的员工，幂等去重）
     */
    @Schema(description = "自动跳过人数")
    private Integer skipCount;

    /**
     * 结算月份（格式：yyyyMM）
     */
    @Schema(description = "结算月份")
    private String settlementMonth;

    /**
     * 🌟 企业级技巧：作为内部传输载荷，不对外暴露
     */
    @JsonIgnore
    private List<SalaryPeriod> newPeriodEntities;
}
