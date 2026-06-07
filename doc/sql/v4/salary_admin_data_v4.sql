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

(130,'字典管理','sys_dict','dict','system/dict/DictPage','','Collection','sys:dict:list',2,10,4,1,1),
(131,'新增字典类型','sys_dict_type_add','','','','','sys:dict_type:add',3,130,1,1,1),
(132,'删除字典类型','sys_dict_type_del','','','','','sys:dict_type:del',3,130,2,1,1),
(133,'修改字典类型','sys_dict_type_edit','','','','','sys:dict_type:edit',3,130,3,1,1),
(134,'查询字典类型','sys_dict_type_query','','','','','sys:dict_type:query',3,130,4,1,1),
(135,'新增字段项','sys_dict_item_add','','','','','sys:dict_item:add',3,130,5,1,1),
(136,'删除字典项','sys_dict_item_del','','','','','sys:dict_item:del',3,130,6,1,1),
(137,'修改字典项','sys_dict_item_edit','','','','','sys:dict_item:edit',3,130,7,1,1),
(138,'查询字典类型','sys_dict_item_query','','','','','sys:dict_item:query',3,130,8,1,1),
(139, '字典刷新缓存', 'sys_dict_refresh', '', '', '', '', 'sys:dict:refresh', 3, 130, 5, 1, 1),
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
(231, '查询管道编排', 'salary_pipeline_query', '', '', '', '', 'salary:pipeline:query', 3, 230, 1, 1, 1),
(232, '新建管道编排', 'salary_pipeline_add', '', '', '', '', 'salary:pipeline:add', 3, 230, 2, 1, 1),
(233, '修改管道编排', 'salary_pipeline_edit', '', '', '', '', 'salary:pipeline:edit', 3, 230, 3, 1, 1),
(234, '删除管道编排', 'salary_pipeline_del', '', '', '', '', 'salary:pipeline:del', 3, 230, 4, 1, 1),
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
(251, '初始化本月账套', 'salary_summary_init', '', '', '', '', 'salary:summary:init', 3, 250, 1, 1, 1),
(252, '查看汇总列表', 'salary_summary_query', '', '', '', '', 'salary:summary:query', 3, 250, 2, 1, 1),
(253, '查看工资条明细', 'salary_summary_detail', '', '', '', '', 'salary:summary:detail', 3, 250, 3, 1, 1),
(254, '锁定与解锁单据', 'salary_summary_lock', '', '', '', '', 'salary:summary:lock', 3, 250, 4, 1, 1),
(255, '执行引擎核算', 'salary_summary_calc', '', '', '', '', 'salary:summary:calc', 3, 250, 5, 1, 1),
(256, '手工发放总金额', 'salary_summary_adjust', '', '', '', '', 'salary:summary:adjust', 3, 250, 6, 1, 1);
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
('salary_audit_status', '薪资档案审批状态', 'salary', '员工定薪/调薪申请的审批流转状态', 'system'),
('salary_tax_rule', '个税核算规则', 'salary', '定义员工发薪时适用的个人所得税计算标准及计税分支', 'system'),
('salary_calc_stage', '薪资核算阶段', 'salary', '定义薪资瀑布流引擎执行的物理次序阶段', 'system'),
('salary_item_category', '薪资项目大类', 'salary', '定义薪资项的物理分类：收入、扣款、税费等', 'system'),
('salary_item_sub_type', '薪资项目细类', 'salary', '定义具体的业务逻辑标识，用于代码或脚本识别', 'system'),
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
-- ==========================================
-- 1. 支付打款渠道
-- ==========================================
('salary_audit_status', '0', '待审批', 10, 'system'),
('salary_audit_status', '1', '已生效', 20, 'system'),
('salary_audit_status', '2', '已驳回', 30, 'system'),

-- ==========================================================
-- 2.个税核算规则明细项 (严格对应 Aviator 脚本的分支逻辑)
-- ==========================================================
 ('salary_tax_rule', 'TAX_RESIDENT_CN', '中国居民综合所得税', 10, 'system'),
 ('salary_tax_rule', 'TAX_LABOR', '劳务报酬所得税', 20, 'system'),
 ('salary_tax_rule', 'NO_TAX', '不计税(外包/免税)', 30, 'system'),
