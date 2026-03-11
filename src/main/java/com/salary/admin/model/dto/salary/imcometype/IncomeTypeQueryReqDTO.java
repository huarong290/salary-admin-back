package com.salary.admin.model.dto.salary.imcometype;

import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "分页查询收入类型请求")
public class IncomeTypeQueryReqDTO extends PageQueryDTO {
    @Schema(description = "关键词 (编码或名称)")
    private String keyword;
}