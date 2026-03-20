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
 * 系统字典项表
 *
 * @author system
 * @since 2026-03-20
 */
@Schema(name = "SysDictItem", description = "系统字典项表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dict_item")
public class SysDictItem extends BaseEntity<SysDictItem> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属字典类型code
     */
    @Schema(description = "所属字典类型code")
    @TableField("dict_type_code")
    private String dictTypeCode;
    /**
     * 字典项标签（如 男、女）
     */
    @Schema(description = "字典项标签（如 男、女）")
    @TableField("dict_item_label")
    private String dictItemLabel;
    /**
     * 字典项值（如 1、0）
     */
    @Schema(description = "字典项值（如 1、0）")
    @TableField("dict_item_value")
    private String dictItemValue;
    /**
     * 排序值，越小越靠前
     */
    @Schema(description = "排序值，越小越靠前")
    @TableField("sort")
    private Integer sort;
    /**
     * 是否启用，0表示启用
     */
    @Schema(description = "是否启用，0表示启用")
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