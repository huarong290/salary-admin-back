package com.salary.admin.model.vo.salary.employee;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工基本信息返回对象 VO
 * 用于薪资模块员工档案展示，裁剪逻辑删除等敏感/冗余字段
 *
 * @author system
 * @since 2026-03-11
 */
@Data
@Schema(description = "员工薪资档案视图对象")
public class EmployeeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    private Long id;

    /**
     * 员工编号
     */
    @Schema(description = "员工编号")
    private String employeeCode;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String employeeName;

    /**
     * 所属公司
     */
    @Schema(description = "所属公司")
    private String companyName;

    /**
     * 部门
     */
    @Schema(description = "部门")
    private String department;

    /**
     * 在职状态
     */
    @Schema(description = "在职状态")
    private String employmentStatus;

    /**
     * 是否转岗 (0:否, 1:是)
     */
    @Schema(description = "是否转岗 (0:否, 1:是)")
    private Integer isTransferred;

    /**
     * 住宿情况
     */
    @Schema(description = "住宿情况")
    private String accommodationStatus;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}