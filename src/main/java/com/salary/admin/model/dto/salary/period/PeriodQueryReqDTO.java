package com.salary.admin.model.dto.salary.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页查询薪资周期列表请求 DTO
 * * @author system
 * @since 2026-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "分页查询薪资周期列表请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeriodQueryReqDTO extends PageQueryDTO {
    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    private Long employeeId;

    /**
     * 员工姓名/编号关键词
     */
    @Schema(description = "员工姓名或编号关键词 (模糊查询)")
    private String keyword;

    /**
     * 结算月份
     */
    @Schema(description = "结算月份 (格式：YYYYMM)")
    private String settlementMonth;

    /**
     * 累计在岗月份计数 (入职首月为1, 递增)
     */
    @Schema(description = "累计在岗月份计数 (入职首月为1, 递增)")
    private Integer workMonth;

    /**
     * 部门
     */
    @Schema(description = "所属部门")
    private String department;
}
