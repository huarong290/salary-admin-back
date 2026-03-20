package com.salary.admin.model.vo.salary.config;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 薪资系统全局配置视图对象
 * 用于展示系统层级的结算币种、计薪规则等核心参数
 *
 * @author system
 * @since 2026-03-20
 */
@Data
@Schema(description = "薪资系统全局配置视图对象")
public class SalaryConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置主键ID
     */
    @Schema(description = "配置主键ID")
    private Long id;

    /**
     * 配置键: 如 SETTLEMENT_CURRENCY
     */
    @Schema(description = "配置键: 如 SETTLEMENT_CURRENCY")
    private String configKey;

    /**
     * 配置值: 如 USDT
     */
    @Schema(description = "配置值: 如 USDT")
    private String configValue;

    /**
     * 配置名称: 如 默认结算币种
     */
    @Schema(description = "配置名称: 如 默认结算币种")
    private String configName;

    /**
     * 是否激活: 1-是, 0-否
     */
    @Schema(description = "是否激活: 1-是, 0-否")
    private Integer activeFlag;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    private String remark;
}