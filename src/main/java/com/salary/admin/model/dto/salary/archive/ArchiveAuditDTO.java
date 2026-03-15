package com.salary.admin.model.dto.salary.archive;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "薪资档案审核请求对象")
public class ArchiveAuditDTO {

    @Schema(description = "档案ID")
    @NotNull(message = "档案ID不能为空")
    private Long id;

    @Schema(description = "审核状态：1-通过，2-驳回")
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "审核备注/驳回原因")
    private String remark;
}
