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
-- ==========================================================
-- 模块 A：工作台与系统基础管理
-- ==========================================================
(1, '工作台', 'dashboard_dir', '/dashboard', 'Layout', '/dashboard/index', 'Odometer', '', 1, 0, 1, 1, 1),
(2, '工作台面板', 'dashboard_index', 'index', 'dashboard/index', '', 'DataBoard', '', 2, 1, 1, 1, 1),

(10, '系统管理', 'sys_dir', '/system', 'Layout', '', 'Setting', '', 1, 0, 2, 1, 1),
-- 用户管理
(100, '用户管理', 'sys_user', 'user', 'system/user/UserPage', '', 'User', 'sys:user:list', 2, 10, 1, 1, 1),
(101, '用户查询', 'sys_user_query', '', '', '', '', 'sys:user:query', 3, 100, 1, 1, 1),
(102, '用户新增', 'sys_user_add', '', '', '', '', 'sys:user:add', 3, 100, 2, 1, 1),
(103, '用户修改', 'sys_user_edit', '', '', '', '', 'sys:user:edit', 3, 100, 3, 1, 1),
(104, '用户删除', 'sys_user_del', '', '', '', '', 'sys:user:del', 3, 100, 4, 1, 1),
(105, '分配角色', 'sys_user_assign', '', '', '', '', 'sys:user:assign', 3, 100, 5, 1, 1),
-- 角色管理
(110, '角色管理', 'sys_role', 'role', 'system/role/RolePage', '', 'Avatar', 'sys:role:list', 2, 10, 2, 1, 1),
(111, '角色查询', 'sys_role_query', '', '', '', '', 'sys:role:query', 3, 110, 1, 1, 1),
(112, '角色新增', 'sys_role_add', '', '', '', '', 'sys:role:add', 3, 110, 2, 1, 1),
(113, '角色修改', 'sys_role_edit', '', '', '', '', 'sys:role:edit', 3, 110, 3, 1, 1),
(114, '角色删除', 'sys_role_del', '', '', '', '', 'sys:role:del', 3, 110, 4, 1, 1),
(115, '分配权限', 'sys_role_assign', '', '', '', '', 'sys:role:assign', 3, 110, 5, 1, 1),
-- 菜单管理
(120, '菜单管理', 'sys_menu', 'menu', 'system/menu/MenuPage', '', 'Menu', 'sys:menu:list', 2, 10, 3, 1, 1),
(121, '菜单查询', 'sys_menu_query', '', '', '', '', 'sys:menu:query', 3, 120, 1, 1, 1),
(122, '菜单新增', 'sys_menu_add', '', '', '', '', 'sys:menu:add', 3, 120, 2, 1, 1),
(123, '菜单修改', 'sys_menu_edit', '', '', '', '', 'sys:menu:edit', 3, 120, 3, 1, 1),
(124, '菜单删除', 'sys_menu_del', '', '', '', '', 'sys:menu:del', 3, 120, 4, 1, 1),
-- 字典管理
(130, '字典管理', 'sys_dict', 'dict', 'system/dict/DictPage', '', 'Collection', 'sys:dict:list', 2, 10, 4, 1, 1),
(131, '新增字典类型', 'sys_dict_type_add', '', '', '', '', 'sys:dict_type:add', 3, 130, 1, 1, 1),
(132, '删除字典类型', 'sys_dict_type_del', '', '', '', '', 'sys:dict_type:del', 3, 130, 2, 1, 1),
(133, '修改字典类型', 'sys_dict_type_edit', '', '', '', '', 'sys:dict_type:edit', 3, 130, 3, 1, 1),
(134, '查询字典类型', 'sys_dict_type_query', '', '', '', '', 'sys:dict_type:query', 3, 130, 4, 1, 1),
(135, '新增字段项', 'sys_dict_item_add', '', '', '', '', 'sys:dict_item:add', 3, 130, 5, 1, 1),
(136, '删除字典项', 'sys_dict_item_del', '', '', '', '', 'sys:dict_item:del', 3, 130, 6, 1, 1),
(137, '修改字典项', 'sys_dict_item_edit', '', '', '', '', 'sys:dict_item:edit', 3, 130, 7, 1, 1),
(138, '查询字典类型', 'sys_dict_item_query', '', '', '', '', 'sys:dict_item:query', 3, 130, 8, 1, 1),

-- 【0】薪资管理 (父级主目录)
(150, '薪资管理', 'salary_manage', '/salary', 'Layout', '', 'Money', '', 1, 0, 10, 1, 1),

-- ==========================================================
-- 【1】员工基础档案 (子菜单，Sort = 1，160号段)
-- ==========================================================
(160, '员工基础档案', 'salary_employee', 'employee', 'salary/employee/EmployeePage', '', 'User', 'salary:employee:list',
 2, 150, 1, 1, 1),
(161, '查看员工列表', 'salary_employee_query', '', '', '', '', 'salary:employee:query', 3, 160, 1, 1, 1),
(162, '新增员工档案', 'salary_employee_add', '', '', '', '', 'salary:employee:add', 3, 160, 2, 1, 1),
(163, '修改员工档案', 'salary_employee_edit', '', '', '', '', 'salary:employee:edit', 3, 160, 3, 1, 1),
(164, '销毁员工档案', 'salary_employee_del', '', '', '', '', 'salary:employee:del', 3, 160, 4, 1, 1),
(165, '查看档案详情', 'salary_employee_detail', '', '', '', '', 'salary:employee:detail', 3, 160, 5, 1, 1),

-- ==========================================================
-- 【2】薪资周期管理 (子菜单，Sort = 2，170号段)
-- ==========================================================
(170, '薪资周期管理', 'salary_period', 'period', 'salary/period/PeriodPage', '', 'Calendar', 'salary:period:list', 2,
 150, 2, 1, 1),
(171, '查看周期列表', 'salary_period_query', '', '', '', '', 'salary:period:query', 3, 170, 1, 1, 1),
(172, '新增/开启周期', 'salary_period_add', '', '', '', '', 'salary:period:add', 3, 170, 2, 1, 1),
(173, '修改周期数据', 'salary_period_edit', '', '', '', '', 'salary:period:edit', 3, 170, 3, 1, 1),
(174, '批量初始化周期', 'salary_period_init', '', '', '', '', 'salary:period:init', 3, 170, 4, 1, 1),
(175, '删除/销毁周期', 'salary_period_del', '', '', '', '', 'salary:period:del', 3, 170, 5, 1, 1),

-- ==========================================================
-- 【3】薪资项目配置 (子菜单，Sort = 3，180号段)
-- ==========================================================
(180, '薪资项目配置', 'salary_item_config', 'itemconfig', 'salary/itemconfig/ItemConfigPage', '', 'Setting',
 'salary:item_config:list', 2, 150, 3, 1, 1),
(181, '新增项目', 'salary_item_add', '', '', '', '', 'salary:item_config:add', 3, 180, 1, 1, 1),
(182, '修改项目', 'salary_item_edit', '', '', '', '', 'salary:item_config:edit', 3, 180, 2, 1, 1),
(183, '删除项目', 'salary_item_del', '', '', '', '', 'salary:item_config:del', 3, 180, 3, 1, 1),
(184, '同步配置', 'salary_item_refresh', '', '', '', '', 'salary:item_config:refresh', 3, 180, 4, 1, 1),

