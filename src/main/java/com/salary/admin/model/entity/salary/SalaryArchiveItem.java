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
import java.math.BigDecimal;

/**
 * 薪资档案固定项明细表
 *
 * @author system
 * @since 2026-03-27
 */
@Schema(name = "SalaryArchiveItem", description = "薪资档案固定项明细表")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("salary_archive_item")
public class SalaryArchiveItem extends BaseEntity<SalaryArchiveItem> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 档案ID
     */
    @Schema(description = "档案ID")
    @TableField("archive_id")
    private Long archiveId;
    /**
     * 项目类型: 1-收入项, 2-扣款项
     */
    @Schema(description = "项目类型: 1-收入项, 2-扣款项")
    @TableField("item_type")
    private Integer itemType;
    /**
     * 关联salary_item_config.id
     */
    @Schema(description = "关联salary_item_config.id")
    @TableField("item_config_id")
    private Long itemConfigId;
    /**
     * 项目名称快照
     */
    @Schema(description = "项目名称快照")
    @TableField("type_name")
    private String typeName;
    /**
     * 分类字典值快照
     */
    @Schema(description = "分类字典值快照")
    @TableField("category_dict_value")
    private String categoryDictValue;
    /**
     * 表达式脚本
     */
    @Schema(description = "表达式脚本")
    @TableField("rule_script")
    private String ruleScript;
    /**
     * 固定金额
     */
    @Schema(description = "固定金额")
    @TableField("amount")
    private BigDecimal amount;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}