package com.salary.admin.model.vo.salary.incometype;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "IncomeTypeOptionVO", description = "收入类型下拉选项VO")
public class IncomeTypeOptionVO {

    @Schema(description = "收入类型ID")
    private Long id; // 对应实体类的 id

    @Schema(description = "收入类型名称")
    private String typeName; // 对应实体类的 typeName

    @Schema(description = "收入类型编码")
    private String typeCode; // 对应实体类的 typeCode

    @Schema(description = "排序值")
    private Integer sortValue; // 对应实体类的 sortValue，方便前端排序
}