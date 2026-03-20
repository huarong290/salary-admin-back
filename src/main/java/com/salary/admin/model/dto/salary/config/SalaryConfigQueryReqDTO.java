package com.salary.admin.model.dto.salary.config;

import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "薪资配置分页查询请求")
public class SalaryConfigQueryReqDTO extends PageQueryDTO {

    @Schema(description = "配置名称 (支持模糊查询)", example = "币种")
    private String configName;

    @Schema(description = "配置键 (支持模糊查询)", example = "CURRENCY")
    private String configKey;

    @Schema(description = "激活状态 (1:启用 0:停用)")
    private Integer activeFlag;
}
