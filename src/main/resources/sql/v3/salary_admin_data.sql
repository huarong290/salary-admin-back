-- =========================================================
-- RBAC 权限系统初始化数据脚本
-- =========================================================

-- ==========================================================
-- 1. 初始化用户数据 (sys_user)
-- 初始密码均为: (密码统一为 123456 的 BCrypt 加密串)
-- ==========================================================
INSERT INTO `sys_user` (`id`, `username`, `password`, `salt`, `nickname`, `email`, `phone`, `sex`, `status`,
                        `create_by`)
VALUES (1, 'system', '$2a$10$4FGmjRDysGbW1t0yPDGxg.99sA3Qf97aHM0yhB6R4vektOZ/d3GFu', '', '系统维护员',
        'system@example.com', '13800000001', 1, 1, 'system'),
       (2, 'admin', '$2a$10$4FGmjRDysGbW1t0yPDGxg.99sA3Qf97aHM0yhB6R4vektOZ/d3GFu', '', '薪资管理员',
        'admin@example.com', '13800000002', 2, 1, 'system'),
       (3, 'user', '$2a$10$4FGmjRDysGbW1t0yPDGxg.99sA3Qf97aHM0yhB6R4vektOZ/d3GFu', '', '普通员工', 'user@example.com',
        '13800000003', 1, 1, 'system'),
       (4, 'test', '$2a$10$4FGmjRDysGbW1t0yPDGxg.99sA3Qf97aHM0yhB6R4vektOZ/d3GFu', '', '测试账号', 'test@example.com',
        '13800000004', 0, 1, 'system');

-- ==========================================================
-- 2. 初始化角色数据
-- ==========================================================
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_sort`, `role_status`, `role_desc`, `remark`, `create_by`)
VALUES (1, '超级管理员', 'SUPER_ADMIN', 1, 1, '系统最高权限', '拥有系统所有资源和操作权限', 'system'),
       (2, '普通管理员', 'ADMIN', 2, 1, '普通管理员权限', '普通管理员权限', 'system'),
       (3, '普通员工', 'USER', 2, 1, '普通员工权限', '普通业务线办理权限', 'system'),
       (4, '测试人员', 'TEST', 3, 1, '测试人员权限', '仅用于查看系统的只读账号', 'system');

-- ==========================================================
-- 3. 初始化用户角色关联 (sys_user_role)
-- ==========================================================
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `create_by`)
VALUES (1, 1, 'system'), -- system -> 超级管理员
       (2, 2, 'system'), -- admin  -> 普通管理员
       (3, 3, 'system'), -- user   -> 普通员工
       (4, 4, 'system');
-- test   -> 测试人员

-- ==========================================================
-- 4. 初始化菜单与权限数据 (严格对应前端 Layout 与 views 目录结构)
-- 菜单类型 (1:目录, 2:菜单, 3:按钮)
-- ==========================================================
TRUNCATE TABLE `sys_menu`;
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`,
                        `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`)
VALUES
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
(110, '角色管理', 'sys_role', 'role', 'system/role/RolePage', '', 'Avatar', 'sys:role:list', 2, 10, 2, 1, 1),
(111, '角色查询', 'sys_role_query', '', '', '', '', 'sys:role:query', 3, 110, 1, 1, 1),
(112, '角色新增', 'sys_role_add', '', '', '', '', 'sys:role:add', 3, 110, 2, 1, 1),
(113, '角色修改', 'sys_role_edit', '', '', '', '', 'sys:role:edit', 3, 110, 3, 1, 1),
(114, '角色删除', 'sys_role_del', '', '', '', '', 'sys:role:del', 3, 110, 4, 1, 1),
(115, '分配权限', 'sys_role_assign', '', '', '', '', 'sys:role:assign', 3, 110, 5, 1, 1),

