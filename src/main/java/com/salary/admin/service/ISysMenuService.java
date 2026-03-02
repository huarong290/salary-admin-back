package com.salary.admin.service;

import com.salary.admin.model.dto.menu.MenuAddReqDTO;
import com.salary.admin.model.dto.menu.MenuEditReqDTO;
import com.salary.admin.model.entity.sys.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.model.vo.menu.MenuTreeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限菜单表 服务类
 * </p>
 *
 * @author system
 * @since 2026-02-22
 */
public interface ISysMenuService extends IService<SysMenu> {

    // ======================== 1. 核心权限与路由查询 ========================
    /**
     * 根据用户 ID 查询所有去重后的权限标识码
     *
     * @param userId 用户的唯一标识（主键 ID）
     * @return 去重后的权限标识码集合，例如 ["sys:user:add", "sys:user:delete", "sys:menu:view"]
     */
    Set<String> selectPermissionsByUserId(Long userId);
    /**
     * 根据用户ID查询其可访问的菜单列表
     *
     * @param userId 用户的唯一标识（主键 ID）
     * @return 菜单实体列表，每个元素为 {@link SysMenu} 对象，包含菜单的基本信息
     */
    List<SysMenu> selectMenuByUserId(Long userId);

    /**
     * 将扁平的菜单列表，构建成前端所需的动态路由树形结构
     *
     * @param menuList 数据库查出的扁平菜单列表
     * @return 树形结构的菜单列表 (包含 Meta 信息)
     */
    List<MenuTreeVO> buildMenuTree(List<SysMenu> menuList);
    // ======================== 2. 菜单管理 CRUD ========================
    /**
     * 新增菜单
     *
     * @param reqDTO 新增参数
     * @return 新生成的菜单 ID
     */
    Long addMenu(MenuAddReqDTO reqDTO);

    /**
     * 修改菜单
     *
     * @param reqDTO 修改参数
     * @return 影响的行数
     */
    Integer editMenu(MenuEditReqDTO reqDTO);

    /**
     * 删除菜单
     *
     * @param id 菜单 ID
     * @return 是否成功
     */
    boolean deleteMenuById(Long id);

}
