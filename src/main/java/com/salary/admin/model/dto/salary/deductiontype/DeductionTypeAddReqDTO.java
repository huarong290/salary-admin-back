package com.salary.admin.model.dto.salary.deductiontype;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
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
     * 是否固定扣款:0-否, 1-是
     */
    @Schema(description = "是否固定扣款:0-否, 1-是")
    private Integer FixedFlag;
    /**
     * 是否为税前合法扣除项(如五险一金): 0-否, 1-是
     */
    @Schema(description = "是否为税前合法扣除项(如五险一金): 0-否, 1-是")
    private Integer taxDeductibleFlag;
    /**
     * 排序值 (数值越小越靠前
     */
    @NotNull(message = "排序值不能为空")
    @Schema(description = "排序值 (数值越小越靠前)")
    private Integer sortValue;
}
