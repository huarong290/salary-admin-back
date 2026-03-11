package com.salary.admin.model.vo.salary.incometype;

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
     * 分类
     */
    @Schema(description = "分类 (如: 固定工资, 补贴, 奖金)")
    private String category;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;


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