package com.salary.admin.model.vo.salary.summary;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 薪资结算汇总返回对象 VO
 * 用于向财务和员工展示最终的工资单结果
 *
 * @author system
 * @since 2026-03-11
 */
@Data
@Schema(description = "薪资结算汇总视图对象")
public class SummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "汇总ID")
    private Long id;

    @Schema(description = "薪资周期ID")
    private Long periodId;

    /* ================== 🌟 扩展显示字段 (内存拼接) ================== */

    @Schema(description = "员工姓名")
    private String employeeName;

    @Schema(description = "结算月份")
    private String settlementMonth;

    /* ================== 💰 核心财务与多币种数据 ================== */

    @Schema(description = "结算币种(CNY/PHP/USDT)")
    private String currency;

    @Schema(description = "汇率(1本币兑X目标币快照)")
    private BigDecimal exchangeRate;

    @Schema(description = "应发小计(本币)")
    private BigDecimal salarySubtotal;

    @Schema(description = "扣款小计(本币)")
    private BigDecimal salaryDeductionTotal;

    @Schema(description = "最终结算薪资(本币)")
    private BigDecimal salaryTotal;

    @Schema(description = "实发金额(目标币)")
    private BigDecimal salaryConverted;

    @Schema(description = "折合人民币(存档)")
    private BigDecimal salaryRmb;

    @Schema(description = "折合USDT(存档)")
    private BigDecimal salaryUsdt;

    @Schema(description = "发放账号/钱包地址(快照)")
    private String targetAccount;

    @Schema(description = "支付状态(0未支付 1已支付 2失败 3锁定)")
    private Boolean paymentStatus;

    @Schema(description = "实际发放/确认时间")
    private LocalDateTime payTime;

    @Schema(description = "备注")
    private String remark;
}
