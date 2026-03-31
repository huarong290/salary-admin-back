package com.salary.admin.model.vo.calclog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
/**
 * 薪资计算日志视图对象
 */
@Data
@Schema(description = "薪资计算日志视图对象")
public class CalcLogVO {

    @Schema(description = "日志主键ID")
    private Long id;

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "薪资周期ID")
    private Long periodId;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "执行阶段")
    private Integer stage;

    @Schema(description = "输入参数JSON字符串")
    private String inputJson;

    @Schema(description = "输出结果")
    private BigDecimal outputValue;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "耗时 (ms)")
    private Long executeTime;
}
