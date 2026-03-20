package com.salary.admin.model.entity.sys;

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
 * 系统字典类型表
 *
 * @author system
 * @since 2026-03-20
 */
@Schema(name = "SysDictType", description = "系统字典类型表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dict_type")
public class SysDictType extends BaseEntity<SysDictType> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 字典类型编码（如 gender、status）
     */
    @Schema(description = "字典类型编码（如 gender、status）")
    @TableField("dict_type_code")
    private String dictTypeCode;
    /**
     * 字典类型名称（如 性别、状态）
     */
    @Schema(description = "字典类型名称（如 性别、状态）")
    @TableField("dict_type_name")
    private String dictTypeName;
    /**
     * 是否启用,0 表示启用
     */
    @Schema(description = "是否启用,0 表示启用")
    @TableField("status")
    private Integer status;
    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    @TableField("remark")
    private String remark;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}