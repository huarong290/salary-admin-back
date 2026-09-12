-- ============================================================================
-- 薪资计算管道编排重整 —— OFFICIAL_STAFF_2026 (v1) 适配个税
-- ----------------------------------------------------------------------------
-- 文件    : doc/sql/v9/pipeline_reorder_official_staff_2026.sql
-- 目标库  : salary_admin_v9
-- 影响表  : salary_calc_pipeline_step
-- 适用版本: pipeline_code = 'OFFICIAL_STAFF_2026', pipeline_version = 1
-- 执行方式: 手动刷库（建议先看第 0 步备份、执行后跑第 2 步验证）
-- ----------------------------------------------------------------------------
-- 引擎排序规则（AbstractSalaryProcessor#getPipelineSteps）：
--     ORDER BY stage ASC, sort_order ASC
-- 个税是"分水岭"：执行到 AUTO_TAX_CALC 时，引擎取截至当时的累计应税收入
--     env["_taxableIncome"] = aggregator.getTaxableIncomeTotal()
-- 作为计税基数 —— 因此"排在个税之前的项才进税基"。
-- ----------------------------------------------------------------------------
-- 编排三原则（铁律）：
--   ① taxable_flag = 1 的收入项         → 必须排在个税之前
--   ② tax_deductible_flag = 1 的扣除项  → 必须排在个税之前
--   ③ 税后项（罚款/水电/押金/返还/公司支出）→ 必须排在个税之后
-- ----------------------------------------------------------------------------
-- 本次重排的实质变更：
--   ★ PREV_MONTH_ADJUSTMENT（上月补发/续扣, taxable_flag=1）
--       由 stage 5 / sort 660（税后）移到 stage 5 / sort 610（税前）
--       → 该笔应税收入自本次起进入税基（修复"漏进税基"缺陷）
--   · 其余 61 步仅做语义归位（其 taxable_flag / tax_deductible_flag 均为 0），
--     不影响任何金额计算结果
--   · AUTO_TAX_CALC 由 stage 4 移到 stage 7（stage 仅用于审计日志，无代码硬编码依赖）
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 0. 备份（可回滚）
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS salary_calc_pipeline_step_bak_20260912;
CREATE TABLE salary_calc_pipeline_step_bak_20260912 AS
SELECT * FROM salary_calc_pipeline_step
WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND pipeline_version = 1;

