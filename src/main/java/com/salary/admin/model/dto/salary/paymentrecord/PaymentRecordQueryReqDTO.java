package com.salary.admin.model.dto.salary.paymentrecord;

import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;



/**
 * 薪资结算明细记录查询请求对象 DTO
 * 用于分页查询和筛选特定汇总批次下的员工薪资记录
 *
 * @author system
 * @since 2026-03-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "薪资结算明细记录查询请求对象")
public class PaymentRecordQueryReqDTO extends PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 汇总ID
     */
    @Schema(description = "关联的薪资汇总ID (必填，用于锁定特定核算批次)")
    private Long summaryId;

    /**
     * 关键词
     */
    @Schema(description = "搜索关键词 (支持员工姓名、员工编号模糊匹配)")
    private String keyword;

    /**
     * 是否手动录入
     */
    @Schema(description = "是否手动录入总额: 0-系统计算, 1-手动录入")
    private Integer isManual;
}