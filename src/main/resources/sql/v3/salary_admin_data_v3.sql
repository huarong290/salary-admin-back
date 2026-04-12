

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




-- ==========================================================
-- 【5】薪资引擎配置 (升级为目录级别，menu_type = 1，统领 200~220 号段)
-- ==========================================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`)
VALUES
    (200, '薪资引擎配置', 'salary_engine', 'engine', '', '', 'Operation', '', 1, 150, 5, 1, 1);

-- ==========================================================
-- 5.1 薪资引擎 -> 计算规则库 (占用 210 号段)
-- ==========================================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`)
VALUES
    (210, '计算规则库', 'salary_calc_rule', 'calc-rule', 'salary/calcrule/CalcRulePage', '', 'Collection', 'salary:rule:list', 2, 200, 1, 1, 1),
-- --- 规则库：按钮级权限 ---
    (211, '查询规则', 'salary_rule_query', '', '', '', '', 'salary:rule:query', 3, 210, 1, 1, 1),
    (212, '新增规则', 'salary_rule_add', '', '', '', '', 'salary:rule:add', 3, 210, 2, 1, 1),
    (213, '修改规则', 'salary_rule_edit', '', '', '', '', 'salary:rule:edit', 3, 210, 3, 1, 1),
    (214, '删除规则', 'salary_rule_del', '', '', '', '', 'salary:rule:del', 3, 210, 4, 1, 1);

-- ==========================================================
-- 5.2 薪资引擎 -> 核算管道编排 (占用 220 号段)
-- ==========================================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`)
VALUES
    (220, '核算管道编排', 'salary_calc_pipeline', 'calc-pipeline', 'salary/calcpipeline/CalcPipelinePage', '', 'Connection', 'salary:pipeline:list', 2, 200, 2, 1, 1),
-- --- 管道编排：高级业务按钮权限 ---
    (221, '查询管道', 'salary_pipeline_query', '', '', '', '', 'salary:pipeline:query', 3, 220, 1, 1, 1),
    (222, '新建管道', 'salary_pipeline_add', '', '', '', '', 'salary:pipeline:add', 3, 220, 2, 1, 1),
    (223, '修改管道元数据', 'salary_pipeline_edit', '', '', '', '', 'salary:pipeline:edit', 3, 220, 3, 1, 1),
    (224, '删除管道', 'salary_pipeline_del', '', '', '', '', 'salary:pipeline:del', 3, 220, 4, 1, 1),
    (225, '发布瀑布流配置', 'salary_pipeline_design', '', '', '', '', 'salary:pipeline:design', 3, 220, 5, 1, 1),
    (226, '设为系统默认', 'salary_pipeline_default', '', '', '', '', 'salary:pipeline:default', 3, 220, 6, 1, 1),
    (227, '升级新版本', 'salary_pipeline_upgrade', '', '', '', '', 'salary:pipeline:upgrade', 3, 220, 7, 1, 1);

-- ==========================================================
-- 【6】薪资汇总与发薪管理 (为引擎让路，顺延调整至 230 号段)
-- ==========================================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`)
VALUES
    (230, '薪资汇总与发薪', 'salary_summary', 'summary', 'salary/summary/SummaryPage', '', 'Wallet', 'salary:summary:list', 2, 150, 6, 1, 1),
-- --- 发薪台：按钮级权限 ---
    (231, '查看汇总列表', 'salary_summary_query', '', '', '', '', 'salary:summary:query', 3, 230, 1, 1, 1),
    (232, '查看工资条明细', 'salary_summary_detail', '', '', '', '', 'salary:summary:detail', 3, 230, 2, 1, 1),
    (233, '锁定与解锁单据', 'salary_summary_lock', '', '', '', '', 'salary:summary:lock', 3, 230, 3, 1, 1),
    (234, '执行薪资引擎核算', 'salary_summary_calc', '', '', '', '', 'salary:summary:calc', 3, 230, 4, 1, 1);





