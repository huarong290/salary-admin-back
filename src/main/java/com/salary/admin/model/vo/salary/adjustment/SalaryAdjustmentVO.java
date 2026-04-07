package com.salary.admin.model.vo.salary.adjustment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "专项调整(手工账)展示对象")
public class SalaryAdjustmentVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "员工姓名") //  增强字段：通过关联查询或字段冗余获得
    private String employeeName;

    @Schema(description = "核算周期ID")
    private Long periodId;

    @Schema(description = "结算月份") //  增强字段：通过关联查询或字段冗余获得
    private String settlementMonth;

    @Schema(description = "薪资项目编码")
    private String itemCode;

    @Schema(description = "薪资项目名称")
    private String itemName;

    @Schema(description = "调账类型: 1-补发, 2-扣减")
    private Integer adjustType;

    @Schema(description = "原币金额")
    private BigDecimal originalAmount;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "折算本币金额")
    private BigDecimal settlementAmount;

    @Schema(description = "状态: 0-草稿, 1-已生效")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}