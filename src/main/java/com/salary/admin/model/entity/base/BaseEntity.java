package com.salary.admin.model.entity.base;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 基础实体类
 * 泛型 T 继承 Model<T>，用于保留 MyBatis-Plus 的 ActiveRecord 特性
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseEntity<T extends Model<T>> extends Model<T> {
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 修改者
     */
    @Schema(description = "修改者")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 删除标识 (0:未删, 1:已删)
     */
    @Schema(description = "删除标识 (0:未删, 1:已删)")
    @TableLogic
    @TableField("delete_flag")
    private Integer deleteFlag;
}