-- ==========================================================
-- 【4】薪资档案管理 (子菜单，Sort = 4，190号段)
-- ==========================================================
(190, '薪资档案管理', 'salary_archive', 'archive', 'salary/archive/ArchivePage', '', 'Document', 'salary:archive:list',
 2, 150, 4, 1, 1),
(191, '新员工定薪', 'salary_archive_init', '', '', '', '', 'salary:archive:init', 3, 190, 1, 1, 1),
(192, '调薪申请', 'salary_archive_adjust', '', '', '', '', 'salary:archive:adjust', 3, 190, 2, 1, 1),
(193, '调薪审批', 'salary_archive_audit', '', '', '', '', 'salary:archive:audit', 3, 190, 3, 1, 1),
(194, '查看详情', 'salary_archive_detail', '', '', '', '', 'salary:archive:detail', 3, 190, 4, 1, 1),
(195, '导出档案', 'salary_archive_export', '', '', '', '', 'salary:archive:export', 3, 190, 5, 1, 1),

-- ==========================================================
-- 【5】月度绩效大盘管理 (🔥 新增子菜单，Sort = 5，200号段)
-- ==========================================================
(200, '月度绩效管理', 'salary_kpi_ecord', 'kpirecord', 'salary/kpirecord/KpiRecordPage', '', 'TrendCharts',
 'salary:kpi_record:list', 2, 150, 5, 1, 1),
(201, '查询绩效大盘', 'salary_kpi_record_query', '', '', '', '', 'salary:kpi_record:query', 3, 200, 1, 1, 1),
(202, '派发绩效单', 'salary_kpi_record_init', '', '', '', '', 'salary:kpi_record:init', 3, 200, 2, 1, 1),
(203, '评估打分', 'salary_kpi_record_evaluate', '', '', '', '', 'salary:kpi_record:evaluate', 3, 200, 3, 1, 1),
(204, '审核定稿', 'salary_kpi_record_confirm', '', '', '', '', 'salary:kpi_record:confirm', 3, 200, 4, 1, 1),

-- ==========================================================
-- 【6】薪资引擎配置 (目录级别，Sort = 6，统领 210~230 号段)
-- ==========================================================
(210, '薪资引擎配置', 'salary_engine', 'engine', '', '', 'Operation', '', 1, 150, 6, 1, 1),

-- 6.1 薪资引擎 -> 计算规则库 (220 号段，父级ID为 210)
(220, '计算规则库', 'salary_calc_rule', 'calc-rule', 'salary/calcrule/CalcRulePage', '', 'Collection',
 'salary:rule:list', 2, 210, 1, 1, 1),
(221, '查询规则', 'salary_rule_query', '', '', '', '', 'salary:rule:query', 3, 220, 1, 1, 1),
(222, '新增规则', 'salary_rule_add', '', '', '', '', 'salary:rule:add', 3, 220, 2, 1, 1),
(223, '修改规则', 'salary_rule_edit', '', '', '', '', 'salary:rule:edit', 3, 220, 3, 1, 1),
(224, '删除规则', 'salary_rule_del', '', '', '', '', 'salary:rule:del', 3, 220, 4, 1, 1),

-- 6.2 薪资引擎 -> 核算管道编排 (230 号段，父级ID为 210)
(230, '核算管道编排', 'salary_calc_pipeline', 'calc-pipeline', 'salary/calcpipeline/CalcPipelinePage', '', 'Connection',
 'salary:pipeline:list', 2, 210, 2, 1, 1),
(231, '查询管道', 'salary_pipeline_query', '', '', '', '', 'salary:pipeline:query', 3, 230, 1, 1, 1),
(232, '新建管道', 'salary_pipeline_add', '', '', '', '', 'salary:pipeline:add', 3, 230, 2, 1, 1),
(233, '修改管道元数据', 'salary_pipeline_edit', '', '', '', '', 'salary:pipeline:edit', 3, 230, 3, 1, 1),
(234, '删除管道', 'salary_pipeline_del', '', '', '', '', 'salary:pipeline:del', 3, 230, 4, 1, 1),
(235, '发布瀑布流配置', 'salary_pipeline_design', '', '', '', '', 'salary:pipeline:design', 3, 230, 5, 1, 1),
(236, '设为系统默认', 'salary_pipeline_default', '', '', '', '', 'salary:pipeline:default', 3, 230, 6, 1, 1),
(237, '升级新版本', 'salary_pipeline_upgrade', '', '', '', '', 'salary:pipeline:upgrade', 3, 230, 7, 1, 1),


-- ==========================================================
-- 【7】专项调整(手工账) (子菜单，Sort = 7，240号段)
-- ==========================================================
(240, '专项调整(手工账)', 'salary_adjustment', 'adjustment', 'salary/adjustment/AdjustmentPage', '', 'PriceTag',
 'salary:adjustment:list', 2, 150, 7, 1, 1),
(241, '查询手工账', 'salary_adjustment_query', '', '', '', '', 'salary:adjustment:query', 3, 240, 1, 1, 1),
(242, '新增手工账', 'salary_adjustment_add', '', '', '', '', 'salary:adjustment:add', 3, 240, 2, 1, 1),
(243, '修改手工账', 'salary_adjustment_edit', '', '', '', '', 'salary:adjustment:edit', 3, 240, 3, 1, 1),
(244, '删除手工账', 'salary_adjustment_del', '', '', '', '', 'salary:adjustment:del', 3, 240, 4, 1, 1),
(245, '批量生效/撤回', 'salary_adjustment_audit', '', '', '', '', 'salary:adjustment:audit', 3, 240, 5, 1, 1),

-- ==========================================================
-- 【8】薪资汇总与发薪 (子菜单，Sort = 8，250号段)
-- ==========================================================
(250, '薪资汇总与发薪', 'salary_summary', 'summary', 'salary/summary/SummaryPage', '', 'Wallet', 'salary:summary:list',
 2, 150, 8, 1, 1),
(251, '查看汇总列表', 'salary_summary_query', '', '', '', '', 'salary:summary:query', 3, 250, 1, 1, 1),
(252, '查看工资条明细', 'salary_summary_detail', '', '', '', '', 'salary:summary:detail', 3, 250, 2, 1, 1),
(253, '锁定与解锁单据', 'salary_summary_lock', '', '', '', '', 'salary:summary:lock', 3, 250, 3, 1, 1),
(254, '执行引擎核算', 'salary_summary_calc', '', '', '', '', 'salary:summary:calc', 3, 250, 4, 1, 1);

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
INSERT INTO `sys_dict_type` (`dict_type_code`, `dict_type_name`, `dict_category`, `remark`, `create_by`)
VALUES
-- 薪资模块 (salary)
('salary_item_category', '薪资项目大类', 'salary', '定义薪资项的物理分类：收入、扣款、税费等', 'system'),
('salary_item_sub_type', '薪资项目细类', 'salary', '定义具体的业务逻辑标识，用于代码或脚本识别', 'system'),
('salary_calc_stage', '薪资核算阶段', 'salary', '定义薪资瀑布流引擎执行的物理次序阶段', 'system'),
('salary_tax_rule', '个税核算规则', 'salary', '定义员工发薪时适用的个人所得税计算标准及计税分支', 'system'),
-- ------------
-- 财务模块 (finance)
('payment_channel', '支付打款渠道', 'finance', '出纳打款的资金渠道', 'system'),
('settlement_currency', '结算本位币种', 'finance', '用于薪资计算和发放的币种', 'system'),


