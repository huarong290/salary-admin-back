package com.salary.admin.model.dto.salary.archiveitem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 薪资档案明细保存传输对象
 */
@Data
@Schema(description = "薪资档案明细保存入参")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchiveItemAddDTO {

    @NotNull(message = "项目类型不能为空")
    @Schema(description = "项目类型: 1-收入项, 2-扣款项", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer itemType;

    @NotNull(message = "字典类型ID不能为空")
    @Schema(description = "对应的收入/扣款类型字典ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long typeId;

    @NotNull(message = "计算方式不能为空")
    @Schema(description = "计算方式: 1-固定金额, 2-按基数比例", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer calcType;

    @Schema(description = "计算基数 (为空则默认取主表base_salary)")
    private BigDecimal baseAmount;

    @Schema(description = "固定金额 (calcType=1时必填)")
    private BigDecimal amount;

    @Schema(description = "计算比例 (calcType=2时必填，如 0.0800)")
    private BigDecimal ratio;
}
