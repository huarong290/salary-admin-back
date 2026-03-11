package com.salary.admin.model.dto.salary.employee;

import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 员工分页查询 DTO
 */
@Data
@Schema(description = "分页查询员工列表请求")
public class EmployeeQueryReqDTO extends PageQueryDTO {
    @Schema(description = "关键词(姓名/编号)")
    private String keyword;

    @Schema(description = "部门名称")
    private String department;

    @Schema(description = "在职状态")
    private String employmentStatus;
}