-- ---------------------------------------------------------------------------
-- 1. 一次性重排 stage + sort_order（62 步全覆盖，脚本幂等可重复执行）
--    阶段语义：
--      S1 基础工资与考勤   S2 绩效与提成   S3 津贴与福利   S4 奖金与年终
--      S5 补发与追溯       S6 税前扣除     S7 个税(分水岭) S8 税后扣款与返还
--      S9 公司成本
-- ---------------------------------------------------------------------------
UPDATE salary_calc_pipeline_step p
JOIN (
    -- ===== S1 基础工资与考勤 (10 ~) =====
              SELECT 'BASE_SALARY'                AS rule_code, 1 AS stage,   10 AS sort_order
    UNION ALL SELECT 'OVERTIME_PAY_DAY',                          1,   20
    UNION ALL SELECT 'OVERTIME_PAY_HOUR',                         1,   30
    UNION ALL SELECT 'ATTENDANCE_BONUS',                          1,   40
    -- ===== S2 绩效与提成 (100 ~) =====
    UNION ALL SELECT 'KPI_BONUS',                                 2,  100
    UNION ALL SELECT 'COMMISSION_SALES',                          2,  110
    UNION ALL SELECT 'COMMISSION_AGENT',                          2,  120
    -- ===== S3 津贴与福利 (200 ~) =====
    UNION ALL SELECT 'HOUSING_ALLOW',                             3,  200
    UNION ALL SELECT 'MEAL_ALLOW',                                3,  210
    UNION ALL SELECT 'SHIFT_12H_ALLOWANCE',                       3,  220
    UNION ALL SELECT 'QUARANTINE_ALLOWANCE',                      3,  230
    UNION ALL SELECT 'OTHER_ALLOWANCE',                           3,  240
    UNION ALL SELECT 'SAFETY_CARD_BONUS',                         3,  250
    UNION ALL SELECT 'ANNUAL_LEAVE_BONUS',                        3,  260
    UNION ALL SELECT 'REFERRAL_BONUS',                            3,  270
    UNION ALL SELECT 'BIRTHDAY_BONUS',                            3,  280
    UNION ALL SELECT 'FESTIVAL_SPRING_GIFT',                      3,  290
    UNION ALL SELECT 'FESTIVAL_SPRING_BONUS',                     3,  300
    UNION ALL SELECT 'FESTIVAL_DRAGON_BOAT_GIFT',                 3,  310
    UNION ALL SELECT 'FESTIVAL_DRAGON_BOAT_BONUS',                3,  320
    UNION ALL SELECT 'FESTIVAL_MID_AUTUMN_GIFT',                  3,  330
    UNION ALL SELECT 'FESTIVAL_MID_AUTUMN_BONUS',                 3,  340
    UNION ALL SELECT 'EVENT_EURO_CUP',                            3,  350
    UNION ALL SELECT 'EVENT_WORLD_CUP',                           3,  360
    -- ===== S4 奖金与年终 (400 ~) =====
    UNION ALL SELECT 'ANNUAL_BONUS_13',                           4,  400
    UNION ALL SELECT 'ANNUAL_BONUS_13_5',                         4,  410
    UNION ALL SELECT 'ANNUAL_BONUS_14',                           4,  420
    UNION ALL SELECT 'ANNUAL_BONUS_14_5',                         4,  430
    UNION ALL SELECT 'ANNUAL_BONUS_15',                           4,  440
    UNION ALL SELECT 'ANNUAL_BONUS_15_5',                         4,  450
    UNION ALL SELECT 'ANNUAL_BONUS_16',                           4,  460
    UNION ALL SELECT 'ANNUAL_BONUS_16_5',                         4,  470
    UNION ALL SELECT 'ANNUAL_BONUS_17',                           4,  480
    UNION ALL SELECT 'ANNUAL_BONUS_17_5',                         4,  490
    UNION ALL SELECT 'ANNUAL_BONUS_18',                           4,  500
    UNION ALL SELECT 'ANNUAL_BONUS_18_5',                         4,  510
    UNION ALL SELECT 'ANNUAL_BONUS_19',                           4,  520
    UNION ALL SELECT 'LOYALTY_BONUS_2Y',                          4,  530
    UNION ALL SELECT 'LOYALTY_BONUS_5Y',                          4,  540
    UNION ALL SELECT 'LOYALTY_BONUS_10Y',                         4,  550
    -- ===== S5 补发与追溯 (600 ~) ★ 必须早于个税 =====
    UNION ALL SELECT 'ATTENDANCE_REISSUE',                        5,  600
    UNION ALL SELECT 'PREV_MONTH_ADJUSTMENT',                     5,  610   -- ★ 660 → 610：纳入税基
    UNION ALL SELECT 'SI_REISSUE_IND',                            5,  620
    UNION ALL SELECT 'EXPENSE_REIMBURSE_ONBOARD',                 5,  630
    -- ===== S6 税前扣除 (700 ~) ★ tax_deductible_flag = 1 =====
    UNION ALL SELECT 'SI_PENSION_IND',                            6,  700
    UNION ALL SELECT 'SI_MED_IND',                                6,  710
    UNION ALL SELECT 'SI_HOUSING_IND',                            6,  720
    UNION ALL SELECT 'ABSENT_DEDUCTION',                          6,  730
    UNION ALL SELECT 'LATE_DEDUCTION',                            6,  740
    -- ===== S7 个税 (900) ★ 分水岭，block_flag = 1 =====
    UNION ALL SELECT 'AUTO_TAX_CALC',                             7,  900
    -- ===== S8 税后扣款与返还 (1000 ~) ★ 必须晚于个税 =====
    UNION ALL SELECT 'UTILITY_DEDUCTION',                         8, 1000
    UNION ALL SELECT 'FINE_DEDUCTION',                            8, 1010
    UNION ALL SELECT 'PASSPORT_FEE_DEDUCTION',                    8, 1020
    UNION ALL SELECT 'DEPOSIT_DEDUCTION_CURRENT',                 8, 1030
    UNION ALL SELECT 'OTHER_DEDUCTION',                           8, 1040
    UNION ALL SELECT 'RESIGNATION_SETTLEMENT',                    8, 1050
    UNION ALL SELECT 'DEPOSIT_REFUND_CURRENT',                    8, 1060
    UNION ALL SELECT 'FINE_REBATE',                               8, 1070
    UNION ALL SELECT 'UTILITY_REBATE',                            8, 1080
    UNION ALL SELECT 'PASSPORT_FEE_REBATE',                       8, 1090
    -- ===== S9 公司成本 (1100 ~) 不参与员工净额 =====
    UNION ALL SELECT 'ER_PENSION_COMP',                           9, 1100
    UNION ALL SELECT 'ER_VISA_COMP',                              9, 1110
) m ON m.rule_code = p.rule_code
SET p.stage      = m.stage,
    p.sort_order = m.sort_order
WHERE p.pipeline_code    = 'OFFICIAL_STAFF_2026'
  AND p.pipeline_version = 1;
-- 预期受影响行数 = 62

-- ---------------------------------------------------------------------------
-- 2. 验证（三条必查）
-- ---------------------------------------------------------------------------
-- 2.1 阶段分布（期望 9 个阶段、合计 62 步、个税落在 stage 7）
SELECT stage, COUNT(*) AS steps, MIN(sort_order) AS min_sort, MAX(sort_order) AS max_sort
FROM salary_calc_pipeline_step
WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND pipeline_version = 1
GROUP BY stage ORDER BY stage;

-- 2.2 铁律校验：个税之后不应再出现"影响税基"的项（期望 0 行）
SELECT p.stage, p.sort_order, p.rule_code, c.taxable_flag, c.tax_deductible_flag
FROM salary_calc_pipeline_step p
JOIN salary_item_config c ON c.item_code = p.rule_code
WHERE p.pipeline_code = 'OFFICIAL_STAFF_2026' AND p.pipeline_version = 1
  AND p.stage > 7
  AND (c.taxable_flag = 1 OR c.tax_deductible_flag = 1);

-- 2.3 个税步骤位置（期望 stage = 7, sort_order = 900, block_flag = 1）
SELECT stage, sort_order, rule_code, block_flag, skip_if_null
FROM salary_calc_pipeline_step
WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND pipeline_version = 1
  AND rule_code = 'AUTO_TAX_CALC';

-- ---------------------------------------------------------------------------
-- 3. 回滚（如需）
-- ---------------------------------------------------------------------------
-- UPDATE salary_calc_pipeline_step p
-- JOIN salary_calc_pipeline_step_bak_20260912 b ON b.id = p.id
-- SET p.stage = b.stage, p.sort_order = b.sort_order;

-- ---------------------------------------------------------------------------
-- 4.（可选）把"社保个人"由 税费类(3) 调整为 扣款类(2)
--    净额不变，仅在工资条上不再归入"税费"，语义更准确
-- ---------------------------------------------------------------------------
-- UPDATE salary_item_config SET item_category = 2
-- WHERE item_code IN ('SI_PENSION_IND','SI_MED_IND','SI_HOUSING_IND');
