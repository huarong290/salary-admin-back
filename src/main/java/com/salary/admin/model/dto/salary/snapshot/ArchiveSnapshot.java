package com.salary.admin.model.dto.salary.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "轻量级档案快照信息 (落库用)")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public  class ArchiveSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 档案主键ID
     */
    @Schema(description = "档案主键ID")
    private Long archiveId;
    /**
     * 档案版本号
     */
    @Schema(description = "档案版本号")
    private Integer version;
    /**
     * 在该计算周期内的有效起始日
     */
    @Schema(description = "在该计算周期内的有效起始日")
    private LocalDate effectiveDate;
    /**
     * 在该计算周期内的有效截止日
     */
    @Schema(description = "在该计算周期内的有效截止日")
    private LocalDate expiryDate;
}
