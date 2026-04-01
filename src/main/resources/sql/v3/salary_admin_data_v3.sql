-- ----------------------------------------------------------
-- 1. 薪资项目大类 (Level 1: 业务属性)
-- ----------------------------------------------------------
INSERT INTO `sys_dict_type`
(`dict_type_code`, `dict_type_name`, `dict_category`, `remark`)
VALUES
    ('salary_item_category', '薪资项目大类', 'salary', '定义薪资项的物理分类：收入、扣款、税费等');

-- ----------------------------------------------------------
-- 2. 薪资项目细类 (Level 2: 逻辑映射)
-- ----------------------------------------------------------
INSERT INTO `sys_dict_type`
(`dict_type_code`, `dict_type_name`, `dict_category`, `remark`)
VALUES
    ('salary_item_sub_type', '薪资项目细类', 'salary', '定义具体的业务逻辑标识，用于代码或脚本识别');


INSERT INTO `sys_dict_item`
(`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`)
VALUES
    ('salary_item_category', '1', '收入', 10),
    ('salary_item_category', '2', '扣款', 20),
    ('salary_item_category', '3', '税费', 30),
    ('salary_item_category', '4', '公司支出', 40);

-- 收入类细分
INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`) VALUES
                                                                                                           ('salary_item_sub_type', 'INC_BASE', '基本工资', 10),
                                                                                                           ('salary_item_sub_type', 'INC_ALLOWANCE', '岗位津贴', 20),
                                                                                                           ('salary_item_sub_type', 'INC_OVERTIME', '加班工资', 30),
                                                                                                           ('salary_item_sub_type', 'INC_ATTENDANCE', '全勤奖', 35),
                                                                                                           ('salary_item_sub_type', 'INC_BONUS', '绩效奖金', 40),
                                                                                                           ('salary_item_sub_type', 'INC_YEAR_END', '年终奖', 50),
                                                                                                           ('salary_item_sub_type', 'INC_SUBSIDY', '补贴', 60),
                                                                                                           ('salary_item_sub_type', 'INC_FESTIVAL', '节日礼金', 70),
                                                                                                           ('salary_item_sub_type', 'INC_OTHER', '其他收入', 80);

-- 扣款类细分
INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`) VALUES
                                                                                                           ('salary_item_sub_type', 'DED_ABSENT', '缺勤扣款', 100),
                                                                                                           ('salary_item_sub_type', 'DED_LATE', '迟到早退', 110),
                                                                                                           ('salary_item_sub_type', 'DED_FINE', '罚款', 115),
                                                                                                           ('salary_item_sub_type', 'DED_LOAN', '借款扣还', 118),
                                                                                                           ('salary_item_sub_type', 'DED_OTHER', '其他扣款', 120);

-- 统筹与税费 (涉及 PHP 多币种计算的关键项)
INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`) VALUES
                                                                                                           ('salary_item_sub_type', 'TAX_INCOME', '个人所得税', 200),
                                                                                                           ('salary_item_sub_type', 'SI_PENSION', '养老保险(个人)', 210),
                                                                                                           ('salary_item_sub_type', 'SI_MED', '医疗保险(个人)', 220),
                                                                                                           ('salary_item_sub_type', 'SI_UNEMPLOYMENT', '失业保险(个人)', 225),
                                                                                                           ('salary_item_sub_type', 'SI_HOUSING', '住房公积金(个人)', 230),
                                                                                                           ('salary_item_sub_type', 'PHP_SSS', 'SSS (菲律宾社保)', 240),
                                                                                                           ('salary_item_sub_type', 'TAX_LOCAL', '地方税', 250);

INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`) VALUES
                                                                                                           ('salary_item_sub_type', 'ER_PENSION', '养老保险(公司缴纳)', 300),
                                                                                                           ('salary_item_sub_type', 'ER_MED', '医疗保险(公司缴纳)', 310),
                                                                                                           ('salary_item_sub_type', 'ER_HOUSING', '住房公积金(公司缴纳)', 320),
                                                                                                           ('salary_item_sub_type', 'ER_VISA', '签证费用', 330),
                                                                                                           ('salary_item_sub_type', 'ER_TRAVEL', '差旅费', 340),
                                                                                                           ('salary_item_sub_type', 'ER_INSURANCE', '商业保险', 350),
                                                                                                           ('salary_item_sub_type', 'ER_OTHER', '其他公司支出', 360);

-- 1. 薪资管理 (父级目录)
INSERT INTO `sys_menu`
(`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`)
VALUES
    (150, '薪资管理', 'salary_manage', '/salary', 'Layout', 'Money', '', 1, 0, 10);

-- 2. 薪资项目配置 (子菜单)
-- 注意：menu_component 对应你项目的实际文件路径：views/salary/itemconfig/ItemConfigPage
INSERT INTO `sys_menu`
(`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`)
VALUES
    (151, '薪资项目配置', 'salary_item_config', 'itemconfig', 'salary/itemconfig/ItemConfigPage', 'Setting', 'salary:item_config:list', 2, 150, 1);

