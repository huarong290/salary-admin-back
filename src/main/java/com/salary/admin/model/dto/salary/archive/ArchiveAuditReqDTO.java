package com.salary.admin.model.dto.salary.archive;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 薪资档案审批请求参数
 */
@Data
@Schema(description = "薪资档案审批请求参数")
public class ArchiveAuditReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 调薪草稿档案的ID
     */
    @Schema(description = "档案ID")
    @NotNull(message = "档案ID不能为空")
    private Long id;
    /**
     * 审核状态：1-通过，2-驳回
     */
    @Schema(description = "审核状态：1-通过，2-驳回")
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;
    /**
     * 审核备注/驳回原因
     */
    @Schema(description = "审核备注/驳回原因")
    private String remark;
}
