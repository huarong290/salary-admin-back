-- ==========================================================
-- 1. 薪资周期管理 (Salary Period)
-- ==========================================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`) VALUES
                                                                                                                                                                       (140, '薪资周期管理', 'salary_period_mgr', 'period', 'salary/period/PeriodPage', 'Calendar', 'salary:period:list', 2, 125, 1),
                                                                                                                                                                       (141, '查询周期', 'salary_period_query', '', '', '', 'salary:period:query', 3, 140, 1),
                                                                                                                                                                       (142, '新增周期', 'salary_period_add', '', '', '', 'salary:period:add', 3, 140, 2),
                                                                                                                                                                       (143, '修改周期', 'salary_period_edit', '', '', '', 'salary:period:edit', 3, 140, 3),
                                                                                                                                                                       (144, '删除周期', 'salary_period_del', '', '', '', 'salary:period:del', 3, 140, 4),
                                                                                                                                                                       (145, '批量删除周期', 'salary_period_batch_del', '', '', '', 'salary:period:batch', 3, 140, 5);

-- ==========================================================
-- 2. 收入项配置 (Salary Income Type)
-- ==========================================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`) VALUES
                                                                                                                                                                       (150, '收入项配置', 'salary_income_type_mgr', 'income-type', 'salary/incomeType/IncomeTypePage', 'Wallet', 'salary:income-type:list', 2, 125, 2),
                                                                                                                                                                       (151, '查询收入项', 'salary_income_type_query', '', '', '', 'salary:income-type:query', 3, 150, 1),
                                                                                                                                                                       (152, '新增收入项', 'salary_income_type_add', '', '', '', 'salary:income-type:add', 3, 150, 2),
                                                                                                                                                                       (153, '修改收入项', 'salary_income_type_edit', '', '', '', 'salary:income-type:edit', 3, 150, 3),
                                                                                                                                                                       (154, '删除收入项', 'salary_income_type_del', '', '', '', 'salary:income-type:del', 3, 150, 4),
                                                                                                                                                                       (155, '批量删除收入项', 'salary_income_type_batch_del', '', '', '', 'salary:income-type:batch', 3, 150, 5);

-- ==========================================================
-- 3. 扣款项配置 (Salary Deduction Type)
-- ==========================================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`) VALUES
                                                                                                                                                                       (160, '扣款项配置', 'salary_deduction_type_mgr', 'deduction-type', 'salary/deductionType/DeductionTypePage', 'Remove', 'salary:deduction-type:list', 2, 125, 3),
                                                                                                                                                                       (161, '查询扣款项', 'salary_deduction_type_query', '', '', '', 'salary:deduction-type:query', 3, 160, 1),
                                                                                                                                                                       (162, '新增扣款项', 'salary_deduction_type_add', '', '', '', 'salary:deduction-type:add', 3, 160, 2),
                                                                                                                                                                       (163, '修改扣款项', 'salary_deduction_type_edit', '', '', '', 'salary:deduction-type:edit', 3, 160, 3),
                                                                                                                                                                       (164, '删除扣款项', 'salary_deduction_type_del', '', '', '', 'salary:deduction-type:del', 3, 160, 4),
                                                                                                                                                                       (165, '批量删除扣款项', 'salary_deduction_type_batch_del', '', '', '', 'salary:deduction-type:batch', 3, 160, 5);

-- ==========================================================
-- 4. 薪资档案配置 (Salary Archive - 根据我们之前的代码补充)
-- ==========================================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`) VALUES
                                                                                                                                                                       (170, '薪资档案管理', 'salary_archive_mgr', 'archive', 'salary/archive/SalaryArchivePage', 'Document', 'salary:archive:list', 2, 125, 4),
                                                                                                                                                                       (171, '查询档案', 'salary_archive_query', '', '', '', 'salary:archive:query', 3, 170, 1),
                                                                                                                                                                       (172, '定薪调薪(新增)', 'salary_archive_add', '', '', '', 'salary:archive:add', 3, 170, 2),
                                                                                                                                                                       (173, '查看详情', 'salary_archive_detail', '', '', '', 'salary:archive:detail', 3, 170, 3),
                                                                                                                                                                       (174, '撤销版本', 'salary_archive_revoke', '', '', '', 'salary:archive:revoke', 3, 170, 4),
                                                                                                                                                                       (175, '调薪(修改)', 'salary_archive_edit', '', '', '', 'salary:archive:edit', 3, 170, 5),
                                                                                                                                                                       (176, '审核档案', 'salary_archive_audit', '', '', '', 'salary:archive:audit', 3, 170, 6);