-- 菜单管理及其按钮
(120, '菜单管理', 'sys_menu', 'menu', 'system/menu/MenuPage', '', 'Menu', 'sys:menu:list', 2, 10, 3, 1, 1),
(121, '菜单查询', 'sys_menu_query', '', '', '', '', 'sys:menu:query', 3, 120, 1, 1, 1),
(122, '菜单新增', 'sys_menu_add', '', '', '', '', 'sys:menu:add', 3, 120, 2, 1, 1),
(123, '菜单修改', 'sys_menu_edit', '', '', '', '', 'sys:menu:edit', 3, 120, 3, 1, 1),
(124, '菜单删除', 'sys_menu_del', '', '', '', '', 'sys:menu:del', 3, 120, 4, 1, 1),

-- 字典管理目录 (挂在系统管理下)

(130, '字典管理', 'sys_dict', 'dict', 'system/dict/DictPage', '', 'Collection', 'sys:dict:list', 2, 10, 4, 1, 1),
(131, '字典查询', 'sys_dict_query', '', '', '', '', 'sys:dict:query', 3, 130, 1, 1, 1),
(132, '字典新增', 'sys_dict_add', '', '', '', '', 'sys:dict:add', 3, 130, 2, 1, 1),
(133, '字典修改', 'sys_dict_edit', '', '', '', '', 'sys:dict:edit', 3, 130, 3, 1, 1),
(134, '字典删除', 'sys_dict_del', '', '', '', '', 'sys:dict:del', 3, 130, 4, 1, 1),
(135, '字典刷新缓存', 'sys_dict_refresh', '', '', '', '', 'sys:dict:refresh', 3, 130, 5, 1, 1);
-- ==========================================================
-- 5. 初始化角色菜单关联 (sys_role_menu)
-- ==========================================================

-- ----------------------------------------------------------
-- 5.1 超级管理员 (SUPER_ADMIN): 拥有所有菜单和按钮权限 (ID: 1-124)
-- ----------------------------------------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
SELECT 1, id, 'system'
FROM `sys_menu`;

-- ----------------------------------------------------------
-- 5.2 普通管理员 (ADMIN): 拥有工作台 + 系统管理(除敏感操作)
-- ----------------------------------------------------------
-- 基础目录与工作台
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
VALUES (2, 1, 'system'),
       (2, 2, 'system'),
       (2, 10, 'system');

-- 用户/角色/菜单的查询、新增、修改权限 (剔除删除 104, 114, 124 和 分配权限 115)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
SELECT 2, id, 'system'
FROM `sys_menu`
WHERE id IN (100, 101, 102, 103, 105, 110, 111, 112, 113, 120, 121, 122, 123);

-- ----------------------------------------------------------
-- 5.3 普通员工 (USER) & 测试人员 (TEST): 仅工作台 + 基础查看
-- ----------------------------------------------------------
-- 工作台 (1, 2)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
VALUES (3, 1, 'system'),
       (3, 2, 'system'),
       (4, 1, 'system'),
       (4, 2, 'system');

-- 给测试人员增加系统管理的只读查看权限 (101, 111, 121)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
VALUES (4, 10, 'system'),
       (4, 100, 'system'),
       (4, 101, 'system'),
       (4, 110, 'system'),
       (4, 111, 'system'),
       (4, 120, 'system'),
       (4, 121, 'system');


-- ==========================================================
-- 6. 字典类型表 (sys_dict_type)
-- ==========================================================
-- ==========================================================
-- 初始化字典类型数据
-- ==========================================================
INSERT INTO `sys_dict_type` (`dict_type_code`, `dict_type_name`, `dict_category`, `status`, `remark`, `create_by`)
VALUES
-- 收入类类别 (income)
('income_type', '收入项类型', 'income', 1, '薪资组成中的收入项目', 'system'),

-- 扣款类类别 (deduction)
('deduction_type', '扣款项类型', 'deduction', 1, '薪资组成中的扣款项目', 'system'),

-- 其他系统类别 (other)
('currency_type', '结算币种', 'other', 1, '薪资结算使用的币种', 'system'),
('payment_status', '支付状态', 'other', 1, '薪资发放单据状态', 'system'),
('sys_user_sex', '性别', 'other', 1, '用户性别', 'system');

