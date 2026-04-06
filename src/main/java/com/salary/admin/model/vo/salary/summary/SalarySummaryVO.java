package com.salary.admin.model.vo.salary.summary;

import com.salary.admin.model.dto.salary.snapshot.ArchiveSnapshot;
import com.salary.admin.model.dto.salary.snapshot.SalarySnapshotDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 薪资汇总展示对象 (用于前端页面展示)
 * 对应数据库 salary_summary 表，并集成了快照 DTO
 */
@Data
@Accessors(chain = true)
@Schema(name = "SalarySummaryVO", description = "薪资汇总详情视图对象")
public class SalarySummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "汇总ID")
    private Long id;

    // ==================== 1. 员工基本信息快照 ====================

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "员工编号 (快照)")
    private String employeeCode;

    @Schema(description = "员工姓名 (快照)")
    private String employeeName;

    // ==================== 2. 周期与时间信息 ====================

    @Schema(description = "结算月份 (YYYYMM)")
    private String settlementMonth;

    @Schema(description = "计薪周期开始日期")
    private LocalDate periodStartDate;

    @Schema(description = "计薪周期结束日期")
    private LocalDate periodEndDate;

    // ==================== 3. 核心金额字段 (直接取自数据库) ====================

    @Schema(description = "收入合计")
    private BigDecimal incomeTotal;

    @Schema(description = "扣款合计")
    private BigDecimal deductionTotal;

    @Schema(description = "税费合计")
    private BigDecimal taxTotal;

    @Schema(description = "应发工资 (税前)")
    private BigDecimal grossSalary;

    @Schema(description = "实发工资 (税后/最终)")
    private BigDecimal netSalary;

    // ==================== 4. 状态控制字段 ====================

    @Schema(description = "计算状态: 0-未计算, 1-成功, 2-失败")
    private Integer calcStatus;

    @Schema(description = "发放状态: 0-未支付, 1-已支付, 2-支付失败")
    private Integer paymentStatus;

    @Schema(description = "锁定标识: 1-已锁定(不可重算), 0-未锁定")
    private Integer lockFlag;

    @Schema(description = "计算版本号 (并发控制)")
    private Integer calcVersion;

    // ==================== 5. 核心：结构化快照数据 ====================

    /**
     * 这里直接引用你刚才定义的 DTO
     * 在 Service 层通过 Jackson 将数据库的 detail_json 转为此对象
     */
    @Schema(description = "薪资计算详细快照 (包含收入/扣款明细及计算日志)")
    private SalarySnapshotDTO details;

    // ==================== 5.5 核算溯源快照 ====================

    @Schema(description = "本次核算命中的档案快照列表 (解决分段计薪和前端溯源展示问题)")
    private List<ArchiveSnapshot> usedArchives;

    // ==================== 6. 审计字段 ====================

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "最后操作人")
    private String updateBy;
}
