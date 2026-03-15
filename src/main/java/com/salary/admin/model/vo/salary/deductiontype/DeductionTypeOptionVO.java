package com.salary.admin.model.vo.salary.deductiontype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "DeductionTypeOptionVO", description = "扣款类型下拉选项VO")
public class DeductionTypeOptionVO {
    @Schema(description = "扣款类型ID")
    private Long id;

    @Schema(description = "扣款类型名称")
    private String typeName;

    @Schema(description = "扣款类型编码")
    private String typeCode;
}