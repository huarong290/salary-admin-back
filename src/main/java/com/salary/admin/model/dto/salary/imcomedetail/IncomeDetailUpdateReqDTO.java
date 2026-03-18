package com.salary.admin.model.dto.salary.imcomedetail;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改收入明细 DTO
 * 继承 AddReqDTO，复用业务字段，仅增加必填的 ID 字段
 */
@Data
@EqualsAndHashCode(callSuper = true) // 🌟 必须加：让 Lombok 的 equals 和 hashCode 包含父类的字段
@Schema(description = "修改收入明细请求")
public class IncomeDetailUpdateReqDTO extends IncomeDetailAddReqDTO {

    @NotNull(message = "修改时明细ID不能为空")
    @Schema(description = "明细主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

}