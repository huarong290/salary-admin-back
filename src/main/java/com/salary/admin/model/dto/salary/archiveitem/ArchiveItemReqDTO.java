package com.salary.admin.model.dto.salary.archiveitem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 薪资档案明细保存传输对象
 */
@Data
@Schema(description = "薪资档案明细项参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchiveItemReqDTO {

    @Schema(description = "项目配置ID (对应 salary_item_config.id)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "项目配置ID不能为空")
    private Long itemConfigId;

    @Schema(description = "项目类型 (1:收入, 2:扣款)", hidden = true)
    private Integer itemType;

    /**
     * 计算模式 (1:按月固定, 2:按出勤天数, 3:按现场出勤, 4:按居家出勤)
     */
    @Schema(description = "计算模式 (1:按月固定, 2:按出勤天数, 3:按现场出勤, 4:按居家出勤)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "计算模式不能为空")
    private Integer calcMode;

    /**
     * 💡 基准标准金额 (按月固定传月总额，按天计算传日单价)
     */
    @Schema(description = "基准标准金额 (按月固定传月总额，按天计算传日单价)", example = "500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.00", message = "金额不能为负数")
    private BigDecimal amount;

    @Schema(description = "个性化表达式脚本 (如为空，则走配置表的默认脚本)")
    private String ruleScript;
}