-- ==========================================================
-- 7. 字典项明细表 (sys_dict_item) - 严格对应你调整后的字段 dict_item_label
-- ==========================================================
-- ==========================================================
-- 初始化字典明细项数据
-- ==========================================================
INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`, `status`, `create_by`)
VALUES
-- 1. 收入项类型 (income_type)
('income_type', 'BASIC_SALARY', '基本工资', 1, 1, 'system'),
('income_type', 'HOUSING_SUB', '住房补贴', 2, 1, 'system'),
('income_type', 'MEAL_SUB', '餐补', 3, 1, 'system'),
('income_type', 'OVERTIME_PAY', '加班费', 4, 1, 'system'),

-- 2. 扣款项类型 (deduction_type)
('deduction_type', 'ABSENCE', '缺勤扣款', 1, 1, 'system'),
('deduction_type', 'SSS', '菲律宾社保(SSS)', 2, 1, 'system'),
('deduction_type', 'PHILHEALTH', '菲律宾医保', 3, 1, 'system'),
('deduction_type', 'PAGIBIG', '住房公积金(Pag-IBIG)', 4, 1, 'system'),
('deduction_type', 'TAX', '个人所得税', 5, 1, 'system'),

-- 3. 结算币种 (currency_type)
('currency_type', 'CNY', '人民币', 1, 1, 'system'),
('currency_type', 'PHP', '菲律宾比索', 2, 1, 'system'),
('currency_type', 'USDT', '泰达币', 3, 1, 'system'),
('currency_type', 'USD', '美元', 4, 1, 'system'),

-- 4. 支付状态 (payment_status)
('payment_status', '0', '待审核', 1, 1, 'system'),
('payment_status', '1', '已确认', 2, 1, 'system'),
('payment_status', '2', '支付中', 3, 1, 'system'),
('payment_status', '3', '支付成功', 4, 1, 'system'),
('payment_status', '4', '支付失败', 5, 1, 'system'),

-- 5. 用户性别 (sys_user_sex)
('sys_user_sex', '0', '未知', 1, 1, 'system'),
('sys_user_sex', '1', '男', 2, 1, 'system'),
('sys_user_sex', '2', '女', 3, 1, 'system');




-- 字典管理目录 (挂在系统管理下)
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`) VALUES
                                                                                                                                                                                                                       (130, '字典管理', 'sys_dict', 'dict', 'system/dict/DictPage', '', 'Collection', 'sys:dict:list', 2, 10, 4, 1, 1),
                                                                                                                                                                                                                       (131, '新增字典类型', 'sys_dict_type_add', '', '', '', '', 'sys:dict_type:add', 3, 130, 1, 1, 1),
                                                                                                                                                                                                                       (132, '删除字典类型', 'sys_dict_type_del', '', '', '', '', 'sys:dict_type:del', 3, 130, 2, 1, 1),
                                                                                                                                                                                                                       (133, '修改字典类型', 'sys_dict_type_edit', '', '', '', '', 'sys:dict_type:edit', 3, 130, 3, 1, 1),
                                                                                                                                                                                                                       (134, '查询字典类型', 'sys_dict_type_query', '', '', '', '', 'sys:dict_type:query', 3, 130, 4, 1, 1),
                                                                                                                                                                                                                       (135, '新增字段项', 'sys_dict_item_add', '', '', '', '', 'sys:dict_item:add', 3, 130, 5, 1, 1),
                                                                                                                                                                                                                       (136, '删除字典项', 'sys_dict_item_del', '', '', '', '', 'sys:dict_item:del', 3, 130, 6, 1, 1),
                                                                                                                                                                                                                       (137, '修改字典项', 'sys_dict_item_edit', '', '', '', '', 'sys:dict_item:edit', 3, 130, 7, 1, 1),

                                                                                                                                                                                                                       (138, '查询字典类型', 'sys_dict_item_query', '', '', '', '', 'sys:dict_item:query', 3, 130, 8, 1, 1);


-- 分配给超级管理员 (SUPER_ADMIN ID: 1)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
SELECT 1, id, 'system' FROM `sys_menu` WHERE id BETWEEN 130 AND 135;

-- 分配给普通管理员 (ADMIN ID: 2, 剔除删除权限)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
SELECT 2, id, 'system' FROM `sys_menu` WHERE id IN (130, 131, 132, 133, 135);