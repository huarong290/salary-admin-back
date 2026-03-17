package com.salary.admin.model.dto.salary.deductiontype;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增扣款类型 DTO
 */
@Data
@Schema(description = "新增扣款类型请求")
public class DeductionTypeAddReqDTO implements Serializable {
    /**
     * 类型编码 (唯一标识)
     */
    @NotBlank(message = "类型编码不能为空")
    @Schema(description = "类型编码 (唯一标识)")
    private String typeCode;
    /**
     * 类型名称
     */
    @NotBlank(message = "类型名称不能为空")
    @Schema(description = "类型名称")
    private String typeName;
    /**
     * 拼音缩写
     */
    @NotBlank(message = "拼音缩写不能为空")
    @Schema(description = "拼音缩写")
    private String pinyinCode;
    /**
     * 扣款分类
     */
    @Schema(description = "扣款分类")
    private String categoryName;
    /**
     * 扣款项说明
     */
    @Schema(description = "扣款项说明")
    private String description;
    /**
     * 是否固定扣款
     */
    @Schema(description = "是否固定扣款")
    private Integer isFixed;
    /**
     * 排序值 (数值越小越靠前
     */
    @NotNull(message = "排序值不能为空")
    @Schema(description = "排序值 (数值越小越靠前)")
    private Integer sortValue;
}
