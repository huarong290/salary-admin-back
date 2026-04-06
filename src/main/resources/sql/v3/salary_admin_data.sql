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
(131, '字典查询', 'sys_dict_query', '', '', '', '', 'sys:dict:query', 3, 130, 1, 1, 1),
(132, '字典新增', 'sys_dict_add', '', '', '', '', 'sys:dict:add', 3, 130, 2, 1, 1),
(133, '字典修改', 'sys_dict_edit', '', '', '', '', 'sys:dict:edit', 3, 130, 3, 1, 1),
(134, '字典删除', 'sys_dict_del', '', '', '', '', 'sys:dict:del', 3, 130, 4, 1, 1),
(135, '字典刷新缓存', 'sys_dict_refresh', '', '', '', '', 'sys:dict:refresh', 3, 130, 5, 1, 1),

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
-- 1. 薪资收入大类 (对应 item_category = 1)
-- ==========================================
('salary_income_type', 'base_pay', '固定工资', 10, 'system'),
('salary_income_type', 'performance_bonus', '绩效奖金', 20, 'system'),
('salary_income_type', 'allowance_subsidy', '津贴福利', 30, 'system'),
('salary_income_type', 'attendance_income', '考勤相关', 40, 'system'),
('salary_income_type', 'annual_bonus', '年终奖金类', 50, 'system'),
('salary_income_type', 'special_award', '专项奖金', 60, 'system'),
('salary_income_type', 'salary_adjustment', '薪资调整', 70, 'system'),

-- ==========================================
-- 2. 薪资扣款大类 (对应 item_category = 2)
-- ==========================================
('salary_deduction_type', 'attendance_deduct', '考勤相关', 10, 'system'),
('salary_deduction_type', 'administrative_penalty', '行政罚款及押金', 20, 'system'),
('salary_deduction_type', 'other_deduct', '其他代扣款项', 30, 'system'),

-- ==========================================
-- 3. 税费与社保大类 (对应 item_category = 3)
-- ==========================================
('salary_tax_social_type', 'social_security_personal', '社保代扣(个人部分)', 10, 'system'),
('salary_tax_social_type', 'provident_fund_personal', '公积金代扣(个人部分)', 20, 'system'),
('salary_tax_social_type', 'individual_income_tax', '个人所得税(含预扣预缴)', 30, 'system'),

-- ==========================================
-- 4. 公司统筹支出大类 (对应 item_category = 4)
-- ==========================================
('salary_company_expense_type', 'social_security_company', '社保统筹(公司部分)', 10, 'system'),
('salary_company_expense_type', 'provident_fund_company', '公积金统筹(公司部分)', 20, 'system'),
('salary_company_expense_type', 'commercial_insurance', '商业补充险(公司承担)', 30, 'system'),

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


