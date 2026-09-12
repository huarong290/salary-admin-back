package com.salary.admin.model.vo.salary.paymentrecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 薪资支付/流水记录 视图对象
 * <p>
 * 说明：该 VO 承载"工资单流水底稿"视图，由 salary_summary + salary_employee 聚合生成，
 * 供发薪台穿透查看每位员工的核算流水与手工调整记录。
 * </p>
 *
 * @author system
 * @since 2026-03-15
 */
@Data
@Schema(description = "薪资流水记录视图对象")
public class SalaryPaymentRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "明细记录唯一ID (salary_summary.id)")
    private Long id;

    @Schema(description = "关联的汇总批次ID")
    private Long summaryId;

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "关联的薪资档案ID (定薪依据快照版本)")
    private Long archiveId;

    @Schema(description = "员工姓名")
    private String employeeName;

    @Schema(description = "员工工号")
    private String employeeNo;

    @Schema(description = "结算月份 (如: 202603)")
    private String settlementMonth;

    @Schema(description = "结算币种")
    private String settlementCurrency;

    @Schema(description = "核算底薪")
    private BigDecimal baseSalary;

    @Schema(description = "收入项合计")
    private BigDecimal incomeTotal;

    @Schema(description = "扣款项合计")
    private BigDecimal deductionTotal;

    @Schema(description = "税费合计")
    private BigDecimal taxTotal;

    @Schema(description = "最终实发工资 (netSalary + manualPaymentAmount)")
    private BigDecimal finalSalary;

    @Schema(description = "是否手动调整: 0-系统计算, 1-手动录入")
    private Integer isManual;

    @Schema(description = "核算明细快照 JSON")
    private String detailJson;

    @Schema(description = "解析后的明细项快照列表")
    private List<ArchiveItemDetailVO> snapshotItems;

    @Schema(description = "备注信息")
    private String remark;

    @Schema(description = "核算生成时间")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;

    /**
     * 薪资项明细快照数据对象 (detailJson 的解析结构)
     */
    @Data
    @Schema(description = "薪资项明细快照数据对象")
    public static class ArchiveItemDetailVO {
        @Schema(description = "薪资项名称")
        private String itemName;
        @Schema(description = "薪资项类型: 1-收入, 2-扣款, 3-税费, 4-公司成本")
        private Integer itemType;
        @Schema(description = "核算时的具体金额")
        private BigDecimal amount;
        @Schema(description = "计算公式追踪 (审计依据)")
        private String formula;
    }
}
