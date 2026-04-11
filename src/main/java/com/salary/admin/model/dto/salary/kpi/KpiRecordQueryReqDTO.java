package com.salary.admin.model.dto.salary.kpi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 员工月度绩效考核查询请求对象 DTO
 * 用于前端大盘分页查询、筛选特定月份或状态的绩效名单
 *
 * @author system
 * @since 2026-04-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "员工月度绩效考核查询请求对象")
// 🌟 架构师标配：忽略前端传来的多余/未知字段，防止直接抛出 Json parse error 导致系统崩溃
@JsonIgnoreProperties(ignoreUnknown = true)
public class KpiRecordQueryReqDTO extends PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关键词
     */
    @Schema(description = "搜索关键词 (支持员工姓名、员工编号模糊匹配)")
    private String keyword;
    /**
     * 结算年份
     */
    @Schema(description = "结算年份筛选 (例如: 2026，与 settlementMonth 互斥)")
    private String year;
    /**
     * 结算月份
     */
    @Schema(description = "结算月份筛选 (例如: 202604，用于查看特定月份的绩效大盘)")
    private String settlementMonth;

    /**
     * 审核状态
     */
    @Schema(description = "审核流转状态筛选: 0-打分中/草稿, 1-已确认(可算薪), 2-申诉中")
    private Integer auditStatus;

    /**
     * 指定部门筛选
     */
    @Schema(description = "部门ID (选填，供部门主管只看自己麾下员工绩效时过滤使用)")
    private Long departmentId;
}
