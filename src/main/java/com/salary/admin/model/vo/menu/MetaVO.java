package com.salary.admin.model.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Meta VO
 * <p>
 * 前端路由扩展信息，用于控制菜单展示效果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "路由元信息")
public class MetaVO {

    /**
     * 菜单标题
     * 示例："角色管理"
     */
    @Schema(description = "菜单标题", example = "薪资看板")
    private String title;

    /**
     * 菜单图标
     * 示例："role"
     */
    @Schema(description = "菜单图标", example = "money")
    private String icon;

    /**
     * 是否缓存页面
     * true = 缓存，false = 不缓存
     */
    @Schema(description = "是否缓存页面 (用于 keep-alive)", example = "true")
    private Boolean keepAlive;

    /**
     * 是否隐藏菜单
     * true = 隐藏，false = 显示
     */
    @Schema(description = "是否隐藏菜单 (在左侧导航栏不可见)", example = "false")
    private Boolean hidden;
    /**
     * 🚨 大数据场景必备：支持跳转到外部链接 (如 Flink/Spark UI)
     */
    @Schema(description = "外链地址 (若填入则点击后跳转外链)", example = "http://flink-ui.local")
    private String link;
    /**
     * 是否固定在标签栏 (不可关闭)
     * true = 固定，false = 不固定
     */
    @Schema(description = "是否固定在标签栏 (不可关闭)", example = "false")
    private Boolean affix;
}
