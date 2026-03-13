package com.salary.admin.model.dto.salary.archive;

import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArchiveQueryReqDTO extends PageQueryDTO {

    @Schema(description = "员工姓名/编号关键字")
    private String keyword;

    @Schema(description = "是否仅看当前最新版本")
    private Integer isLatest = 1;
}
