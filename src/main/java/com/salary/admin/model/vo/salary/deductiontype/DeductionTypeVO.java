package com.salary.admin.model.vo.salary.deductiontype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 扣款类型返回对象 VO
 * 用于定义薪资扣减项（如：社保、税金、缺勤扣款）的元数据
 *
 * @author system
 * @since 2026-03-11
 */
@Data
@Schema(description = "扣款类型视图对象")
public class DeductionTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 类型编码
     */
    @Schema(description = "类型编码 (如: TAX, INSURANCE)")
    private String typeCode;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称 (如: 个人所得税, 社保扣款)")
    private String typeName;
    /**
     * 拼音缩写
     */
    @Schema(description = "拼音缩写")
    private String pinyinCode;
    /**
     * 分类ID
     */
    @Schema(description = "分类ID (用于前端编辑回显)")
    private Long categoryId;
    /**
     * 扣款分类
     */
    @Schema(description = "扣款分类")
    private String categoryName;
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
     * 是否计入社保基数扣减: 0-否, 1-是
     */
    @Schema(description = "是否计入社保基数扣减: 0-否, 1-是")
    private Integer socialBaseFlag;
    /**
     * 是否影响奖金发放: 0-否, 1-是
     */
    @Schema(description = "是否影响奖金发放: 0-否, 1-是")
    private Integer bonusFlag;
    /**
     * 否与考勤强相关 (如迟到早退扣款): 0-否, 1-是
     */
    @Schema(description = "是否与考勤强相关 (如迟到早退扣款): 0-否, 1-是")
    private Integer attendanceRelatedFlag;
    /**
     * 扣款项说明
     */
    @Schema(description = "扣款项说明")
    private String description;

    /**
     * 排序值
     */
    @Schema(description = "排序值 (数值越小越靠前)")
    private Integer sortValue;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;
}
