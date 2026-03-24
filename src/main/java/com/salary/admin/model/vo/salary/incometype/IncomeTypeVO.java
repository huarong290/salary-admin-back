package com.salary.admin.model.vo.salary.incometype;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收入类型返回对象 VO
 * 用于薪资项配置展示，定义薪资构成的基础元数据
 *
 * @author system
 * @since 2026-03-11
 */
@Data
@Schema(description = "收入类型视图对象")
public class IncomeTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 类型编码
     */
    @Schema(description = "类型编码 (如: BASE, OT)")
    private String typeCode;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称 (如: 基本工资, 加班费)")
    private String typeName;
    /**
     * 拼音缩写
     */
    @Schema(description = "拼音缩写")
    private String pinyinCode;
    /**
     * 关联分类ID
     */
    @Schema(description = "关联分类ID (用于前端编辑回显)")
    private Long categoryId;
    /**
     * 分类
     */
    @Schema(description = "分类 (如: 固定工资, 补贴, 奖金)")
    private String categoryName;

    /**
     * 是否纳入个税计税基数: 0-否, 1-是
     */
    @Schema(description = "'是否纳入个税计税基数: 0-否, 1-是")
    @TableField("taxable_flag")
    private Integer taxableFlag;
    /**
     * 是否计入社保基数: 0-否, 1-是
     */
    @Schema(description = "是否计入社保基数: 0-否, 1-是")
    private Integer socialBaseFlag;
    /**
     * 是否属于奖金类 (用于年终奖独立计税等场景): 0-否, 1-是
     */
    @Schema(description = "是否属于奖金类 (用于年终奖独立计税等场景): 0-否, 1-是")
    private Integer bonusFlag;
    /**
     * 是否与考勤强相关 (决定是否按出勤天数折算): 0-否, 1-是
     */
    @Schema(description = "是否与考勤强相关 (决定是否按出勤天数折算): 0-否, 1-是")
    private Integer attendanceRelatedFlag;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String description;
    /**
     * 排序值
     */
    @Schema(description = "排序值 (数值越小越靠前)")
    private Integer sortValue;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;

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
}