package com.salary.admin.model.dto.salary.archive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchiveQueryReqDTO extends PageQueryDTO {

    @Schema(description = "员工姓名/编号关键字")
    private String keyword;

    @Schema(description = "是否仅看当前最新版本")
    private Integer isLatest = 1;
}
