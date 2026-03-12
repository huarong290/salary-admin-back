package com.salary.admin.model.vo.salary.employee;

import lombok.Data;

import java.io.Serializable;

/**
 * 员工下拉选项 VO - 轻量化对象
 */
@Data
public class EmployeeOptionVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 员工ID */
    private Long id;

    /** 员工姓名 */
    private String employeeName;

    /** 员工工号/编号 */
    private String employeeCode;
}