-- 字典管理目录 (挂在系统管理下)
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`,
                        `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`)
VALUES (130, '字典管理', 'sys_dict', 'dict', 'system/dict/DictPage', '', 'Collection', 'sys:dict:list', 2, 10, 4, 1, 1),
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
SELECT 1, id, 'system'
FROM `sys_menu`
WHERE id BETWEEN 130 AND 135;

-- 分配给普通管理员 (ADMIN ID: 2, 剔除删除权限)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
SELECT 2, id, 'system'
FROM `sys_menu`
WHERE id IN (130, 131, 132, 133, 135);



-- ==========================================================
-- 9. 初始化 系统字典明细表 salary_item_config
-- ==========================================================
TRUNCATE TABLE `salary_item_config`;

INSERT INTO `salary_item_config`
(`item_code`, `item_name`, `item_category`, `category_dict_value`, `env_var_name`, `calc_priority`, `taxable_flag`, `tax_deductible_flag`, `fixed_flag`, `pinyin_code`, `sort_value`, `remark`)
VALUES
-- ----------------------------------------------------------
-- 【1】收入类 - 档案固定项 (Fixed = 1，这些配置给员工定薪时使用)
-- ----------------------------------------------------------
('BASE_SALARY', '基本工资', 1, 'INC_BASE', 'baseSalary', 10, 1, 0, 1, 'jbgz', 10, '员工档案中的核心基础底薪，计税基准'),
('HOUSING_ALLOW', '住房补贴', 1, 'INC_ALLOWANCE', 'housingAllow', 20, 1, 0, 1, 'zfbt', 11, '随职级固定的每月房屋补贴，合并计税'),
('MEAL_ALLOW', '餐补', 1, 'INC_ALLOWANCE', 'mealAllow', 30, 0, 0, 1, 'cb', 12, '每月固定餐补，依据合规策略设为不计税'),
('SHIFT_12H_ALLOWANCE', '12小时补贴', 1, 'INC_ALLOWANCE', 'shift12hAllowance', 12, 1, 0, 1, '12xsbt', 13, '特殊排班固定补贴'),
('QUARANTINE_ALLOWANCE', '隔离补贴', 1, 'INC_SUBSIDY', 'quarantineAllowance', 13, 1, 0, 0, 'glbt', 14, '疫情或特殊情况隔离补贴'),
-- ----------------------------------------------------------
-- 【2】收入类 - 动态变动项 (Fixed = 0，通过考勤/绩效/手工账产生)
-- ----------------------------------------------------------
('OVERTIME_PAY_DAY', '日加班工资', 1, 'INC_OVERTIME', 'overtimePayDay', 20, 1, 0, 0, 'rjbgz', 20, '按天折算的加班费'),
('OVERTIME_PAY_HOUR', '时加班工资', 1, 'INC_OVERTIME', 'overtimePayHour', 21, 1, 0, 0, 'sjbgz', 21, '按小时折算的加班费'),
('KPI_BONUS', 'KPI绩效', 1, 'INC_BONUS', 'kpiBonus', 30, 1, 0, 0, 'kpi', 30, '月度KPI考核奖金'),
('COMMISSION_SALES', '业绩提成', 1, 'INC_BONUS', 'commissionSales', 31, 1, 0, 0, 'yjtc', 31, '业务员业绩抽成'),
('COMMISSION_AGENT', '代理提成', 1, 'INC_BONUS', 'commissionAgent', 32, 1, 0, 0, 'dltc', 32, '代理线抽成'),
('ATTENDANCE_BONUS', '全勤奖', 1, 'INC_ATTENDANCE', 'attendanceBonus', 40, 1, 0, 0, 'qqj', 40, '考勤满勤奖励'),

-- ----------------------------------------------------------
-- 【3】各类奖金/福利/报销 (Fixed = 0，通常通过手工账或特殊批次导入)
-- ----------------------------------------------------------
('SAFETY_CARD_BONUS', '安全卡奖励', 1, 'INC_OTHER', 'safetyCardBonus', 50, 1, 0, 0, 'aqkjl', 50, '安全合规奖励'),
('ANNUAL_LEAVE_BONUS', '年假奖金', 1, 'INC_OTHER', 'annualLeaveBonus', 51, 1, 0, 0, 'njjj', 51, '未休年假折现'),
('REFERRAL_BONUS', '内推奖金', 1, 'INC_OTHER', 'referralBonus', 52, 1, 0, 0, 'ntjj', 52, '推荐人才奖励'),
('BIRTHDAY_BONUS', '生日礼金', 1, 'INC_FESTIVAL', 'birthdayBonus', 53, 0, 0, 0, 'srlj', 53, '生日福利(通常避税)'),

-- 节日福利系列
('FESTIVAL_DRAGON_BOAT', '端午节福利/礼金', 1, 'INC_FESTIVAL', 'festivalDragonBoat', 60, 1, 0, 0, 'dwj', 60, '端午专项'),
('FESTIVAL_MID_AUTUMN', '中秋节福利/礼金', 1, 'INC_FESTIVAL', 'festivalMidAutumn', 61, 1, 0, 0, 'zqj', 61, '中秋专项'),

-- 赛事激励系列
('EVENT_EURO_CUP', '欧洲杯激励奖金', 1, 'INC_BONUS', 'eventEuroCup', 70, 1, 0, 0, 'ozb', 70, '欧洲杯期间业务激励'),
('EVENT_WORLD_CUP', '世界杯激励奖金', 1, 'INC_BONUS', 'eventWorldCup', 71, 1, 0, 0, 'sjb', 71, '世界杯期间业务激励'),

-- 年终奖系列 (枚举出常用倍数，方便财务做账与审计)
('ANNUAL_BONUS_13', '年终奖13薪', 1, 'INC_YEAR_END', 'annualBonus13', 80, 1, 0, 0, 'nzj13', 80, '标准13薪'),
('ANNUAL_BONUS_13_5', '年终奖13.5薪', 1, 'INC_YEAR_END', 'annualBonus13_5', 81, 1, 0, 0, 'nzj13.5', 81, '13.5薪'),
('ANNUAL_BONUS_14', '年终奖14薪', 1, 'INC_YEAR_END', 'annualBonus14', 82, 1, 0, 0, 'nzj14', 82, '14薪'),
('ANNUAL_BONUS_14_5', '年终奖14.5薪', 1, 'INC_YEAR_END', 'annualBonus14_5', 83, 1, 0, 0, 'nzj14.5', 83, '14.5薪'),
('ANNUAL_BONUS_15', '年终奖15薪', 1, 'INC_YEAR_END', 'annualBonus15', 84, 1, 0, 0, 'nzj15', 84, '15薪'),
('ANNUAL_BONUS_15_5', '年终奖15.5薪', 1, 'INC_YEAR_END', 'annualBonus15_5', 85, 1, 0, 0, 'nzj15.5', 85, '15.5薪'),
('ANNUAL_BONUS_16', '年终奖16薪', 1, 'INC_YEAR_END', 'annualBonus16', 86, 1, 0, 0, 'nzj16', 86, '16薪'),
('ANNUAL_BONUS_16_5', '年终奖16.5薪', 1, 'INC_YEAR_END', 'annualBonus16_5', 87, 1, 0, 0, 'nzj16.5', 87, '16.5薪'),
('ANNUAL_BONUS_17', '年终奖17薪', 1, 'INC_YEAR_END', 'annualBonus17', 88, 1, 0, 0, 'nzj17', 88, '17薪'),
('ANNUAL_BONUS_17_5', '年终奖17.5薪', 1, 'INC_YEAR_END', 'annualBonus17_5', 89, 1, 0, 0, 'nzj17.5', 89, '17.5薪'),
-- 忠诚奖系列 (按周期细分)

('LOYALTY_BONUS_2Y', '忠诚奖金(二年度)', 1, 'INC_BONUS', 'loyaltyBonus2y', 90, 1, 0, 0, 'zcj2', 90, '满两年忠诚奖'),
('LOYALTY_BONUS_5Y', '忠诚奖金(五年度)', 1, 'INC_BONUS', 'loyaltyBonus5y', 91, 1, 0, 0, 'zcj5', 91, '满五年忠诚奖'),
('LOYALTY_BONUS_10Y', '忠诚奖金(十年度)', 1, 'INC_BONUS', 'loyaltyBonus10y', 92, 1, 0, 0, 'zcj10', 92, '满十年忠诚奖'),
-- 报销与返还系列 (非薪金收入，免税)
('EXPENSE_REIMBURSE_ONBOARD', '新入职/回国费用报销', 1, 'INC_OTHER', 'expenseReimburseOnboard', 95, 0, 0, 0, 'bxfy', 95, '机票签注等费用报销(免税)'),
('DEPOSIT_REFUND_CURRENT', '本月押金返还', 1, 'INC_OTHER', 'depositRefundCurrent', 96, 0, 0, 0, 'yjfh', 96, '押金到期退还员工(免税)'),
-- ----------------------------------------------------------
-- 【4】扣款类 (Fixed = 0，注意税前扣除与税后扣除的区别)
-- ----------------------------------------------------------
('ABSENT_DEDUCTION', '缺勤扣款', 2, 'DED_ABSENT', 'absentDeduction', 100, 0, 1, 0, 'qqkk', 100, '【税前扣除】按旷工天数扣减底薪，降低个税基数'),
('LATE_DEDUCTION', '迟到早退扣款', 2, 'DED_LATE', 'lateDeduction', 110, 0, 1, 0, 'cdzt', 110, '【税前扣除】根据考勤自动计算扣减'),

('UTILITY_DEDUCTION', '水电扣款', 2, 'DED_OTHER', 'utilityDeduction', 120, 0, 0, 0, 'sdkk', 120, '【税后扣除】后勤手工账导入，不影响个税基数'),
('FINE_DEDUCTION', '管理罚款', 2, 'DED_FINE', 'fineDeduction', 121, 0, 0, 0, 'glfk', 121, '【税后扣除】单次违规罚款'),

-- 代扣与押金类 (税后扣除，钱暂存公司)
('PASSPORT_FEE_DEDUCTION', '护照费用代扣', 2, 'DED_OTHER', 'passportFeeDeduction', 122, 0, 0, 0, 'hzdk', 122, '【税后扣除】签证护照代办费'),
('DEPOSIT_DEDUCTION_CURRENT', '本月押金', 2, 'DED_OTHER', 'depositDeductionCurrent', 123, 0, 0, 0, 'byyj', 123, '【税后扣除】9G工签或设备押金按月扣减'),

-- ----------------------------------------------------------
-- 【5】系统兜底/调整项
-- ----------------------------------------------------------
('RESIGNATION_SETTLEMENT', '离职费用结算', 2, 'DED_OTHER', 'resignationSettlement', 140, 0, 0, 0, 'lzjs', 140, '离职时的清算专项(正负皆可)'),
('PREV_MONTH_ADJUSTMENT', '上月补发/续扣', 1, 'INC_OTHER', 'prevMonthAdjustment', 141, 1, 0, 0, 'sybf', 141, '历史遗留问题的人工调账'),
-- ----------------------------------------------------------
-- 【6】税费与社保类 (社保为固定项，个税为最高优先级的动态计算项)
-- ----------------------------------------------------------
('SI_PENSION_IND', '养老保险(个人)', 3, 'SI_PENSION', 'siPensionInd', 200, 0, 1, 1, 'ylbx', 200, '【税前扣除】法定五险一金代扣，定薪时带入'),
('SI_MED_IND', '医疗保险(个人)', 3, 'SI_MED', 'siMedInd', 210, 0, 1, 1, 'ylbx', 210, '【税前扣除】法定五险一金代扣，定薪时带入'),
('SI_HOUSING_IND', '公积金(个人)', 3, 'SI_HOUSING', 'siHousingInd', 220, 0, 1, 1, 'gjj', 220, '【税前扣除】法定五险一金代扣，定薪时带入'),
('AUTO_TAX_CALC', '智能个税核算中心', 3, 'TAX_INCOME', 'autoTaxCalc', 999, 0, 0, 0, 'zngs', 999, '【终极节点】计算优先级最低(999)，最后一步执行阶梯个税扣除'),

-- ----------------------------------------------------------
-- 【7】公司支出类 (HR视角成本核算，不显示在员工基础工资条)
-- ----------------------------------------------------------
('ER_PENSION_COMP', '养老保险(公司)', 4, 'ER_PENSION', 'erPensionComp', 300, 0, 0, 1, 'ylbx', 300, '公司用工成本'),
('ER_VISA_COMP', '海外签证费用', 4, 'ER_VISA', 'erVisaComp', 310, 0, 0, 0, 'qzfy', 310, '出海员工特有，公司承担费用入账');