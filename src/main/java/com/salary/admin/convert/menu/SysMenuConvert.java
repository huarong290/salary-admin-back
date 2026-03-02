package com.salary.admin.convert.menu;

import com.salary.admin.model.dto.menu.MenuAddReqDTO;
import com.salary.admin.model.dto.menu.MenuEditReqDTO;
import com.salary.admin.model.dto.menu.SysMenuDTO;
import com.salary.admin.model.entity.sys.SysMenu;
import com.salary.admin.model.vo.menu.MenuTreeVO;
import com.salary.admin.model.vo.menu.MetaVO;
import com.salary.admin.model.vo.menu.SysMenuVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单对象转换器(MapStruct)
 * <p>
 * 使用 MapStruct 自动生成对象之间的转换代码。
 * 主要用于 SysMenuDTO、SysMenu 实体、SysMenuVO、SysMenuTreeVO 之间的转换。
 * 处理核心：属性映射、MetaVO 封装、以及树形递归构建。
 * 设计目的：
 * - 保持分层清晰：DTO 用于接收前端数据，Entity 对应数据库表，VO 用于返回前端展示。
 * - 避免手写重复的转换代码，提高开发效率。
 * - 保证对象之间字段映射的一致性，减少人为错误。
 * <p>
 * 使用场景：
 * - Controller 层接收前端传入的 DTO，调用 Service 层时转换为 Entity。
 * - Service 层查询数据库得到 Entity，返回给 Controller 时转换为 VO。
 * - 构建菜单树时，将平铺的 VO 转换为树形 VO。
 */
@Mapper(componentModel = "spring")
public interface SysMenuConvert {

    // ======================== 1. 入参转换 (DTO -> DO) ========================

    /**
     * 新增参数转实体
     */
    SysMenu toEntity(MenuAddReqDTO reqDTO);

    /**
     * 修改参数转实体
     * (MapStruct 会自动识别 MenuEditReqDTO 继承自 MenuAddReqDTO 的所有父类属性并进行映射)
     */
    SysMenu toEntity(MenuEditReqDTO reqDTO);
    // ======================== 2. 出参转换 (DO -> VO) ========================

    /**
     * 实体转普通视图对象 (用于后台菜单管理界面的表格/列表展示)
     */
    SysMenuVO toVO(SysMenu entity);

    /**
     * 实体列表转普通视图对象列表
     *
     */
    List<SysMenuVO> toVOList(List<SysMenu> entities);

    // ======================== 3. 动态路由树转换核心 ========================

    /**
     * 实体转树形节点对象 (专供前端 Vue Router 动态渲染使用)
     * 忽略 children 字段 (交由 Service 层的递归算法填充)
     * 动态生成 meta 字段
     */
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "meta", source = ".", qualifiedByName = "toMetaVO")
    MenuTreeVO toTreeVO(SysMenu menu);

    /**
     * 自定义映射逻辑：将扁平的菜单属性组装成前端路由标准的 MetaVO 对象
     */
    @Named("toMetaVO")
    default MetaVO toMetaVO(SysMenu menu) {
        if (menu == null) {
            return null;
        }
        return MetaVO.builder()
                .title(menu.getMenuName())
                .icon(menu.getMenuIcon())
                // 数据库显隐状态 (1:可见 0:隐藏) 映射为前端的 hidden (true:隐藏 false:可见)
                .hidden(Objects.equals(menu.getMenuVisible(), 0))
                .keepAlive(true) // 默认开启页面缓存
                // 如果路径是 http 开头，则作为外链处理
                .link(menu.getMenuPath() != null && menu.getMenuPath().startsWith("http")
                        ? menu.getMenuPath() : null)
                .affix(false)
                .build();
    }
}
