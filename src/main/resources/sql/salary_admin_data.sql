-- =========================================================
-- RBAC 权限系统初始化数据脚本
-- 密码默认全为: 123456 (BCrypt加密密文)
-- =========================================================

-- ----------------------------
-- 1. 初始化用户数据 (sys_user)
-- ----------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `sex`, `status`, `create_time`) VALUES
                                                                                                                        (1, 'admin', '$2a$10$4FGmjRDysGbW1t0yPDGxg.99sA3Qf97aHM0yhB6R4vektOZ/d3GFu', '超级管理员', 'admin@salary.com', '13800000001', 1, 1, NOW()),
                                                                                                                        (2, 'zhaoyun', '$2a$10$4FGmjRDysGbW1t0yPDGxg.99sA3Qf97aHM0yhB6R4vektOZ/d3GFu', 'Harvey', 'harvey@salary.com', '13800000002', 1, 1, NOW());

-- ----------------------------
-- 2. 初始化角色数据 (sys_role)
-- ----------------------------
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_sort`, `role_status`, `role_desc`, `remark`, `create_time`) VALUES
                                                                                                                              (1, '超级管理员', 'ADMIN', 1, 1, '系统最高权限', '拥有系统所有资源的访问权限', NOW()),
                                                                                                                              (2, '普通员工', 'COMMON', 2, 1, '普通员工角色', '仅拥有基础查询权限', NOW());

-- ----------------------------
-- 3. 初始化菜单与按钮数据 (sys_menu)
-- menu_type 说明: 1-目录, 2-菜单页面, 3-按钮
-- ----------------------------
-- 【顶级目录】
INSERT INTO `sys_menu` (`id`, `menu_parent_id`, `menu_name`, `menu_code`, `menu_type`, `menu_path`, `menu_component`, `menu_icon`, `menu_permission`, `menu_sort`) VALUES
    (1, 0, '系统管理', 'sys_manage', 1, '/system', 'Layout', 'system', '', 1);

-- 【二级菜单】
INSERT INTO `sys_menu` (`id`, `menu_parent_id`, `menu_name`, `menu_code`, `menu_type`, `menu_path`, `menu_component`, `menu_icon`, `menu_permission`, `menu_sort`) VALUES
                                                                                                                                                                       (101, 1, '用户管理', 'sys_user', 2, 'user', 'system/user/index', 'user', 'sys:user:list', 1),
                                                                                                                                                                       (102, 1, '角色管理', 'sys_role', 2, 'role', 'system/role/index', 'peoples', 'sys:role:list', 2),
                                                                                                                                                                       (103, 1, '菜单管理', 'sys_menu', 2, 'menu', 'system/menu/index', 'tree-table', 'sys:menu:list', 3);

-- 【三级按钮 - 用户管理】
INSERT INTO `sys_menu` (`id`, `menu_parent_id`, `menu_name`, `menu_code`, `menu_type`, `menu_permission`, `menu_sort`) VALUES
                                                                                                                           (1011, 101, '用户查询', 'sys_user_query', 3, 'sys:user:query', 1),
                                                                                                                           (1012, 101, '用户新增', 'sys_user_add', 3, 'sys:user:add', 2),
                                                                                                                           (1013, 101, '用户修改', 'sys_user_edit', 3, 'sys:user:edit', 3),
                                                                                                                           (1014, 101, '用户删除', 'sys_user_del', 3, 'sys:user:del', 4),
                                                                                                                           (1015, 101, '分配角色', 'sys_user_assign', 3, 'sys:user:assign', 5);

-- 【三级按钮 - 角色管理】
INSERT INTO `sys_menu` (`id`, `menu_parent_id`, `menu_name`, `menu_code`, `menu_type`, `menu_permission`, `menu_sort`) VALUES
                                                                                                                           (1021, 102, '角色查询', 'sys_role_query', 3, 'sys:role:query', 1),
                                                                                                                           (1022, 102, '角色新增', 'sys_role_add', 3, 'sys:role:add', 2),
                                                                                                                           (1023, 102, '角色修改', 'sys_role_edit', 3, 'sys:role:edit', 3),
                                                                                                                           (1024, 102, '角色删除', 'sys_role_del', 3, 'sys:role:del', 4),
                                                                                                                           (1025, 102, '分配权限', 'sys_role_assign', 3, 'sys:role:assign', 5);

-- 【三级按钮 - 菜单管理】
INSERT INTO `sys_menu` (`id`, `menu_parent_id`, `menu_name`, `menu_code`, `menu_type`, `menu_permission`, `menu_sort`) VALUES
                                                                                                                           (1031, 103, '菜单查询', 'sys_menu_query', 3, 'sys:menu:query', 1),
                                                                                                                           (1032, 103, '菜单新增', 'sys_menu_add', 3, 'sys:menu:add', 2),
                                                                                                                           (1033, 103, '菜单修改', 'sys_menu_edit', 3, 'sys:menu:edit', 3),
                                                                                                                           (1034, 103, '菜单删除', 'sys_menu_del', 3, 'sys:menu:del', 4);

-- ----------------------------
-- 4. 初始化 用户-角色 关联 (sys_user_role)
-- ----------------------------
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
                                                       (1, 1), -- admin 绑定 超级管理员
                                                       (2, 2); -- harvey 绑定 普通员工

-- ----------------------------
-- 5. 初始化 角色-菜单 关联 (sys_role_menu)
-- ----------------------------
-- 超级管理员(role_id=1) 拥有所有权限菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
                                                       (1, 1),
                                                       (1, 101), (1, 102), (1, 103),
                                                       (1, 1011), (1, 1012), (1, 1013), (1, 1014), (1, 1015),
                                                       (1, 1021), (1, 1022), (1, 1023), (1, 1024), (1, 1025),
                                                       (1, 1031), (1, 1032), (1, 1033), (1, 1034);

-- 普通员工(role_id=2) 仅拥有系统管理目录和列表查询权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
                                                       (2, 1),
                                                       (2, 101), (2, 102), (2, 103),
                                                       (2, 1011), (2, 1021), (2, 1031);