-- ==========================================
-- 3. 薪资核算阶段
-- ==========================================
('salary_calc_stage', '1', '基础薪资阶段', 10, 'system'),
('salary_calc_stage', '2', '津贴与奖金阶段', 20, 'system'),
('salary_calc_stage', '3', '扣款与社保阶段', 30, 'system'),
('salary_calc_stage', '4', '税务核算阶段', 40, 'system'),
('salary_calc_stage', '5', '最终汇总阶段', 50, 'system'),
-- ==========================================
-- ==========================================
-- 4. 薪资项目大类
-- ==========================================
('salary_item_category', '1', '收入', 10, 'system'),
('salary_item_category', '2', '扣款', 20, 'system'),
('salary_item_category', '3', '税费', 30, 'system'),
('salary_item_category', '4', '公司支出', 40, 'system'),
-- ==========================================
-- 5. 薪资项目细类
-- ==========================================
-- 收入类细分
('salary_item_sub_type', 'INC_BASE', '基本工资', 10, 'system'),
('salary_item_sub_type', 'INC_ALLOWANCE', '岗位津贴', 20, 'system'),
('salary_item_sub_type', 'INC_OVERTIME', '加班工资', 30, 'system'),
('salary_item_sub_type', 'INC_ATTENDANCE', '全勤奖', 35, 'system'),
('salary_item_sub_type', 'INC_BONUS', '绩效奖金', 40, 'system'),
('salary_item_sub_type', 'INC_YEAR_END', '年终奖', 50, 'system'),
('salary_item_sub_type', 'INC_SUBSIDY', '补贴', 60, 'system'),
('salary_item_sub_type', 'INC_FESTIVAL', '节日礼金', 70, 'system'),
('salary_item_sub_type', 'INC_OTHER', '其他收入', 80, 'system'),
-- 扣款类细分
('salary_item_sub_type', 'DED_ABSENT', '缺勤扣款', 100, 'system'),
('salary_item_sub_type', 'DED_LATE', '迟到早退', 110, 'system'),
('salary_item_sub_type', 'DED_FINE', '罚款', 115, 'system'),
('salary_item_sub_type', 'DED_LOAN', '借款扣还', 118, 'system'),
('salary_item_sub_type', 'DED_OTHER', '其他扣款', 120, 'system'),
-- 统筹与税费 (涉及 PHP 多币种计算的关键项)
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
-- 6. 支付打款渠道
-- ==========================================
('payment_channel', 'bank_transfer_cmb', '招商银行企业代发', 10, 'system'),
('payment_channel', 'bank_transfer_icbc', '工商银行企业代发', 20, 'system'),
('payment_channel', 'bank_transfer_bdo', 'BDO Unibank', 30, 'system'),
('payment_channel', 'wallet_gcash', 'GCash 企业转账', 40, 'system'),
('payment_channel', 'alipay_batch', '支付宝批量代发', 50, 'system'),
('payment_channel', 'overseas_swift', '跨境电汇(SWIFT)', 60, 'system'),
-- ==========================================
-- 7. 结算本位币种
-- ==========================================
('settlement_currency', 'CNY', '人民币 (CNY)', 10, 'system'),
('settlement_currency', 'PHP', '菲律宾比索 (PHP)', 20, 'system'),
('settlement_currency', 'USDT', '泰达币(USDT)', 30, 'system'),
('settlement_currency', 'USD', '美元 (USD)', 40, 'system'),

-- ==========================================
-- 8. 员工在职状态
-- ==========================================
('employment_status', '1', '正式员工', 10, 'system'),
('employment_status', '2', '试用期员工', 20, 'system'),
('employment_status', '3', '实习生', 30, 'system'),
('employment_status', '4', '兼职/外包', 40, 'system'),
('employment_status', '0', '已离职', 50, 'system');