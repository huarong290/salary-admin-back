package com.salary.admin.model.vo.salary.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 薪资系统全局配置 返回视图对象
 *
 * @author system
 * @since 2026-03-27
 */
@Data
@Schema(description = "薪资全局配置视图对象")
public class SalaryConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "配置键 (全局唯一)")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "值类型: string, number, boolean, json")
    private String configType;

    @Schema(description = "配置分组")
    private String configGroup;

    @Schema(description = "是否激活 (1:是, 0:否)")
    private Integer activeFlag;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;
}
