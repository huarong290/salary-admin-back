package com.salary.admin.model.dto.calclog;


import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 薪资计算日志查询请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "薪资计算日志查询请求参数")
public class CalcLogQueryReqDTO extends PageQueryDTO {

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "薪资周期ID")
    private Long periodId;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "执行阶段")
    private Integer stage;

    @Schema(description = "是否成功 (true:无错误, false:有错误)")
    private Boolean success;
}
