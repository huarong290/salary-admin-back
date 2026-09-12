package com.salary.admin.model.entity.salary;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.salary.admin.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 月度汇率表 (多币种结算支撑)
 * <p>
 * 按 结算月份 × 币种 维护对基准币(默认 CNY)的汇率。
 * 引擎核算时按员工档案币种查询当月汇率，未配置回退 1:1。
 * </p>
 *
 * @author system
 * @since 2026-08-20
 */
@Schema(name = "SalaryExchangeRate", description = "月度汇率表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_exchange_rate")
public class SalaryExchangeRate extends BaseEntity<SalaryExchangeRate> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 结算月份 (YYYYMM)
     */
    @Schema(description = "结算月份 (YYYYMM)")
    @TableField("settlement_month")
    private String settlementMonth;

    /**
     * 币种
     */
    @Schema(description = "币种 (如 CNY/PHP/USD/AED)")
    @TableField("currency")
    private String currency;

    /**
     * 1 单位 currency = ? 基准币
     */
    @Schema(description = "1 单位 currency = ? 基准币")
    @TableField("exchange_rate")
    private BigDecimal exchangeRate;

    /**
     * 基准币种
     */
    @Schema(description = "基准币种")
    @TableField("base_currency")
    private String baseCurrency;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
