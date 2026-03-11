package com.salary.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.salary.admin.convert.menu.SysMenuConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SysMenuExtMapper;
import com.salary.admin.model.dto.menu.MenuAddReqDTO;
import com.salary.admin.model.dto.menu.MenuEditReqDTO;
import com.salary.admin.model.entity.sys.SysMenu;
import com.salary.admin.model.vo.menu.MenuTreeVO;
import com.salary.admin.service.ISysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 权限菜单表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
@Service
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuExtMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private  SysMenuExtMapper sysMenuExtMapper;

    @Autowired
    private SysMenuConvert sysMenuConvert;

    // ======================== 1. 核心权限与路由查询 ========================

    @Override
    public Set<String> selectPermissionsByUserId(Long userId) {
        return sysMenuExtMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public List<SysMenu> selectMenuByUserId(Long userId) {

        return sysMenuExtMapper.selectMenuByUserId(userId);
    }

    @Override
    public List<MenuTreeVO> buildMenuTree(List<SysMenu> menuList) {
        if (menuList == null || menuList.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 将 Entity 批量转为 TreeVO (此时 meta 已由 Convert 的 @AfterMapping/自定义逻辑 生成)
        List<MenuTreeVO> allNodes = menuList.stream()
                .map(sysMenuConvert::toTreeVO)
                .collect(Collectors.toList());

        // 2. 核心算法：组装树形结构并排序
        return allNodes.stream()
                // 找顶级节点 (父ID为0)
                .filter(node -> Long.valueOf(0L).equals(node.getMenuParentId()))
                // 💡 规范：使用 map 而非 peek，防止并发修改副作用
                .map(node -> {
                    node.setChildren(findChildren(node, allNodes));
                    return node;
                })
                // 💡 吸纳你的优点：顶级菜单严格按照 menuSort 排序
                .sorted((m1, m2) -> {
                    Integer sort1 = m1.getMenuSort() == null ? 0 : m1.getMenuSort();
                    Integer sort2 = m2.getMenuSort() == null ? 0 : m2.getMenuSort();
                    return sort1.compareTo(sort2);
                })
                .collect(Collectors.toList());
    }

    /**
     * 递归寻找子节点并排序
     */
    private List<MenuTreeVO> findChildren(MenuTreeVO parent, List<MenuTreeVO> allNodes) {
        return allNodes.stream()
                .filter(node -> Objects.equals(node.getMenuParentId(), parent.getId()))
                .map(node -> {
                    node.setChildren(findChildren(node, allNodes));
                    return node;
                })
                // 💡 子菜单同样需要严格排序
                .sorted((m1, m2) -> {
                    Integer sort1 = m1.getMenuSort() == null ? 0 : m1.getMenuSort();
                    Integer sort2 = m2.getMenuSort() == null ? 0 : m2.getMenuSort();
                    return sort1.compareTo(sort2);
                })
                .collect(Collectors.toList());
    }


    // ======================== 2. 菜单管理 CRUD ========================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addMenu(MenuAddReqDTO reqDTO) {
        // 校验父节点是否存在 (如果是顶级目录则跳过校验)
        if (!Long.valueOf(0L).equals(reqDTO.getMenuParentId())) {
            SysMenu parent = this.getById(reqDTO.getMenuParentId());
            if (parent == null) {
                throw new BusinessException("新增失败，父级菜单不存在");
            }
        }

        SysMenu sysMenu = sysMenuConvert.toEntity(reqDTO);
        this.save(sysMenu);
        return sysMenu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer editMenu(MenuEditReqDTO reqDTO) {
        // 🛡️ 核心防御：上级菜单绝不能选择自己，否则引发递归死循环 (OOM)
        if (reqDTO.getId().equals(reqDTO.getMenuParentId())) {
            throw new BusinessException("修改失败，上级菜单不能选择自己");
        }

        SysMenu sysMenu = sysMenuConvert.toEntity(reqDTO);
        boolean success = this.updateById(sysMenu);
        return success ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenuById(Long id) {
        // 🛡️ 核心防御：检查该菜单下是否还有子菜单
        LambdaQueryWrapper<SysMenu> childQuery = new LambdaQueryWrapper<>();
        childQuery.eq(SysMenu::getMenuParentId, id);
        if (this.count(childQuery) > 0) {
            throw new BusinessException("删除失败，该菜单下存在子节点，请先删除子节点");
        }

        // ⚠️ 进阶防御预留：在真实场景下，如果有角色已经绑定了这个菜单，强删会导致角色-菜单中间表出现脏数据
        // 如果你有 sysRoleMenuMapper，建议在这里 count 一下，若大于 0 则抛出异常

        return this.removeById(id);
    }
}
