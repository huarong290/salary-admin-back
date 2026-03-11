package com.salary.admin.model.vo.deductiondetail;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 扣款明细返回对象 VO
 * 用于展示员工在特定周期内的各项扣款流水（如社保、个税等）
 *
 * @author system
 * @since 2026-03-11
 */
@Data
@Schema(description = "扣款明细视图对象")
public class DeductionDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    @Schema(description = "明细ID")
    private Long id;

    /**
     * 周期ID
     */
    @Schema(description = "周期ID (关联 salary_period)")
    private Long periodId;

    /**
     * 扣款类型ID
     */
    @Schema(description = "扣款类型ID (关联 salary_deduction_type)")
    private Long deductionTypeId;

    /**
     * 🌟 扩展字段：扣款项目名称
     */
    @Schema(description = "扣款项目名称 (如: 个人所得税)")
    private String deductionTypeName;

    /**
     * 🌟 扩展字段：员工姓名
     */
    @Schema(description = "员工姓名")
    private String employeeName;

    /**
     * 金额
     */
    @Schema(description = "金额")
    private BigDecimal amount;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;
}
