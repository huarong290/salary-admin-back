package com.salary.admin.convert.menu;

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
 *
 * 使用场景：
 * - Controller 层接收前端传入的 DTO，调用 Service 层时转换为 Entity。
 * - Service 层查询数据库得到 Entity，返回给 Controller 时转换为 VO。
 * - 构建菜单树时，将平铺的 VO 转换为树形 VO。
 */
@Mapper(componentModel = "spring")
public interface SysMenuConvert {

    /**
     * 1. DTO -> Entity (新增/修改场景)
     */
    SysMenu toEntity(SysMenuDTO dto);

    /**
     * 2. Entity -> SysMenuVO (菜单管理列表展示)
     */
    SysMenuVO toVO(SysMenu entity);

    /**
     * 3.  集合对象映射：List<Entity> -> List<VO>
     * 对应场景：菜单管理页面的扁平化列表查询
     */
    List<SysMenuVO> toVOList(List<SysMenu> entities);

    /**
     * 4. Entity -> MenuTreeVO (登录后构建动态路由)
     * 排除 children 字段，并指定自定义转换方法生成 meta
     */
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "meta", source = ".", qualifiedByName = "toMetaVO")
    MenuTreeVO toTreeVO(SysMenu menu);

    /**
     * 5. 路由元信息映射：根据实体属性动态生成 MetaVO
     */
    @Named("toMetaVO")
    default MetaVO toMetaVO(SysMenu menu) {
        if (menu == null) return null;
        return MetaVO.builder()
                .title(menu.getMenuName())
                .icon(menu.getMenuIcon())
                // 数据库 1:正常显示, 0:隐藏 -> MetaVO hidden: true(隐藏)
                .hidden(Objects.equals(menu.getMenuVisible(), 0))
                .keepAlive(true) // 默认开启页面缓存
                .link(menu.getMenuPath() != null && menu.getMenuPath().startsWith("http")
                        ? menu.getMenuPath() : null)
                .affix(false)
                .build();
    }

    /**
     * 6. 树形结构组装核心逻辑
     * @param menuList 数据库查出的扁平 List (通常只含 M/C 类型)
     * @return 递归构建好的 TreeVO 列表
     */
    default List<MenuTreeVO> buildMenuTree(List<SysMenu> menuList) {
        if (CollectionUtils.isEmpty(menuList)) {
            return new ArrayList<>();
        }

        // 步骤1：Entity 批量转为 TreeVO（此时 meta 自动填充）
        List<MenuTreeVO> allNodes = menuList.stream()
                .map(this::toTreeVO)
                .collect(Collectors.toList());

        // 步骤2：流式递归找爸爸，构建层级结构
        return allNodes.stream()
                .filter(node -> Objects.equals(node.getMenuParentId(), 0L)) // 找顶级
                .peek(node -> node.setChildren(findChildren(node, allNodes)))
                .sorted((m1, m2) -> m1.getMenuSort().compareTo(m2.getMenuSort())) // 顶级排序
                .collect(Collectors.toList());
    }

    /**
     * 私有方法：递归寻找子节点并排序
     */
    private List<MenuTreeVO> findChildren(MenuTreeVO parent, List<MenuTreeVO> allNodes) {
        return allNodes.stream()
                .filter(node -> Objects.equals(node.getMenuParentId(), parent.getId()))
                .peek(node -> node.setChildren(findChildren(node, allNodes)))
                .sorted((m1, m2) -> m1.getMenuSort().compareTo(m2.getMenuSort()))
                .collect(Collectors.toList());
    }
}
