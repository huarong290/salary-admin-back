package com.salary.admin.model.dto.salary.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改汇总结算信息 DTO (通常用于支付后手动更新状态)
 */
@Data
@Schema(description = "修改薪资汇总记录请求")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryEditReqDTO implements Serializable {
    @NotNull(message = "汇总记录ID不能为空")
    @Schema(description = "汇总ID")
    private Long id;

    @Schema(description = "发放账号/钱包地址")
    private String targetAccount;

    @Schema(description = "支付状态(0未支付 1已支付 2失败 3锁定)")
    private Integer paymentStatus;

    @Schema(description = "备注")
    private String remark;
}