-- 人事模块 (hr)
('employment_status', '员工在职状态', 'hr', '影响薪资周期计算的状态', 'system');

-- ==========================================================
-- 7. 字典项明细表 (sys_dict_item) - 严格对应你调整后的字段 dict_item_label
-- ==========================================================
-- ==========================================================
-- 初始化字典明细项数据
-- ==========================================================
INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`, `create_by`)
VALUES
-- 薪资模块 (salary)
-- ==========================================
-- 1. 薪资项目大类
-- ==========================================
('salary_item_category', '1', '收入', 10, 'system'),
('salary_item_category', '2', '扣款', 20, 'system'),
('salary_item_category', '3', '税费', 30, 'system'),
('salary_item_category', '4', '公司支出', 40, 'system'),
-- ==========================================
-- 2. 薪资项目细类
-- ==========================================
('salary_item_sub_type', 'INC_BASE', '基本工资', 10, 'system'),
('salary_item_sub_type', 'INC_ALLOWANCE', '岗位津贴', 20, 'system'),
('salary_item_sub_type', 'INC_OVERTIME', '加班工资', 30, 'system'),
('salary_item_sub_type', 'INC_ATTENDANCE', '全勤奖', 35, 'system'),
('salary_item_sub_type', 'INC_BONUS', '绩效奖金', 40, 'system'),
('salary_item_sub_type', 'INC_YEAR_END', '年终奖', 50, 'system'),
('salary_item_sub_type', 'INC_SUBSIDY', '补贴', 60, 'system'),
('salary_item_sub_type', 'INC_FESTIVAL', '节日礼金', 70, 'system'),
('salary_item_sub_type', 'INC_OTHER', '其他收入', 80, 'system'),
('salary_item_sub_type', 'DED_ABSENT', '缺勤扣款', 100, 'system'),
('salary_item_sub_type', 'DED_LATE', '迟到早退', 110, 'system'),
('salary_item_sub_type', 'DED_FINE', '罚款', 115, 'system'),
('salary_item_sub_type', 'DED_LOAN', '借款扣还', 118, 'system'),
('salary_item_sub_type', 'DED_OTHER', '其他扣款', 120, 'system'),
('salary_item_sub_type', 'TAX_INCOME', '个人所得税', 200, 'system'),
('salary_item_sub_type', 'SI_PENSION', '养老保险(个人)', 210, 'system'),
('salary_item_sub_type', 'SI_MED', '医疗保险(个人)', 220, 'system'),
('salary_item_sub_type', 'SI_UNEMPLOYMENT', '失业保险(个人)', 225, 'system'),
('salary_item_sub_type', 'SI_HOUSING', '住房公积金(个人)', 230, 'system'),
('salary_item_sub_type', 'PHP_SSS', 'SSS (菲律宾社保)', 240, 'system'),
('salary_item_sub_type', 'TAX_LOCAL', '地方税', 250, 'system'),
('salary_item_sub_type', 'ER_PENSION', '养老保险(公司缴纳)', 300, 'system'),
('salary_item_sub_type', 'ER_MED', '医疗保险(公司缴纳)', 310, 'system'),
('salary_item_sub_type', 'ER_HOUSING', '住房公积金(公司缴纳)', 320, 'system'),
('salary_item_sub_type', 'ER_VISA', '签证费用', 330, 'system'),
('salary_item_sub_type', 'ER_TRAVEL', '差旅费', 340, 'system'),
('salary_item_sub_type', 'ER_INSURANCE', '商业保险', 350, 'system'),
('salary_item_sub_type', 'ER_OTHER', '其他公司支出', 360, 'system'),
-- ==========================================================
-- 3. 新增字典明细：严格对应 1~5 阶段
-- ==========================================================

('salary_calc_stage', '1', '基础薪资阶段', 10, 'system'),
('salary_calc_stage', '2', '津贴与奖金阶段', 20, 'system'),
('salary_calc_stage', '3', '扣款与社保阶段', 30, 'system'),
('salary_calc_stage', '4', '税务核算阶段', 40, 'system'),
('salary_calc_stage', '5', '最终汇总阶段', 50, 'system'),
-- ==========================================================
-- 4.个税核算规则明细项 (严格对应 Aviator 脚本的分支逻辑)
-- ==========================================================
('salary_tax_rule', 'TAX_RESIDENT_CN', '中国居民综合所得税', 10, 'system'),
('salary_tax_rule', 'TAX_LABOR', '劳务报酬所得税', 20, 'system'),
('salary_tax_rule', 'NO_TAX', '不计税(外包/免税)', 30, 'system'),
-- ==========================================
-- 5. 支付打款渠道
-- ==========================================
('payment_channel', 'bank_transfer_cmb', '招商银行企业代发', 10, 'system'),
('payment_channel', 'bank_transfer_icbc', '工商银行企业代发', 20, 'system'),
('payment_channel', 'bank_transfer_bdo', 'BDO Unibank', 30, 'system'),
('payment_channel', 'wallet_gcash', 'GCash 企业转账', 40, 'system'),
('payment_channel', 'alipay_batch', '支付宝批量代发', 50, 'system'),
('payment_channel', 'overseas_swift', '跨境电汇(SWIFT)', 60, 'system'),

-- ==========================================
-- 6. 结算本位币种
-- ==========================================
('settlement_currency', 'CNY', '人民币 (CNY)', 10, 'system'),
('settlement_currency', 'PHP', '菲律宾比索 (PHP)', 20, 'system'),
('settlement_currency', 'USDT', '泰达币(USDT)', 30, 'system'),
('settlement_currency', 'USD', '美元 (USD)', 40, 'system'),

-- ==========================================
-- 7. 员工在职状态
-- ==========================================
('employment_status', '1', '正式员工', 10, 'system'),
('employment_status', '2', '试用期员工', 20, 'system'),
('employment_status', '3', '实习生', 30, 'system'),
('employment_status', '4', '兼职/外包', 40, 'system'),
('employment_status', '0', '已离职', 50, 'system');



-- ==========================================================
-- 9. 统一标准化：系统薪资项目配置表 (salary_item_config)
-- 标准：env_var_name 严格遵循 lowerCamelCase (小驼峰)
-- ==========================================================
TRUNCATE TABLE `salary_item_config`;

-- 建议：如果表内已有数据，可以先 TRUNCATE TABLE salary_item_config;
INSERT INTO `salary_item_config`
(`item_code`, `item_name`, `item_category`, `category_dict_value`, `env_var_name`, `calc_priority`, `taxable_flag`,
 `tax_deductible_flag`, `fixed_flag`, `pinyin_code`, `sort_value`, `remark`)
VALUES
-- ----------------------------------------------------------
-- 【1】收入类 - 档案固定项 (Fixed Items)
-- ----------------------------------------------------------
('BASE_SALARY', '基本工资', 1, 'INC_BASE', 'baseSalary', 10, 1, 0, 1, 'jbgz', 10, '核心底薪'),
('HOUSING_ALLOW', '住房补贴', 1, 'INC_ALLOWANCE', 'housingAllow', 11, 1, 0, 1, 'zfbt', 11, '每月固定房补'),
('MEAL_ALLOW', '餐补', 1, 'INC_ALLOWANCE', 'mealAllow', 12, 0, 0, 1, 'cb', 12, '固定餐补'),
('SHIFT_12H_ALLOWANCE', '12小时补贴', 1, 'INC_ALLOWANCE', 'shift12hAllowance', 13, 1, 0, 1, '12xsbt', 13,
 '特殊排班补贴'),
('QUARANTINE_ALLOWANCE', '隔离补贴', 1, 'INC_SUBSIDY', 'quarantineAllowance', 14, 1, 0, 0, 'glbt', 14, '特殊隔离补贴'),
('OTHER_ALLOWANCE', '其他补贴', 1, 'INC_ALLOWANCE', 'otherAllowance', 19, 1, 0, 0, 'qtbt', 19, '非固定通用补贴'),

-- ----------------------------------------------------------
-- 【2】收入类 - 动态变动项 (Attendance & Performance)
-- ----------------------------------------------------------
('OVERTIME_PAY_DAY', '日加班工资', 1, 'INC_OVERTIME', 'overtimePayDay', 20, 1, 0, 0, 'rjbgz', 20, '按天加班费'),
('OVERTIME_PAY_HOUR', '时加班工资', 1, 'INC_OVERTIME', 'overtimePayHour', 21, 1, 0, 0, 'sjbgz', 21, '按时加班费'),
('KPI_BONUS', 'KPI绩效', 1, 'INC_BONUS', 'kpiBonus', 30, 1, 0, 0, 'kpi', 30, '月度绩效'),
('COMMISSION_SALES', '业绩提成', 1, 'INC_BONUS', 'commissionSales', 31, 1, 0, 0, 'yjtc', 31, '业务提成'),
('COMMISSION_AGENT', '代理提成', 1, 'INC_BONUS', 'commissionAgent', 32, 1, 0, 0, 'dltc', 32, '代理提成'),
('ATTENDANCE_BONUS', '全勤奖', 1, 'INC_ATTENDANCE', 'attendanceBonus', 40, 1, 0, 0, 'qqj', 40, '全勤奖金'),
('ATTENDANCE_REISSUE', '考勤/薪资补发', 1, 'INC_ATTENDANCE', 'attendanceReissue', 41, 1, 0, 0, 'kqbf', 41,
 '漏打卡或考勤误差补发'),

-- ----------------------------------------------------------
-- 【3】各类奖励与节日福利 (Bonus & Festival)
-- ----------------------------------------------------------
('SAFETY_CARD_BONUS', '安全卡奖励', 1, 'INC_OTHER', 'safetyCardBonus', 50, 1, 0, 0, 'aqkjl', 50, '安全奖励'),
('ANNUAL_LEAVE_BONUS', '年假奖金', 1, 'INC_OTHER', 'annualLeaveBonus', 51, 1, 0, 0, 'njjj', 51, '年假折现'),
('REFERRAL_BONUS', '内推奖金', 1, 'INC_OTHER', 'referralBonus', 52, 1, 0, 0, 'ntjj', 52, '内推奖励'),
('BIRTHDAY_BONUS', '生日礼金', 1, 'INC_FESTIVAL', 'birthdayBonus', 53, 0, 0, 0, 'srlj', 53, '生日福利'),

-- 节日现金/实物
('FESTIVAL_SPRING_GIFT', '春节福利', 1, 'INC_FESTIVAL', 'festivalSpringGift', 60, 1, 0, 0, 'cjfw', 60, '实物'),
('FESTIVAL_SPRING_BONUS', '春节礼金', 1, 'INC_FESTIVAL', 'festivalSpringBonus', 61, 1, 0, 0, 'cjlj', 61, '现金'),
('FESTIVAL_DRAGON_BOAT_GIFT', '端午节福利', 1, 'INC_FESTIVAL', 'festivalDragonBoatGift', 62, 1, 0, 0, 'dwfw', 62,
 '实物'),
('FESTIVAL_DRAGON_BOAT_BONUS', '端午节礼金', 1, 'INC_FESTIVAL', 'festivalDragonBoatBonus', 63, 1, 0, 0, 'dwlj', 63,
 '现金'),
('FESTIVAL_MID_AUTUMN_GIFT', '中秋节福利', 1, 'INC_FESTIVAL', 'festivalMidAutumnGift', 64, 1, 0, 0, 'zqfw', 64, '实物'),
('FESTIVAL_MID_AUTUMN_BONUS', '中秋节礼金', 1, 'INC_FESTIVAL', 'festivalMidAutumnBonus', 65, 1, 0, 0, 'zqlj', 65,
 '现金'),

-- 赛事激励
('EVENT_EURO_CUP', '欧洲杯激励奖金', 1, 'INC_BONUS', 'eventEuroCup', 70, 1, 0, 0, 'ozb', 70, '欧洲杯奖金'),
('EVENT_WORLD_CUP', '世界杯激励奖金', 1, 'INC_BONUS', 'eventWorldCup', 71, 1, 0, 0, 'sjb', 71, '世界杯奖金'),

-- 年终奖系列 (统一移除小数点，使用小驼峰)
('ANNUAL_BONUS_13', '年终奖13薪', 1, 'INC_YEAR_END', 'annualBonus13', 77, 1, 0, 0, 'nzj13', 77, '13薪'),
('ANNUAL_BONUS_13_5', '年终奖13.5薪', 1, 'INC_YEAR_END', 'annualBonus135', 78, 1, 0, 0, 'nzj135', 78, '13.5薪'),
('ANNUAL_BONUS_14', '年终奖14薪', 1, 'INC_YEAR_END', 'annualBonus14', 79, 1, 0, 0, 'nzj14', 79, '14薪'),
('ANNUAL_BONUS_14_5', '年终奖14.5薪', 1, 'INC_YEAR_END', 'annualBonus14_5', 80, 1, 0, 0, 'nzj14.5', 80, '14.5薪'),
('ANNUAL_BONUS_15', '年终奖15薪', 1, 'INC_YEAR_END', 'annualBonus15', 81, 1, 0, 0, 'nzj15', 81, '15薪'),
('ANNUAL_BONUS_15_5', '年终奖15.5薪', 1, 'INC_YEAR_END', 'annualBonus15_5', 82, 1, 0, 0, 'nzj15.5', 82, '15.5薪'),
('ANNUAL_BONUS_16', '年终奖16薪', 1, 'INC_YEAR_END', 'annualBonus16', 83, 1, 0, 0, 'nzj16', 83, '16薪'),
('ANNUAL_BONUS_16_5', '年终奖16.5薪', 1, 'INC_YEAR_END', 'annualBonus16_5', 84, 1, 0, 0, 'nzj16.5', 84, '16.5薪'),
('ANNUAL_BONUS_17', '年终奖17薪', 1, 'INC_YEAR_END', 'annualBonus17', 85, 1, 0, 0, 'nzj17', 85, '17薪'),
('ANNUAL_BONUS_17_5', '年终奖17.5薪', 1, 'INC_YEAR_END', 'annualBonus17_5', 86, 1, 0, 0, 'nzj17.5', 86, '17.5薪'),
('ANNUAL_BONUS_18', '年终奖18薪', 1, 'INC_YEAR_END', 'annualBonus18', 87, 1, 0, 0, 'nzj18', 87, '18薪'),
('ANNUAL_BONUS_18_5', '年终奖18.5薪', 1, 'INC_YEAR_END', 'annualBonus185', 88, 1, 0, 0, 'nzj185', 88, '18.5薪'),
('ANNUAL_BONUS_19', '年终奖19薪', 1, 'INC_YEAR_END', 'annualBonus19', 89, 1, 0, 0, 'nzj19', 89, '19薪'),

-- 忠诚奖
('LOYALTY_BONUS_2Y', '忠诚奖金(二年度)', 1, 'INC_BONUS', 'loyaltyBonus2y', 90, 1, 0, 0, 'zcj2', 90, '满2年'),
('LOYALTY_BONUS_5Y', '忠诚奖金(五年度)', 1, 'INC_BONUS', 'loyaltyBonus5y', 91, 1, 0, 0, 'zcj5', 91, '满5年'),
('LOYALTY_BONUS_10Y', '忠诚奖金(十年度)', 1, 'INC_BONUS', 'loyaltyBonus10y', 92, 1, 0, 0, 'zcj10', 92, '满10年'),

-- ----------------------------------------------------------
-- 【4】返还/报销项 (Rebate/Reimbursement - 对应扣款)
-- ----------------------------------------------------------
('EXPENSE_REIMBURSE_ONBOARD', '入职费用报销', 1, 'INC_OTHER', 'expenseReimburseOnboard', 95, 0, 0, 0, 'rzbx', 95,
 '免税报销'),
('DEPOSIT_REFUND_CURRENT', '本月押金返还', 1, 'INC_OTHER', 'depositRefundCurrent', 96, 0, 0, 0, 'yjfh', 96,
 '对应押金扣除'),
('FINE_REBATE', '管理罚款返还', 1, 'INC_OTHER', 'fineRebate', 97, 0, 0, 0, 'fkfh', 97, '罚款申诉退回'),
('UTILITY_REBATE', '水电网费返还', 1, 'INC_OTHER', 'utilityRebate', 98, 0, 0, 0, 'sdwfh', 98, '水电费多扣返还'),
('PASSPORT_FEE_REBATE', '护照费用返还', 1, 'INC_OTHER', 'passportFeeRebate', 99, 0, 0, 0, 'hzfh', 99, '护照费多扣返还'),

-- ----------------------------------------------------------
-- 【5】扣款类 (Deductions)
-- ----------------------------------------------------------
('ABSENT_DEDUCTION', '缺勤扣款', 2, 'DED_ABSENT', 'absentDeduction', 100, 0, 1, 0, 'qqkk', 100, '税前扣'),
('LATE_DEDUCTION', '迟到早退扣款', 2, 'DED_LATE', 'lateDeduction', 110, 0, 1, 0, 'cdzt', 110, '税前扣'),
('UTILITY_DEDUCTION', '水电网扣款', 2, 'DED_OTHER', 'utilityDeduction', 120, 0, 0, 0, 'sdwkk', 120, '税后扣'),
('FINE_DEDUCTION', '管理罚款', 2, 'DED_FINE', 'fineDeduction', 121, 0, 0, 0, 'glfk', 121, '税后扣'),
('PASSPORT_FEE_DEDUCTION', '护照费用代扣', 2, 'DED_OTHER', 'passportFeeDeduction', 122, 0, 0, 0, 'hzdk', 122, '护照费'),
('DEPOSIT_DEDUCTION_CURRENT', '本月押金扣除', 2, 'DED_OTHER', 'depositDeductionCurrent', 123, 0, 0, 0, 'byyj', 123,
 '押金扣'),
('OTHER_DEDUCTION', '其他扣除', 2, 'DED_OTHER', 'otherDeduction', 124, 0, 0, 0, 'qtkc', 130, '通用非固定扣款'),
-- ----------------------------------------------------------
-- 【6】系统调整与结算
-- ----------------------------------------------------------
('RESIGNATION_SETTLEMENT', '离职费用结算', 2, 'DED_OTHER', 'resignationSettlement', 140, 0, 0, 0, 'lzjs', 140,
 '离职清算扣款'),
('PREV_MONTH_ADJUSTMENT', '上月补发/续扣', 1, 'INC_OTHER', 'prevMonthAdjustment', 141, 1, 0, 0, 'sybf', 141,
 '人工调账'),

-- ----------------------------------------------------------
-- 【7】税费与社保 (Personal SI & Tax)
-- ----------------------------------------------------------
('SI_PENSION_IND', '养老保险(个人)', 3, 'SI_PENSION', 'siPensionInd', 200, 0, 1, 1, 'ylbx', 200, '个人养老'),
('SI_MED_IND', '医疗保险(个人)', 3, 'SI_MED', 'siMedInd', 210, 0, 1, 1, 'ylbx', 210, '个人医疗'),
('SI_HOUSING_IND', '公积金(个人)', 3, 'SI_HOUSING', 'siHousingInd', 220, 0, 1, 1, 'gjj', 220, '个人公积金'),
('SI_REISSUE_IND', '个人社保退费/补发', 1, 'INC_OTHER', 'siReissueInd', 230, 0, 0, 0, 'sbgjjbf', 230, '社保多扣返还'),
('AUTO_TAX_CALC', '智能个税核算', 3, 'TAX_INCOME', 'autoTaxCalc', 999, 0, 0, 0, 'zngs', 999, '个税终结节点'),

-- ----------------------------------------------------------
-- 【8】公司成本 (Employer Cost - 不进个人工资条实发)
-- ----------------------------------------------------------
('ER_PENSION_COMP', '养老保险(公司)', 4, 'ER_PENSION', 'erPensionComp', 300, 0, 0, 1, 'ylbx', 300, '公司成本'),
('ER_VISA_COMP', '海外签证费用', 4, 'ER_VISA', 'erVisaComp', 310, 0, 0, 0, 'qzfy', 310, '公司承担签证');


INSERT INTO salary_calc_pipeline_info
(id, pipeline_code, pipeline_name, version, default_flag, status, remark, delete_flag, create_by, create_time,
 update_by, update_time)
VALUES (1, 'OFFICIAL_STAFF_2026', '2026年度正式员工核算流', 1, 0, 1, '本管道适用于集团 2026 年度全体正式员工月度核算。
制度依据：遵循 2026 版薪酬管理办法，包含基本工资、五险一金及各项绩效奖金。
逻辑特性：计算顺序严格遵循 [基础->补贴->扣款->税->汇总] 阶段，已同步 2026 年最新公积金缴存基数上限。
维护人：HR-薪酬组 / 技术支撑部', 0, 'system', '2026-04-02 14:11:34', 'system', '2026-04-02 14:11:34');

-- =================================================================================
-- 10. 初始化 薪资计算规则表 salary_calc_rule (全量59项大满贯版)
-- 标准：全部采用 env_var_name (小驼峰) + decimal() 强制防精度丢失 + nil 空值防御
-- =================================================================================
TRUNCATE TABLE `salary_calc_rule`;

INSERT INTO `salary_calc_rule`
(`rule_code`, `rule_name`, `rule_type`, `rule_script`, `return_type`, `sort_value`, `status`, `remark`)
VALUES
-- ----------------------------------------------------------
-- 【1】基础与考勤绩效 (Base & Perf)
-- ----------------------------------------------------------
('BASE_SALARY', '基本工资', 1,
 'let base = (baseSalary == nil) ? 0.0M : decimal(baseSalary); monthDays > 0M ? (base / monthDays * attendanceDays) : 0.0M',
 'Decimal', 10, 1, '底薪折算'),
('HOUSING_ALLOW', '住房补贴', 1,
 'let allow = (housingAllow == nil) ? 0.0M : decimal(housingAllow); monthDays > 0M ? (allow / monthDays * attendanceDays) : 0.0M',
 'Decimal', 11, 1, '房补折算'),
('MEAL_ALLOW', '餐补', 1,
 'let allow = (mealAllow == nil) ? 0.0M : decimal(mealAllow); monthDays > 0M ? (allow / monthDays * attendanceDays) : 0.0M',
 'Decimal', 12, 1, '餐补折算'),
('SHIFT_12H_ALLOWANCE', '12小时补贴', 1, 'shift12hAllowance == nil ? 0.0M : decimal(shift12hAllowance)', 'Decimal', 13,
 1, '排班补贴'),
('QUARANTINE_ALLOWANCE', '隔离补贴', 1, 'quarantineAllowance == nil ? 0.0M : decimal(quarantineAllowance)', 'Decimal',
 14, 1, '隔离补贴'),
('OTHER_ALLOWANCE', '其他补贴', 1, 'otherAllowance == nil ? 0.0M : decimal(otherAllowance)', 'Decimal', 19, 1,
 '其他非固定补贴'),

('OVERTIME_PAY_DAY', '日加班工资', 1, 'overtimePayDay == nil ? 0.0M : decimal(overtimePayDay)', 'Decimal', 20, 1,
 '按天加班'),
('OVERTIME_PAY_HOUR', '时加班工资', 1, 'overtimePayHour == nil ? 0.0M : decimal(overtimePayHour)', 'Decimal', 21, 1,
 '按时加班'),
('KPI_BONUS', 'KPI绩效', 1,
 'let base = (baseSalary == nil) ? 0.0M : decimal(baseSalary); let coeff = (kpiCoefficient == nil) ? 0.0M : decimal(kpiCoefficient); base * coeff',
 'Decimal', 30, 1, '绩效系数'),
('COMMISSION_SALES', '业绩提成', 1, 'commissionSales == nil ? 0.0M : decimal(commissionSales)', 'Decimal', 31, 1,
 '销售提成'),
('COMMISSION_AGENT', '代理提成', 1, 'commissionAgent == nil ? 0.0M : decimal(commissionAgent)', 'Decimal', 32, 1,
 '代理提成'),
('ATTENDANCE_BONUS', '全勤奖', 1,
 '(isFullAttendance == true) ? (attendanceBonus == nil ? 0.0M : decimal(attendanceBonus)) : 0.0M', 'Decimal', 40, 1,
 '满勤触发'),
('ATTENDANCE_REISSUE', '考勤/薪资补发', 1, 'attendanceReissue == nil ? 0.0M : decimal(attendanceReissue)', 'Decimal',
 41, 1, '漏打卡补发'),

-- ----------------------------------------------------------
-- 【2】福利、节日与赛事 (Bonus & Festival)
-- ----------------------------------------------------------
('SAFETY_CARD_BONUS', '安全卡奖励', 1, 'safetyCardBonus == nil ? 0.0M : decimal(safetyCardBonus)', 'Decimal', 50, 1,
 '安全奖励'),
('ANNUAL_LEAVE_BONUS', '年假奖金', 1, 'annualLeaveBonus == nil ? 0.0M : decimal(annualLeaveBonus)', 'Decimal', 51, 1,
 '年假折现'),
('REFERRAL_BONUS', '内推奖金', 1, 'referralBonus == nil ? 0.0M : decimal(referralBonus)', 'Decimal', 52, 1, '内推奖'),
('BIRTHDAY_BONUS', '生日礼金', 1, 'birthdayBonus == nil ? 0.0M : decimal(birthdayBonus)', 'Decimal', 53, 1, '生日红包'),

('FESTIVAL_SPRING_GIFT', '春节福利', 1, 'festivalSpringGift == nil ? 0.0M : decimal(festivalSpringGift)', 'Decimal', 60,
 1, '春节实物'),
('FESTIVAL_SPRING_BONUS', '春节礼金', 1, 'festivalSpringBonus == nil ? 0.0M : decimal(festivalSpringBonus)', 'Decimal',
 61, 1, '春节现金'),
('FESTIVAL_DRAGON_BOAT_GIFT', '端午节福利', 1, 'festivalDragonBoatGift == nil ? 0.0M : decimal(festivalDragonBoatGift)',
 'Decimal', 62, 1, '端午实物'),
('FESTIVAL_DRAGON_BOAT_BONUS', '端午节礼金', 1,
 'festivalDragonBoatBonus == nil ? 0.0M : decimal(festivalDragonBoatBonus)', 'Decimal', 63, 1, '端午现金'),
('FESTIVAL_MID_AUTUMN_GIFT', '中秋节福利', 1, 'festivalMidAutumnGift == nil ? 0.0M : decimal(festivalMidAutumnGift)',
 'Decimal', 64, 1, '中秋实物'),
('FESTIVAL_MID_AUTUMN_BONUS', '中秋节礼金', 1, 'festivalMidAutumnBonus == nil ? 0.0M : decimal(festivalMidAutumnBonus)',
 'Decimal', 65, 1, '中秋现金'),

('EVENT_EURO_CUP', '欧洲杯激励奖金', 1, 'eventEuroCup == nil ? 0.0M : decimal(eventEuroCup)', 'Decimal', 70, 1,
 '欧洲杯'),
('EVENT_WORLD_CUP', '世界杯激励奖金', 1, 'eventWorldCup == nil ? 0.0M : decimal(eventWorldCup)', 'Decimal', 71, 1,
 '世界杯'),

-- ----------------------------------------------------------
-- 【3】年终奖与忠诚奖 (Year End & Loyalty)
-- ----------------------------------------------------------
('ANNUAL_BONUS_13', '年终奖13薪', 1, 'annualBonus13 == nil ? 0.0M : decimal(annualBonus13)', 'Decimal', 79, 1, '13薪'),
('ANNUAL_BONUS_13_5', '年终奖13.5薪', 1, 'annualBonus135 == nil ? 0.0M : decimal(annualBonus135)', 'Decimal', 80, 1,
 '13.5薪'),
('ANNUAL_BONUS_14', '年终奖14薪', 1, 'annualBonus14 == nil ? 0.0M : decimal(annualBonus14)', 'Decimal', 81, 1, '14薪'),
('ANNUAL_BONUS_14_5', '年终奖14.5薪', 1, 'annualBonus145 == nil ? 0.0M : decimal(annualBonus145)', 'Decimal', 82, 1,
 '14.5薪'),
('ANNUAL_BONUS_15', '年终奖15薪', 1, 'annualBonus15 == nil ? 0.0M : decimal(annualBonus15)', 'Decimal', 83, 1, '15薪'),
('ANNUAL_BONUS_15_5', '年终奖15.5薪', 1, 'annualBonus155 == nil ? 0.0M : decimal(annualBonus155)', 'Decimal', 84, 1,
 '15.5薪'),
('ANNUAL_BONUS_16', '年终奖16薪', 1, 'annualBonus16 == nil ? 0.0M : decimal(annualBonus16)', 'Decimal', 85, 1, '16薪'),
('ANNUAL_BONUS_16_5', '年终奖16.5薪', 1, 'annualBonus165 == nil ? 0.0M : decimal(annualBonus165)', 'Decimal', 86, 1,
 '16.5薪'),
('ANNUAL_BONUS_17', '年终奖17薪', 1, 'annualBonus17 == nil ? 0.0M : decimal(annualBonus17)', 'Decimal', 87, 1, '17薪'),
('ANNUAL_BONUS_17_5', '年终奖17.5薪', 1, 'annualBonus175 == nil ? 0.0M : decimal(annualBonus175)', 'Decimal', 88, 1,
 '17.5薪'),
('ANNUAL_BONUS_18', '年终奖18薪', 1, 'annualBonus18 == nil ? 0.0M : decimal(annualBonus18)', 'Decimal', 89, 1, '18薪'),
('ANNUAL_BONUS_18_5', '年终奖18.5薪', 1, 'annualBonus185 == nil ? 0.0M : decimal(annualBonus185)', 'Decimal', 90, 1,
 '18.5薪'),
('ANNUAL_BONUS_19', '年终奖19薪', 1, 'annualBonus19 == nil ? 0.0M : decimal(annualBonus19)', 'Decimal', 91, 1, '19薪'),

('LOYALTY_BONUS_2Y', '忠诚奖金(二年度)', 1, 'loyaltyBonus2y == nil ? 0.0M : decimal(loyaltyBonus2y)', 'Decimal', 92, 1,
 '满2年'),
('LOYALTY_BONUS_5Y', '忠诚奖金(五年度)', 1, 'loyaltyBonus5y == nil ? 0.0M : decimal(loyaltyBonus5y)', 'Decimal', 93, 1,
 '满5年'),
('LOYALTY_BONUS_10Y', '忠诚奖金(十年度)', 1, 'loyaltyBonus10y == nil ? 0.0M : decimal(loyaltyBonus10y)', 'Decimal', 94,
 1, '满10年'),

-- ----------------------------------------------------------
-- 【4】返还/报销项 (Rebates)
-- ----------------------------------------------------------
('EXPENSE_REIMBURSE_ONBOARD', '入职费用报销', 1,
 'expenseReimburseOnboard == nil ? 0.0M : decimal(expenseReimburseOnboard)', 'Decimal', 95, 1, '免税报销'),
('DEPOSIT_REFUND_CURRENT', '本月押金返还', 1, 'depositRefundCurrent == nil ? 0.0M : decimal(depositRefundCurrent)',
 'Decimal', 96, 1, '押金返还'),
('FINE_REBATE', '管理罚款返还', 1, 'fineRebate == nil ? 0.0M : decimal(fineRebate)', 'Decimal', 97, 1, '罚款申诉返还'),
('UTILITY_REBATE', '水电网费返还', 1, 'utilityRebate == nil ? 0.0M : decimal(utilityRebate)', 'Decimal', 98, 1,
 '多扣返还'),
('PASSPORT_FEE_REBATE', '护照费用返还', 1, 'passportFeeRebate == nil ? 0.0M : decimal(passportFeeRebate)', 'Decimal',
 99, 1, '护照费返还'),

-- ----------------------------------------------------------
-- 【5】扣款与调账类 (Deductions & Adjustments)
-- ----------------------------------------------------------
('ABSENT_DEDUCTION', '缺勤扣款', 1, 'absentDeduction == nil ? 0.0M : decimal(absentDeduction)', 'Decimal', 100, 1,
 '税前扣'),
('LATE_DEDUCTION', '迟到早退扣款', 1, 'lateDeduction == nil ? 0.0M : decimal(lateDeduction)', 'Decimal', 110, 1,
 '税前扣'),
('UTILITY_DEDUCTION', '水电网扣款', 1, 'utilityDeduction == nil ? 0.0M : decimal(utilityDeduction)', 'Decimal', 120, 1,
 '税后扣'),
('FINE_DEDUCTION', '管理罚款', 1, 'fineDeduction == nil ? 0.0M : decimal(fineDeduction)', 'Decimal', 121, 1, '税后扣'),
('PASSPORT_FEE_DEDUCTION', '护照费用代扣', 1, 'passportFeeDeduction == nil ? 0.0M : decimal(passportFeeDeduction)',
 'Decimal', 122, 1, '护照代扣'),
('DEPOSIT_DEDUCTION_CURRENT', '本月押金扣除', 1,
 'depositDeductionCurrent == nil ? 0.0M : decimal(depositDeductionCurrent)', 'Decimal', 123, 1, '押金扣'),
('OTHER_DEDUCTION', '其他扣除', 1, 'otherDeduction == nil ? 0.0M : decimal(otherDeduction)', 'Decimal', 130, 1,
 '其他非固定扣款'),

('RESIGNATION_SETTLEMENT', '离职费用结算', 1, 'resignationSettlement == nil ? 0.0M : decimal(resignationSettlement)',
 'Decimal', 140, 1, '离职清算扣款'),
('PREV_MONTH_ADJUSTMENT', '上月补发/续扣', 1, 'prevMonthAdjustment == nil ? 0.0M : decimal(prevMonthAdjustment)',
 'Decimal', 141, 1, '人工回溯调账'),

-- ----------------------------------------------------------
-- 【6】社保、税费与公司成本 (SI, Tax & ER Costs)
-- ----------------------------------------------------------
('SI_PENSION_IND', '养老保险(个人)', 1, 'siPensionInd == nil ? 0.0M : decimal(siPensionInd)', 'Decimal', 200, 1,
 '个人社保'),
('SI_MED_IND', '医疗保险(个人)', 1, 'siMedInd == nil ? 0.0M : decimal(siMedInd)', 'Decimal', 210, 1, '个人社保'),
('SI_HOUSING_IND', '公积金(个人)', 1, 'siHousingInd == nil ? 0.0M : decimal(siHousingInd)', 'Decimal', 220, 1,
 '个人公积金'),
('SI_REISSUE_IND', '个人社保退费/补发', 1, 'siReissueInd == nil ? 0.0M : decimal(siReissueInd)', 'Decimal', 230, 1,
 '社保多扣补发'),

('ER_PENSION_COMP', '养老保险(公司)', 1, 'erPensionComp == nil ? 0.0M : decimal(erPensionComp)', 'Decimal', 300, 1,
 '公司成本'),
('ER_VISA_COMP', '海外签证费用', 1, 'erVisaComp == nil ? 0.0M : decimal(erVisaComp)', 'Decimal', 310, 1, '公司承担'),

('AUTO_TAX_CALC', '智能个税核算', 1, '0.0M', 'Decimal', 999, 1, '触发Java内置计税引擎');

TRUNCATE TABLE `salary_calc_pipeline_step`;

TRUNCATE TABLE `salary_calc_pipeline_step`;

INSERT INTO `salary_calc_pipeline_step`
(`pipeline_code`, `pipeline_version`, `rule_code`, `rule_name`, `rule_type`, `condition_script`, `stage`, `sort_order`,
 `block_flag`, `skip_if_null`, `status`)
VALUES
-- ----------------------------------------------------------
-- 【Stage 1: 基础与绩效计算】 权重 10-99
-- ----------------------------------------------------------
('OFFICIAL_STAFF_2026', 1, 'BASE_SALARY', '基本工资', 1, NULL, 1, 10, 1, 0, 1),
('OFFICIAL_STAFF_2026', 1, 'ATTENDANCE_BONUS', '全勤奖', 1, NULL, 1, 20, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'OVERTIME_PAY_DAY', '日加班工资', 1, NULL, 1, 30, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'OVERTIME_PAY_HOUR', '时加班工资', 1, NULL, 1, 40, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'KPI_BONUS', 'KPI绩效', 1, NULL, 1, 50, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'COMMISSION_SALES', '业绩提成', 1, NULL, 1, 60, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'COMMISSION_AGENT', '代理提成', 1, NULL, 1, 70, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ATTENDANCE_REISSUE', '考勤/薪资补发', 1, NULL, 1, 80, 0, 1, 1),

-- ----------------------------------------------------------
-- 【Stage 2: 补贴与各项奖励】 权重 100-399
-- ----------------------------------------------------------
('OFFICIAL_STAFF_2026', 1, 'HOUSING_ALLOW', '住房补贴', 1, NULL, 2, 100, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'MEAL_ALLOW', '餐补', 1, NULL, 2, 110, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'SHIFT_12H_ALLOWANCE', '12小时补贴', 1, NULL, 2, 120, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'QUARANTINE_ALLOWANCE', '隔离补贴', 1, NULL, 2, 130, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'OTHER_ALLOWANCE', '其他补贴', 1, NULL, 2, 135, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'SAFETY_CARD_BONUS', '安全卡奖励', 1, NULL, 2, 140, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_LEAVE_BONUS', '年假奖金', 1, NULL, 2, 150, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'REFERRAL_BONUS', '内推奖金', 1, NULL, 2, 160, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'BIRTHDAY_BONUS', '生日礼金', 1, NULL, 2, 170, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'FESTIVAL_SPRING_GIFT', '春节福利', 1, NULL, 2, 180, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FESTIVAL_SPRING_BONUS', '春节礼金', 1, NULL, 2, 190, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FESTIVAL_DRAGON_BOAT_GIFT', '端午节福利', 1, NULL, 2, 200, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FESTIVAL_DRAGON_BOAT_BONUS', '端午节礼金', 1, NULL, 2, 210, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FESTIVAL_MID_AUTUMN_GIFT', '中秋节福利', 1, NULL, 2, 220, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FESTIVAL_MID_AUTUMN_BONUS', '中秋节礼金', 1, NULL, 2, 230, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'EVENT_EURO_CUP', '欧洲杯激励奖金', 1, NULL, 2, 240, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'EVENT_WORLD_CUP', '世界杯激励奖金', 1, NULL, 2, 250, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_13', '年终奖13薪', 1, NULL, 2, 300, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_13_5', '年终奖13.5薪', 1, NULL, 2, 301, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_14', '年终奖14薪', 1, NULL, 2, 302, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_14_5', '年终奖14.5薪', 1, NULL, 2, 303, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_15', '年终奖15薪', 1, NULL, 2, 304, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_15_5', '年终奖15.5薪', 1, NULL, 2, 305, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_16', '年终奖16薪', 1, NULL, 2, 306, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_16_5', '年终奖16.5薪', 1, NULL, 2, 307, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_17', '年终奖17薪', 1, NULL, 2, 308, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_17_5', '年终奖17.5薪', 1, NULL, 2, 309, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_18', '年终奖18薪', 1, NULL, 2, 310, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_18_5', '年终奖18.5薪', 1, NULL, 2, 311, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_19', '年终奖19薪', 1, NULL, 2, 312, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'LOYALTY_BONUS_2Y', '忠诚奖金(二年度)', 1, NULL, 2, 320, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'LOYALTY_BONUS_5Y', '忠诚奖金(五年度)', 1, NULL, 2, 330, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'LOYALTY_BONUS_10Y', '忠诚奖金(十年度)', 1, NULL, 2, 340, 0, 1, 1),

-- ----------------------------------------------------------
-- 【Stage 3: 考勤扣减与法务代扣】 权重 400-599
-- ----------------------------------------------------------
('OFFICIAL_STAFF_2026', 1, 'ABSENT_DEDUCTION', '缺勤扣款', 1, NULL, 3, 400, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'LATE_DEDUCTION', '迟到早退扣款', 1, NULL, 3, 410, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'UTILITY_DEDUCTION', '水电网扣款', 1, NULL, 3, 420, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FINE_DEDUCTION', '管理罚款', 1, NULL, 3, 430, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'PASSPORT_FEE_DEDUCTION', '护照费用代扣', 1, NULL, 3, 440, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'DEPOSIT_DEDUCTION_CURRENT', '本月押金扣除', 1, NULL, 3, 450, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'OTHER_DEDUCTION', '其他扣除', 1, NULL, 3, 455, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'SI_PENSION_IND', '养老保险(个人)', 1, NULL, 3, 460, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'SI_MED_IND', '医疗保险(个人)', 1, NULL, 3, 470, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'SI_HOUSING_IND', '公积金(个人)', 1, NULL, 3, 480, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ER_PENSION_COMP', '养老保险(公司)', 1, NULL, 3, 490, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ER_VISA_COMP', '海外签证费用', 1, NULL, 3, 500, 0, 1, 1),

-- ----------------------------------------------------------
-- 【Stage 5: 汇总结算与返还调账】 权重 600-899
-- ----------------------------------------------------------
('OFFICIAL_STAFF_2026', 1, 'EXPENSE_REIMBURSE_ONBOARD', '入职费用报销', 1, NULL, 5, 600, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'DEPOSIT_REFUND_CURRENT', '本月押金返还', 1, NULL, 5, 610, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FINE_REBATE', '管理罚款返还', 1, NULL, 5, 620, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'UTILITY_REBATE', '水电网费返还', 1, NULL, 5, 630, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'PASSPORT_FEE_REBATE', '护照费用返还', 1, NULL, 5, 640, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'SI_REISSUE_IND', '个人社保退费/补发', 1, NULL, 5, 650, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'PREV_MONTH_ADJUSTMENT', '上月补发/续扣', 1, NULL, 5, 660, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'RESIGNATION_SETTLEMENT', '离职费用结算', 1, NULL, 5, 670, 0, 1, 1),

-- ----------------------------------------------------------
-- 【Stage 4: 个税核心 (最终节点)】 权重 999
-- ----------------------------------------------------------
('OFFICIAL_STAFF_2026', 1, 'AUTO_TAX_CALC', '智能个税核算', 1, NULL, 4, 999, 1, 0, 1);


-- 分配给超级管理员 (SUPER_ADMIN ID: 1)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
SELECT 1, id, 'system'
FROM `sys_menu`
WHERE id BETWEEN 130 AND 135;

-- 分配给普通管理员 (ADMIN ID: 2, 剔除删除权限)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
SELECT 2, id, 'system'
FROM `sys_menu`
WHERE id IN (130, 131, 132, 133, 135);
