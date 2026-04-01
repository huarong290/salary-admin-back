package com.salary.admin.model.dto.calcpipelineinfo;

import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 薪资计算管道主表查询请求参数
 *
 * 用于分页查询管道元信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "薪资计算管道主表查询请求参数")
public class CalcPipelineInfoQueryReqDTO extends PageQueryDTO {

    /** 管道编码/名称关键字 */
    @Schema(description = "管道编码/名称关键字")
    private String keyword;

    /** 状态 (1启用 0停用) */
    @Schema(description = "状态 (1启用 0停用)")
    private Integer status;
}
