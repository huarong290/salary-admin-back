package com.salary.admin.model.dto.salary.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 薪资结算单全局快照 DTO
 * 用于序列化为 JSON 存储在 salary_payment_record 的 detail_json 字段中
 */
@Data
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalarySnapshotDTO {

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
     * 薪资构成明细快照 (核心)
     * 包含该月所有薪资项目的详细信息（底薪折算、固定津贴、临时奖惩、扣款等）。
     * 内部包含分类(category)、溯源(source)和计算公式(formula)。
     */
    private List<SalaryDetailItemDTO> items = new ArrayList<>();
}
