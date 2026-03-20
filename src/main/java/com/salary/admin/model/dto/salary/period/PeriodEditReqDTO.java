package com.salary.admin.model.dto.salary.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "修改薪资周期请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeriodEditReqDTO extends PeriodAddReqDTO {
    @NotNull(message = "周期ID不能为空")
    @Schema(description = "周期ID")
    private Long id;
}
