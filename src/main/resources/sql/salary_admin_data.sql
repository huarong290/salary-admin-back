-- =========================================================
-- RBAC 权限系统初始化数据脚本
-- 密码默认全为: 123456 (BCrypt加密密文)
-- =========================================================

-- ==========================================================
-- 1. 初始化用户数据 (密码统一为 123456 的 BCrypt 加密串)
-- ==========================================================
TRUNCATE TABLE `sys_user`;
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `sex`, `status`, `create_by`) VALUES
                                                                                                                      (1, 'admin', '$2a$10$4FGmjRDysGbW1t0yPDGxg.99sA3Qf97aHM0yhB6R4vektOZ/d3GFu', '超级管理员', 'admin@example.com', '13800138000', 1, 1, 'system'),
                                                                                                                      (2, 'test', '$2a$10$4FGmjRDysGbW1t0yPDGxg.99sA3Qf97aHM0yhB6R4vektOZ/d3GFu', '测试人员', 'test@example.com', '13800138001', 1, 1, 'system');

-- ==========================================================
-- 2. 初始化角色数据
-- ==========================================================
TRUNCATE TABLE `sys_role`;
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_sort`, `role_status`, `role_desc`, `remark`, `create_by`) VALUES
                                                                                                                            (1, '超级管理员', 'ADMIN', 1, 1, '系统最高权限', '拥有系统所有资源和操作权限', 'system'),
                                                                                                                            (2, '普通员工', 'USER', 2, 1, '普通员工权限', '普通业务线办理权限', 'system'),
                                                                                                                            (3, '测试人员', 'TEST', 3, 1, '测试人员权限', '仅用于查看系统的只读账号', 'system');

-- ==========================================================
-- 3. 初始化菜单与权限数据 (严格对应前端 Layout 与 views 目录结构)
-- 菜单类型 (1:目录, 2:菜单, 3:按钮)
-- ==========================================================
TRUNCATE TABLE `sys_menu`;
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`) VALUES
-- 工作台模块
(1, '工作台', 'dashboard_dir', '/dashboard', 'Layout', '/dashboard/index', 'Odometer', '', 1, 0, 1, 1, 1),
(2, '工作台面板', 'dashboard_index', 'index', 'dashboard/index', '', 'DataBoard', '', 2, 1, 1, 1, 1),

-- 系统管理目录
(10, '系统管理', 'sys_dir', '/system', 'Layout', '', 'Setting', '', 1, 0, 2, 1, 1),

-- 用户管理及其按钮
(100, '用户管理', 'sys_user', 'user', 'system/user/UserPage', '', 'User', 'sys:user:list', 2, 10, 1, 1, 1),
(101, '用户查询', 'sys_user_query', '', '', '', '', 'sys:user:query', 3, 100, 1, 1, 1),
(102, '用户新增', 'sys_user_add', '', '', '', '', 'sys:user:add', 3, 100, 2, 1, 1),
(103, '用户修改', 'sys_user_edit', '', '', '', '', 'sys:user:edit', 3, 100, 3, 1, 1),
(104, '用户删除', 'sys_user_del', '', '', '', '', 'sys:user:del', 3, 100, 4, 1, 1),
(105, '分配角色', 'sys_user_assign', '', '', '', '', 'sys:user:assign', 3, 100, 5, 1, 1),

-- 角色管理及其按钮
(110, '角色管理', 'sys_role', 'role', 'system/role/RolePage', '', 'Avatar', 'sys:role:list', 2, 10, 2, 0, 1),
(111, '角色查询', 'sys_role_query', '', '', '', '', 'sys:role:query', 3, 110, 1, 1, 1),
(112, '角色新增', 'sys_role_add', '', '', '', '', 'sys:role:add', 3, 110, 2, 1, 1),
(113, '角色修改', 'sys_role_edit', '', '', '', '', 'sys:role:edit', 3, 110, 3, 1, 1),
(114, '角色删除', 'sys_role_del', '', '', '', '', 'sys:role:del', 3, 110, 4, 1, 1),
(115, '分配权限', 'sys_role_assign', '', '', '', '', 'sys:role:assign', 3, 110, 5, 1, 1),

-- 菜单管理及其按钮
(120, '菜单管理', 'sys_menu_mgr', 'menu', 'system/menu/MenuPage', '', 'Menu', 'sys:menu:list', 2, 10, 3, 1, 1),
(121, '菜单查询', 'sys_menu_query', '', '', '', '', 'sys:menu:query', 3, 120, 1, 1, 1),
(122, '菜单新增', 'sys_menu_add', '', '', '', '', 'sys:menu:add', 3, 120, 2, 1, 1),
(123, '菜单修改', 'sys_menu_edit', '', '', '', '', 'sys:menu:edit', 3, 120, 3, 1, 1),
(124, '菜单删除', 'sys_menu_del', '', '', '', '', 'sys:menu:del', 3, 120, 4, 1, 1);

-- ==========================================================
-- 4. 初始化用户-角色映射
-- ==========================================================
TRUNCATE TABLE `sys_user_role`;
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES
                                                             (1, 1, 1), -- admin 赋予 超级管理员
                                                             (2, 2, 3); -- test 赋予 测试人员

-- ==========================================================
-- 5. 初始化角色-菜单映射
-- ==========================================================
TRUNCATE TABLE `sys_role_menu`;

-- 5.1 超级管理员 (ID: 1) 拥有所有权限 (涵盖目录、菜单、按钮)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
                                                       (1, 1), (1, 2),
                                                       (1, 10),
                                                       (1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105),
                                                       (1, 110), (1, 111), (1, 112), (1, 113), (1, 114), (1, 115),
                                                       (1, 120), (1, 121), (1, 122), (1, 123), (1, 124);

-- 5.2 测试人员 (ID: 3) 仅拥有工作台和各个列表的查询权限 (不给增删改和分配权限)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
                                                       (3, 1), (3, 2), -- 能够看到工作台
                                                       (3, 10),        -- 能够展开系统管理目录
                                                       (3, 100), (3, 101), -- 只能看到用户列表并查询
                                                       (3, 110), (3, 111), -- 只能看到角色列表并查询
                                                       (3, 120), (3, 121); -- 只能看到菜单列表并查询