-- 3. 功能按钮 (权限控制)
-- 对应代码中的 v-hasPerm="['salary:item_config:add']" 等
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`) VALUES
                                                                                                                           (152, '新增项目', 'salary_item_add', 'salary:item_config:add', 3, 151, 1),
                                                                                                                           (153, '修改项目', 'salary_item_edit', 'salary:item_config:edit', 3, 151, 2),
                                                                                                                           (154, '删除项目', 'salary_item_del', 'salary:item_config:del', 3, 151, 3),
                                                                                                                           (155, '同步配置', 'salary_item_refresh', 'salary:item_config:refresh', 3, 151, 4);


-- ====================================================================
-- 📌 菜单脚本：薪资档案管理 (ArchivePage)
-- 功能：包含入职定薪、调薪申请、调薪审批、查看详情等权限控制
-- ====================================================================

-- 1. 薪资档案管理 (子菜单)
-- 对应文件路径：src/views/salary/archive/ArchivePage.vue
INSERT INTO `sys_menu`
(`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`)
VALUES
    (160, '薪资档案管理', 'salary_archive', 'archive', 'salary/archive/ArchivePage', 'Document', 'salary:archive:list', 2, 150, 2);

-- 2. 功能按钮 (权限控制)
-- 对应代码中的 v-hasPerm 权限点
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`) VALUES
                                                                                                                           -- [定薪权限] 对应 handleInit 方法
                                                                                                                           (161, '新员工定薪', 'salary_archive_init', 'salary:archive:init', 3, 160, 1),

                                                                                                                           -- [调薪权限] 对应 handleAdjust 方法
                                                                                                                           (162, '调薪申请', 'salary_archive_adjust', 'salary:archive:adjust', 3, 160, 2),

                                                                                                                           -- [审批权限] 对应 handleOpenAudit 方法
                                                                                                                           (163, '调薪审批', 'salary_archive_audit', 'salary:archive:audit', 3, 160, 3),

                                                                                                                           -- [查询详情权限] 对应 handleDetail 方法
                                                                                                                           (164, '查看详情', 'salary_archive_detail', 'salary:archive:detail', 3, 160, 4),

                                                                                                                           -- [导出权限] (预留，通常档案管理需要导出 Excel)
                                                                                                                           (165, '导出档案', 'salary_archive_export', 'salary:archive:export', 3, 160, 5);

-- 1. 员工基础档案 (子菜单)
-- 对应文件路径：src/views/salary/employee/EmployeePage.vue
-- 🌟 [标注]: 起始 ID 严格从 170 开始，父菜单 ID 假设为 150 (薪资管理)
INSERT INTO `sys_menu`
(`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`)
VALUES
    (170, '员工基础档案', 'salary_employee', 'employee', 'salary/employee/EmployeePage', 'User', 'salary:employee:list', 2, 150, 1);

-- 2. 功能按钮 (权限控制)
-- 对应代码中的 v-hasPerm 权限点，ID 从 171 开始顺延
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`) VALUES
-- [查看权限] 对应 getList 方法及页面初始加载权限
(171, '查看员工列表', 'salary_employee_query', 'salary:employee:query', 3, 170, 1),

-- [新增权限] 对应页面 handleAdd 方法
(172, '新增员工档案', 'salary_employee_add', 'salary:employee:add', 3, 170, 2),

-- [修改权限] 对应页面 handleUpdate 方法
(173, '修改员工档案', 'salary_employee_edit', 'salary:employee:edit', 3, 170, 3),

-- [删除权限] 对应 handleDelete 和 handleBatchDelete 方法
(174, '销毁员工档案', 'salary_employee_del', 'salary:employee:del', 3, 170, 4),

-- [详情权限] 对应 getEmployeeDetailApi
(175, '查看档案详情', 'salary_employee_detail', 'salary:employee:detail', 3, 170, 5);




INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`)
VALUES
-- ==========================================================
-- 【5】薪资汇总与发薪管理 (子菜单，Sort = 5，200号段)
-- 包含：发薪台展示、工资条快照查看、锁定防篡改、触发核算等核心财务权限
-- ==========================================================
(200, '薪资汇总与发薪', 'salary_summary', 'summary', 'salary/summary/SummaryPage', '', 'Wallet', 'salary:summary:list', 2, 150, 5, 1, 1),
(201, '查看汇总列表', 'salary_summary_query', '', '', '', '', 'salary:summary:query', 3, 200, 1, 1, 1),
(202, '查看工资条明细', 'salary_summary_detail', '', '', '', '', 'salary:summary:detail', 3, 200, 2, 1, 1),
(203, '锁定与解锁单据', 'salary_summary_lock', '', '', '', '', 'salary:summary:lock', 3, 200, 3, 1, 1),
(204, '执行薪资引擎核算', 'salary_summary_calc', '', '', '', '', 'salary:summary:calc', 3, 200, 4, 1, 1);


INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`)
VALUES
-- ==========================================================
-- 【6】薪资引擎配置 (子菜单，Sort = 6，210号段)
-- 包含：计算规则库(Rule)的维护、瀑布流管道(Pipeline)的编排与一键发布权限
-- ==========================================================salary_calc_pipeline
(210, '薪资引擎配置', 'salary_engine', 'engine', 'salary/engine/EngineConfigPage', '', 'Operation', 'salary:engine:list', 2, 150, 6, 1, 1),

-- --- 按钮级权限 (归属在 210 菜单下) ---
(211, '查看规则与管道', 'salary_engine_query', '', '', '', '', 'salary:engine:query', 3, 210, 1, 1, 1),
(212, '新增计算规则', 'salary_calc_rule_add', '', '', '', '', 'salary:calc_rule:add', 3, 210, 2, 1, 1),
(213, '修改计算规则', 'salary_calc_rule_edit', '', '', '', '', 'salary:calc_rule:edit', 3, 210, 3, 1, 1),
(214, '删除计算规则', 'salary_calc_rule_del', '', '', '', '', 'salary:calc_rule:del', 3, 210, 4, 1, 1),
(215, '发布管道编排', 'salary_calc_pipeline_save', '', '', '', '', 'salary:calc_pipeline:save', 3, 210, 5, 1, 1);