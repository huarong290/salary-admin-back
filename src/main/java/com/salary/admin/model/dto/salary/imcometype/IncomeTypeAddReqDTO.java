package com.salary.admin.model.dto.salary.imcometype;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 收入类型字典维护 DTO
 */
@Data
@Schema(description = "新增收入类型请求")
public class IncomeTypeAddReqDTO implements Serializable {
    /**
     * 类型编码 (唯一标识)
     */
    @NotBlank(message = "类型编码不能为空")
    @Schema(description = "类型编码 (如: BASE, OVERTIME)")
    private String typeCode;
    /**
     * 类型名称
     */
    @NotBlank(message = "类型名称不能为空")
    @Schema(description = "类型名称 (如: 基本工资, 加班费)")
    private String typeName;
    /**
     * 拼音缩写
     */
    @NotBlank(message = "拼音缩写不能为空")
    @Schema(description = "拼音缩写")
    private String pinyinCode;
    /**
     * 收入项分类
     */
    @Schema(description = "分类 (如: 固定工资, 补贴, 奖金)")
    private String categoryName;
    /**
     * 收入项说明
     */
    @Schema(description = "收入项说明")
    private String description;
    /**
     * 排序值
     */
    @NotNull(message = "排序值不能为空")
    @Schema(description = "排序值 (数值越小越靠前)")
    private Integer sortValue;
}
