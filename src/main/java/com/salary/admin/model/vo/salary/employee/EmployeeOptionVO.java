package com.salary.admin.model.vo.salary.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 员工下拉选项 VO - 轻量化对象
 */
@Data
public class EmployeeOptionVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    private Long id;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String employeeName;

    /**
     * 员工编号
     */
    @Schema(description = "员工编号")
    private String employeeCode;

    /**
     * 平台账号
     */
    @Schema(description = "平台账号")
    private String platformAccount;
}