INSERT INTO salary_calc_pipeline_info
(id, pipeline_code, pipeline_name, version, default_flag, status, remark, delete_flag, create_by, create_time, update_by, update_time)
VALUES(1, 'OFFICIAL_STAFF_2026', '2026年度正式员工核算流', 1, 0, 1, '本管道适用于集团 2026 年度全体正式员工月度核算。
制度依据：遵循 2026 版薪酬管理办法，包含基本工资、五险一金及各项绩效奖金。
逻辑特性：计算顺序严格遵循 [基础->补贴->扣款->税->汇总] 阶段，已同步 2026 年最新公积金缴存基数上限。
维护人：HR-薪酬组 / 技术支撑部', 0, 'system', '2026-04-02 14:11:34', 'system', '2026-04-02 14:11:34');




-- ==========================================================
-- 注入测试数据：2026年度正式员工核算流 (V1版本) 的瀑布流步骤
-- ==========================================================
INSERT INTO `salary_calc_pipeline_step`
(`pipeline_code`, `pipeline_version`, `rule_code`, `rule_name`, `rule_type`, `condition_script`, `stage`, `sort_order`, `block_flag`, `skip_if_null`, `status`, `delete_flag`, `create_by`, `create_time`, `update_by`, `update_time`)
VALUES
-- 第 1 步：算底薪 (基础阶段，必须成功，失败则阻断整个引擎)
('OFFICIAL_STAFF_2026', 1, 'BASE_SALARY', '底薪计算', 1, NULL, 1, 1, 1, 0, 1, 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

-- 第 2 步：算全勤奖 (基础阶段，同样阻断)
('OFFICIAL_STAFF_2026', 1, 'ATTENDANCE_BONUS', '全勤奖', 1, NULL, 1, 2, 1, 0, 1, 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

-- 第 3 步：算KPI (基础阶段，带有条件：只算KPI>0的；并且即使异常也不阻断主流程，没算出来当0处理)
('OFFICIAL_STAFF_2026', 1, 'KPI_BONUS', 'KPI绩效计算', 1, 'kpi_score > 0', 1, 3, 0, 1, 1, 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

-- 第 4 步：算住房补贴 (补贴阶段，换阶段了)
('OFFICIAL_STAFF_2026', 1, 'HOUSING_ALLOW', '住房补贴', 1, NULL, 2, 4, 1, 0, 1, 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

-- 第 5 步：算个税 (税务阶段，作为瀑布流的最后一步，拦截并计算所有的应发项)
('OFFICIAL_STAFF_2026', 1, 'AUTO_TAX_CALC', '智能个税核算中心', 1, NULL, 4, 5, 1, 0, 1, 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);









-- ==========================================================
-- 新增：薪资模块 (salary) - 个税核算规则字典类型
-- ==========================================================
INSERT INTO `sys_dict_type` (`dict_type_code`, `dict_type_name`, `dict_category`, `remark`, `create_by`)
VALUES
    ('salary_tax_rule', '个税核算规则', 'salary', '定义员工发薪时适用的个人所得税计算标准及计税分支', 'system');

-- ==========================================================
-- 新增：个税核算规则明细项 (严格对应 Aviator 脚本的分支逻辑)
-- ==========================================================
INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`, `create_by`)
VALUES
    ('salary_tax_rule', 'TAX_RESIDENT_CN', '中国居民综合所得税', 10, 'system'),
    ('salary_tax_rule', 'TAX_LABOR', '劳务报酬所得税', 20, 'system'),
    ('salary_tax_rule', 'NO_TAX', '不计税(外包/免税)', 30, 'system');

-- ==========================================================
-- 1. 新增字典类型：薪资核算阶段
-- ==========================================================
INSERT INTO `sys_dict_type` (`dict_type_code`, `dict_type_name`, `dict_category`, `remark`, `create_by`)
VALUES
    ('salary_calc_stage', '薪资核算阶段', 'salary', '定义薪资瀑布流引擎执行的物理次序阶段', 'system');

-- ==========================================================
-- 2. 新增字典明细：严格对应 1~5 阶段
-- ==========================================================
INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`, `create_by`)
VALUES
    ('salary_calc_stage', '1', '基础薪资阶段', 10, 'system'),
    ('salary_calc_stage', '2', '津贴与奖金阶段', 20, 'system'),
    ('salary_calc_stage', '3', '扣款与社保阶段', 30, 'system'),
    ('salary_calc_stage', '4', '税务核算阶段', 40, 'system'),
    ('salary_calc_stage', '5', '最终汇总阶段', 50, 'system');