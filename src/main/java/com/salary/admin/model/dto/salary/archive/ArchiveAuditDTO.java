package com.salary.admin.model.dto.salary.archive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "薪资档案审核请求对象")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchiveAuditDTO {

    @Schema(description = "档案ID")
    @NotNull(message = "档案ID不能为空")
    private Long id;

    @Schema(description = "审核状态：1-通过，2-驳回")
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    /**
     * 计税方案: 0-不计税, 1-居民个人所得税, 2-劳务报酬税
     */
    @Schema(description = "计税方案: 0-不计税, 1-居民个人所得税, 2-劳务报酬税")
    private Integer taxScheme;

    @Schema(description = "审核备注/驳回原因")
    private String remark;
}
