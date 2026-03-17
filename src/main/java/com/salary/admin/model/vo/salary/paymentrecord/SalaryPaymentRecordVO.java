package com.salary.admin.model.vo.salary.paymentrecord;


import com.salary.admin.model.dto.salary.snapshot.SalarySnapshotDTO;
import com.salary.admin.model.entity.salary.SalaryPaymentRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 薪资结算明细记录返回对象 VO
 * 包含结算详情及关联的员工基础信息，用于前端列表展示
 *
 * @author system
 * @since 2026-03-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "薪资结算明细记录视图对象")
public class SalaryPaymentRecordVO extends SalaryPaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工姓名
     */
    @Schema(description = "员工姓名")
    private String employeeName;

    /**
     * 员工编号
     */
    @Schema(description = "员工编号")
    private String employeeCode;

    /**
     * 结算月份
     */
    @Schema(description = "结算月份")
    private String settlementMonth;

    // 🌟 核心修正：使用快照 DTO 来接收解析后的 JSON
    // 我们将字段名设为 parsedSnapshot，以区分原生的字符串 detailJson
    @Schema(description = "解析后的快照详情对象 (包含出勤天数、底薪及明细列表)")
    private SalarySnapshotDTO parsedSnapshot;
}