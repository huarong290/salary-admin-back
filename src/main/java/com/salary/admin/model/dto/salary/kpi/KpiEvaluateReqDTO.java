package com.salary.admin.model.dto.salary.kpi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 员工月度绩效打分与评级提交对象 DTO
 * 供部门主管或HR用于录入员工当月绩效考核结果，提交后系统将自动换算系数
 *
 * @author system
 * @since 2026-04-04
 */
@Data
@Schema(description = "员工月度绩效打分提交对象")
// 🌟 架构师标配：忽略前端传来的多余/未知字段，防止直接抛出 Json parse error 导致系统崩溃
@JsonIgnoreProperties(ignoreUnknown = true)
public class KpiEvaluateReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 绩效记录ID
     */
    @NotNull(message = "绩效单ID不能为空")
    @Schema(description = "绩效记录主键ID (必填，用于精准定位待打分单据)")
    private Long id;

    /**
     * 最终绩效评级
     */
    @NotBlank(message = "绩效评级不能为空")
    @Schema(description = "考核评级 (必填，如: S, A, B, C, D)")
    private String kpiGrade;

    /**
     * 最终考核打分
     */
    @Schema(description = "具体考核分数 (选填，如: 95.50，部分公司精细化核算时使用)")
    private BigDecimal kpiScore;

    /**
     * 考核评语
     */
    @Schema(description = "考核评语/说明 (选填，建议C级及以下绩效必须填写评语以备审计)")
    private String evaluateRemark;
}