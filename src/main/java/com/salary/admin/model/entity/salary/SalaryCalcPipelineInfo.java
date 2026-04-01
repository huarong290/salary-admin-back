package com.salary.admin.model.entity.salary;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.salary.admin.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 薪资计算管道主表
 *
 * 存储管道的元信息：编码、名称、版本、是否默认、状态等
 * 用于管理不同版本的薪资计算流程
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryCalcPipelineInfo", description = "薪资计算管道主表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_calc_pipeline_info")
public class SalaryCalcPipelineInfo extends BaseEntity<SalaryCalcPipelineInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 管道唯一编码
     */
    @Schema(description = "管道唯一编码")
    @TableField("pipeline_code")
    private String pipelineCode;

    /**
     * 管道名称
     */
    @Schema(description = "管道名称")
    @TableField("pipeline_name")
    private String pipelineName;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    @TableField("version")
    private Integer version;

    /**
     * 是否默认流程 (1默认 0否)
     */
    @Schema(description = "是否默认流程 (1默认 0否)")
    @TableField("default_flag")
    private Integer defaultFlag;

    /**
     * 状态 (1启用 0停用)
     */
    @Schema(description = "状态 (1启用 0停用)")
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
