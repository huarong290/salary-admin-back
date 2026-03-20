package com.salary.admin.model.dto.salary.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 指定周期触发薪资计算请求 DTO
 * 专用于单人重算、局部批量重算场景
 */
@Data
@Schema(description = "指定周期触发薪资计算请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryCalcByPeriodReqDTO implements Serializable {

    @NotEmpty(message = "请至少指定一个需要核算的薪资周期")
    @Schema(description = "需要核算的周期ID集合", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> periodIds;

    @Schema(description = "手工备注 (如: 单人异常数据修正重算)")
    private String remark;
}