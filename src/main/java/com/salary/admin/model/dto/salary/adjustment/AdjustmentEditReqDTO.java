package com.salary.admin.model.dto.salary.adjustment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 薪资专项调整-修改请求参数
 * 继承 AddReqDTO，复用所有业务字段的校验规则
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "薪资专项调整-修改请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdjustmentEditReqDTO extends AdjustmentAddReqDTO {

    @NotNull(message = "修改时主键ID不可为空")
    @Schema(description = "调整记录的主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    // 💡 架构师提示：
    // 因为继承了 AdjustmentAddReqDTO，所以 originalAmount、exchangeRate 等字段及其 @NotNull 校验规则都会自动生效。
    // 这大大减少了冗余代码，以后加新字段只需要在 AddReqDTO 里加一次即可。
}