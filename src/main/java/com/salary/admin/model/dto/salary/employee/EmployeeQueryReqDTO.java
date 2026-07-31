package com.salary.admin.model.dto.salary.employee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 员工分页查询 DTO
 */
@Data
@Schema(description = "分页查询员工列表请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeQueryReqDTO extends PageQueryDTO {
    @Schema(description = "关键词(姓名/编号)")
    private String keyword;

    @Schema(description = "部门名称")
    private String department;

    @Schema(description = "在职状态")
    private Integer employmentStatus;
    /**
     * 平台账号
     */
    @Schema(description = "平台账号")
    private String platformAccount;
}
