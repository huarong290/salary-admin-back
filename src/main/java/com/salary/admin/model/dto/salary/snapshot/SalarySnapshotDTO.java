package com.salary.admin.model.dto.salary.snapshot;

import com.salary.admin.model.vo.salary.archiveitem.ArchiveItemDetailVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SalarySnapshotDTO {
    /**
     * 月份的总天数
     * 例如：某个月有 30 天或 31 天，用于计算出勤和薪资的基准。
     */
    private Integer monthDays;

    /**
     * 实际出勤天数
     * 员工在该月份实际出勤的天数，用于与 monthDays 对比，计算薪资的按比例发放。
     */
    private Integer attendanceDays;

    /**
     * 基础薪资
     * 员工的固定月薪，不考虑出勤情况时的标准工资。
     */
    private BigDecimal baseSalary;

    /**
     * 按出勤天数比例计算后的基础薪资
     * 如果员工未满勤，则根据 attendanceDays / monthDays 的比例计算应发的基础工资。
     * 例如：月薪 30000 元，出勤 15 天，月总天数 30 天，则应发工资 = 30000 * (15/30) = 15000。
     */
    private BigDecimal proratedBaseSalary;

    /**
     * 薪资构成明细快照
     * 包含该月所有薪资项目的详细信息，例如：
     * - 奖金
     * - 津贴
     * - 扣款
     * - 加班费
     * 每个项目由 ArchiveItemDetailVO 表示，存放在列表中。
     */
    private List<ArchiveItemDetailVO> items = new ArrayList<>();
}

