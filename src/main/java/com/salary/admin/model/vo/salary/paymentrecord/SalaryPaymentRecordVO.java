package com.salary.admin.model.vo.salary.paymentrecord;


import com.salary.admin.model.entity.salary.SalaryPaymentRecord;
import com.salary.admin.model.vo.salary.archiveitem.ArchiveItemDetailVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

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
     * 解析后的明细列表
     */
    @Schema(description = "解析后的计算详情明细 (由 detail_json 反序列化而来)")
    private List<ArchiveItemDetailVO> itemDetails;
}