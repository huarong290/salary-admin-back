package com.salary.admin.model.dto.salary.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 薪资结算单全局快照 DTO
 * 用于序列化为 JSON 存储在 salary_summary 的 detail_json 字段中
 */
@Data
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "薪资结算单快照详情 (用于工资条展示)")
public class SalarySnapshotDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 1. 基础元数据快照 ====================
    /**
     * 当月计薪总天数
     * 用于计算出勤和薪资的基准（如法定 21.75 天，或自然月天数）。
     */
    private BigDecimal monthDays;

    /**
     * 实际出勤天数
     * 员工在该月份实际出勤的天数，支持带小数的半天（如 21.5）。
     */
    private BigDecimal attendanceDays;

    /**
     * 档案标准基础薪资
     * 员工的固定月薪，不考虑出勤情况时的标准工资，作为财务对账参照。
     */
    private BigDecimal baseSalary;
    /**
     * 结算币种 (固化)
     *
     */
    @Schema(description = "结算币种 (固化)")
    private String settlementCurrency;
    /**
     * 核算汇率 (固化)
     *
     */
    @Schema(description = "核算汇率 (固化)")
    private BigDecimal exchangeRate;
    // ==================== 2. 统计汇总快照 (直接用于前端显示) ====================
    /**
     * 统计快照：应发工资/收入合计
     */
    @Schema(description = "统计快照：应发工资 (Gross)")
    private BigDecimal grossSalary;

    /**
     * 统计快照：扣款合计
     */
    @Schema(description = "统计快照：扣款合计 (Deduction)")
    private BigDecimal deductionTotal;

    /**
     * 统计快照：税费合计
     */
    @Schema(description = "统计快照：税费合计 (Tax)")
    private BigDecimal taxTotal;

    /**
     * 统计快照：实发工资
     */
    @Schema(description = "统计快照：实发工资 (Net)")
    private BigDecimal netSalary;


    // ==================== 3.薪资构成明细 (按财务标准分组) ====================
    /**
     * 收入项明细集合
     */
    @Schema(description = "收入项明细集合 (item_category = 1)")
    private List<SalaryDetailItemDTO> income = new ArrayList<>();
    /**
     * 扣款项明细集合
     */
    @Schema(description = "扣款项明细集合 (item_category = 2)")
    private List<SalaryDetailItemDTO> deduction = new ArrayList<>();
    /**
     * 税费明细集合
     */
    @Schema(description = "税费明细集合 (item_category = 3)")
    private List<SalaryDetailItemDTO> tax = new ArrayList<>();
    /**
     * 公司支出/补贴成本明细集合
     */
    @Schema(description = "公司支出/补贴成本明细集合 (item_category = 4, 仅HR/财务可见)")
    private List<SalaryDetailItemDTO> companyExpense = new ArrayList<>();

    // ==================== 4. 辅助备注 ====================
    /**
     * 计算时的自动备注
     */
    @Schema(description = "计算时的自动备注")
    private String calcRemark;

    // ==================== 5. 🌟 核算溯源快照 (底层存储) ====================
    /**
     * 本次核算命中的档案快照列表
     */
    @Schema(description = "本次核算命中的档案快照列表 (解决分段计薪和底层持久化溯源)")
    private List<ArchiveSnapshot> usedArchives;
}
