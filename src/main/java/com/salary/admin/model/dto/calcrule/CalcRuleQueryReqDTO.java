package com.salary.admin.model.dto.calcrule;

import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "薪资计算规则查询请求参数")
public class CalcRuleQueryReqDTO extends PageQueryDTO {

    @Schema(description = "规则编码/名称关键字")
    private String keyword;

    @Schema(description = "规则类型 (1:公式, 2:函数)")
    private Integer ruleType;

    @Schema(description = "状态 (1:启用, 0:停用)")
    private Integer status;

    @Schema(description = "所属阶段 (1基础 2补贴 3扣款 4税 5汇总)")
    private Integer stage;
}