-- ==========================================================
-- 5. 结算汇总管理 (Salary Summary)
-- ==========================================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`) VALUES
                                                                                                                                                                       (180, '结算汇总管理', 'salary_summary_mgr', 'summary', 'salary/summary/SummaryPage', 'DataAnalysis', 'salary:summary:list', 2, 125, 5),
                                                                                                                                                                       (181, '查询汇总', 'salary_summary_query', '', '', '', 'salary:summary:query', 3, 180, 1),
                                                                                                                                                                       (182, '一键核算', 'salary_summary_calc', '', '', '', 'salary:summary:calc', 3, 180, 2),
                                                                                                                                                                       (183, '查看详情', 'salary_summary_detail', '', '', '', 'salary:summary:detail', 3, 180, 3),
                                                                                                                                                                       (184, '作废汇总', 'salary_summary_del', '', '', '', 'salary:summary:del', 3, 180, 4),
                                                                                                                                                                       (185, '批量作废', 'salary_summary_batch_del', '', '', '', 'salary:summary:batch_del', 3, 180, 5);
-- ==========================================================
-- 6. 发薪明细记录 (Salary Payment Record)
-- ==========================================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`) VALUES
                                                                                                                                                                       (190, '发薪明细记录', 'salary_payment_record_mgr', 'paymentrecord', 'salary/paymentrecord/PaymentRecordPage', 'List', 'salary:payment_record:list', 2, 125, 6),
                                                                                                                                                                       (191, '查询明细', 'salary_payment_record_query', '', '', '', 'salary:payment_record:query', 3, 190, 1),
                                                                                                                                                                       (192, '手动调整', 'salary_payment_record_edit', '', '', '', 'salary:payment_record:edit', 3, 190, 2),
                                                                                                                                                                       (193, '删除明细', 'salary_payment_record_del', '', '', '', 'salary:payment_record:del', 3, 190, 3),
                                                                                                                                                                       (194, '明细导出', 'salary_payment_record_export', '', '', '', 'salary:payment_record:export', 3, 190, 4);
-- =================================================================================
-- 薪资周期数据初始化 (员工ID: 1)
-- 包含 2024, 2025, 2026 三个年度的在岗记录
-- =================================================================================

INSERT INTO salary_admin.salary_period
(employee_id, work_month, settlement_month, start_date, end_date, month_days, attendance_days, delete_flag, create_by, create_time, update_by, update_time)
VALUES
    -- 2024年度
    (1, '1', '202404', '2024-04-01', '2024-04-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '2', '202405', '2024-05-01', '2024-05-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '3', '202406', '2024-06-01', '2024-06-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '4', '202407', '2024-07-01', '2024-07-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '5', '202408', '2024-08-01', '2024-08-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '6', '202409', '2024-09-01', '2024-09-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '7', '202410', '2024-10-01', '2024-10-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '8', '202411', '2024-11-01', '2024-11-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '9', '202412', '2024-12-01', '2024-12-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),

    -- 2025年度
    (1, '10', '202501', '2025-01-01', '2025-01-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '11', '202502', '2025-02-01', '2025-02-28', 28, 28, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '12', '202503', '2025-03-01', '2025-03-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '13', '202504', '2025-04-01', '2025-04-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '14', '202505', '2025-05-01', '2025-05-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '15', '202506', '2025-06-01', '2025-06-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '16', '202507', '2025-07-01', '2025-07-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '17', '202508', '2025-08-01', '2025-08-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '18', '202509', '2025-09-01', '2025-09-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '19', '202510', '2025-10-01', '2025-10-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '20', '202511', '2025-11-01', '2025-11-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '21', '202512', '2025-12-01', '2025-12-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),

    -- 2026年度
    (1, '22', '202601', '2026-01-01', '2026-01-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '23', '202602', '2026-02-01', '2026-02-28', 28, 28, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '24', '202603', '2026-03-01', '2026-03-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '25', '202604', '2026-04-01', '2026-04-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '26', '202605', '2026-05-01', '2026-05-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '27', '202606', '2026-06-01', '2026-06-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '28', '202607', '2026-07-01', '2026-07-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '29', '202608', '2026-08-01', '2026-08-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '30', '202609', '2026-09-01', '2026-09-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '31', '202610', '2026-10-01', '2026-10-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '32', '202611', '2026-11-01', '2026-11-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '33', '202612', '2026-12-01', '2026-12-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    -- 2027年度
    (1, '34', '202701', '2027-01-01', '2027-01-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '35', '202702', '2027-02-01', '2027-02-28', 28, 28, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '36', '202703', '2027-03-01', '2027-03-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '37', '202704', '2027-04-01', '2027-04-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '38', '202705', '2027-05-01', '2027-05-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '39', '202706', '2027-06-01', '2027-06-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '40', '202707', '2027-07-01', '2027-07-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '41', '202708', '2027-08-01', '2027-08-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '42', '202709', '2027-09-01', '2027-09-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '43', '202710', '2027-10-01', '2027-10-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '44', '202711', '2027-11-01', '2027-11-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '45', '202712', '2027-12-01', '2027-12-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),

    -- 2028年度 (闰年)
    (1, '46', '202801', '2028-01-01', '2028-01-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '47', '202802', '2028-02-01', '2028-02-29', 29, 29, 0, 'admin', NOW(), 'admin', NOW()), -- 🌟 闰二月
    (1, '48', '202803', '2028-03-01', '2028-03-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '49', '202804', '2028-04-01', '2028-04-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '50', '202805', '2028-05-01', '2028-05-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '51', '202806', '2028-06-01', '2028-06-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '52', '202807', '2028-07-01', '2028-07-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '53', '202808', '2028-08-01', '2028-08-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '54', '202809', '2028-09-01', '2028-09-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '55', '202810', '2028-10-01', '2028-10-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '56', '202811', '2028-11-01', '2028-11-30', 30, 30, 0, 'admin', NOW(), 'admin', NOW()),
    (1, '57', '202812', '2028-12-01', '2028-12-31', 31, 31, 0, 'admin', NOW(), 'admin', NOW());