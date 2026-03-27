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
 * 薪资系统全局配置表
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryConfig", description = "薪资系统全局配置表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_config")
public class SalaryConfig extends BaseEntity<SalaryConfig> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 配置键
     */
    @Schema(description = "配置键")
    @TableField("config_key")
    private String configKey;
    /**
     * 配置值
     */
    @Schema(description = "配置值")
    @TableField("config_value")
    private String configValue;
    /**
     * 配置名称
     */
    @Schema(description = "配置名称")
    @TableField("config_name")
    private String configName;
    /**
     * 值类型: string, number, boolean, json
     */
    @Schema(description = "值类型: string, number, boolean, json")
    @TableField("config_type")
    private String configType;
    /**
     * 配置分组 (如: calc_rule, notification)
     */
    @Schema(description = "配置分组 (如: calc_rule, notification)")
    @TableField("config_group")
    private String configGroup;
    /**
     * 是否激活 (1:是,0:否)
     */
    @Schema(description = "是否激活 (1:是,0:否)")
    @TableField("active_flag")
    private Boolean activeFlag;
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