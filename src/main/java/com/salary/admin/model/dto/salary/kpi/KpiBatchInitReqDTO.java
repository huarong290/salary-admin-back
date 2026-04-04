package com.salary.admin.model.dto.salary.kpi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

/**
 * 员工月度绩效批量初始化请求对象 DTO
 * 用于发薪前，一键为当月拥有考勤周期的员工生成空白绩效打分单
 *
 * @author system
 * @since 2026-04-04
 */
@Data
@Schema(description = "员工月度绩效批量初始化请求对象")
// 🌟 架构师标配：忽略前端传来的多余/未知字段，防止直接抛出 Json parse error 导致系统崩溃
@JsonIgnoreProperties(ignoreUnknown = true)
public class KpiBatchInitReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结算月份
     */
    @NotBlank(message = "结算月份不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "结算月份格式必须为6位数字(例如: 202604)")
    @Schema(description = "结算月份 (必填，格式: YYYYMM，系统将为其生成当月考核单)")
    private String settlementMonth;
}