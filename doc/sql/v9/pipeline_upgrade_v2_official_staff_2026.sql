-- ============================================================================
-- 薪资计算管道 —— 以"新增 V2 版本"方式落地个税编排（保留 V1 历史口径）
-- ----------------------------------------------------------------------------
-- 文件    : doc/sql/v9/pipeline_upgrade_v2_official_staff_2026.sql
-- 目标库  : salary_admin_v9
-- 影响表  : salary_calc_pipeline_info / salary_calc_pipeline_step
-- 适用    : pipeline_code = 'OFFICIAL_STAFF_2026'
-- 配套    : doc/sql/v9/pipeline_reorder_official_staff_2026.sql（原地改 V1 的方案）
--           doc/calc-pipeline-spec.md（编排规范）
-- ----------------------------------------------------------------------------
-- 与"原地改 V1"的区别：
--   · V1 保持原样（历史工资单可继续按 V1 口径重算、可回溯）
--   · 新建 V2 承载新编排，并把全局默认管道切到 V2
--   · 回滚 = 把默认切回 V1 + 逻辑删除 V2
-- ----------------------------------------------------------------------------
-- ⚠️ 重要前置检查（否则本次升级不会生效！）
--   前端发薪台目前**硬编码**了管道版本：
--       src/views/salary/summary/SummaryPage.vue:1018-1019
--           pipelineCode: 'OFFICIAL_STAFF_2026',
--           pipelineVersion: 1,
--   后端 SalaryCoreEngineImpl 会把这两个值透传到每个员工的单人核算请求
--   （SalaryCoreEngineImpl:199-200），而引擎对"显式指定"的管道是**优先采用**的，
--   因此：不改前端 → 跑批仍走 V1，V2 只有在"调用方不传管道"时才会被解析为默认。
--   前端改法（二选一）：
--     a) 删除 params 中的 pipelineCode / pipelineVersion，交给后端按"默认管道"解析（推荐）
--     b) 先调"获取默认管道"接口，再把返回值带上
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 0. 前置检查
-- ---------------------------------------------------------------------------
-- 0.1 确认 V2 尚不存在（期望 0；若已存在请先回滚再重跑）
SELECT COUNT(*) AS v2_exists FROM salary_calc_pipeline_info
WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND version = 2 AND delete_flag = 0;

-- 0.2 记录 V1 现状（期望 62）
SELECT COUNT(*) AS v1_steps FROM salary_calc_pipeline_step
WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND pipeline_version = 1 AND delete_flag = 0;

-- ---------------------------------------------------------------------------
-- 1. 新增管道主表 V2（沿用 V1 编码/名称，版本 +1，不设为默认）
-- ---------------------------------------------------------------------------
INSERT INTO salary_calc_pipeline_info
    (pipeline_code, pipeline_name, version, default_flag, status, remark)
SELECT pipeline_code,
       CONCAT(pipeline_name, ' (V2)'),
       2,
       0,
       1,
       'V2：按个税分水岭重排阶段 S1-S9，应税收入与税前扣除项全部前置到个税之前'
FROM salary_calc_pipeline_info
WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND version = 1 AND delete_flag = 0;

-- ---------------------------------------------------------------------------
-- 2. 深拷贝 V1 的全部步骤到 V2（排除主键，其余字段原样复制）
-- ---------------------------------------------------------------------------
INSERT INTO salary_calc_pipeline_step
    (pipeline_code, pipeline_version, rule_code, rule_name, rule_type, condition_script,
     stage, sort_order, block_flag, skip_if_null, status)
SELECT pipeline_code, 2, rule_code, rule_name, rule_type, condition_script,
       stage, sort_order, block_flag, skip_if_null, status
FROM salary_calc_pipeline_step
WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND pipeline_version = 1 AND delete_flag = 0;
-- 预期 62 行

-- ---------------------------------------------------------------------------
-- 3. 只对 V2 执行编排重排（62 步映射；与原地方案完全一致，幂等）
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
  AND p.pipeline_version = 2;
-- 预期受影响行数 = 61（BASE_SALARY 位置本就一致，值未变不计入）

-- ---------------------------------------------------------------------------
-- 4. 把全局默认管道切到 V2（全局唯一默认，与 setDefaultPipeline 行为一致）
-- ---------------------------------------------------------------------------
UPDATE salary_calc_pipeline_info SET default_flag = 0 WHERE delete_flag = 0;
UPDATE salary_calc_pipeline_info SET default_flag = 1
WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND version = 2 AND delete_flag = 0;

-- ---------------------------------------------------------------------------
-- 5. 验证
-- ---------------------------------------------------------------------------
-- 5.1 管道列表（V2 应为 default_flag = 1）
SELECT id, pipeline_code, pipeline_name, version, default_flag, status
FROM salary_calc_pipeline_info WHERE delete_flag = 0
ORDER BY pipeline_code, version;

-- 5.2 V2 阶段分布（期望 9 个阶段、62 步、个税落在 stage 7）
SELECT stage, COUNT(*) AS steps, MIN(sort_order) AS min_sort, MAX(sort_order) AS max_sort
FROM salary_calc_pipeline_step
WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND pipeline_version = 2 AND delete_flag = 0
GROUP BY stage ORDER BY stage;

-- 5.3 V2 铁律校验：个税之后不应再出现"影响税基"的项（期望 0 行）
SELECT p.stage, p.sort_order, p.rule_code, c.taxable_flag, c.tax_deductible_flag
FROM salary_calc_pipeline_step p
JOIN salary_item_config c ON c.item_code = p.rule_code
WHERE p.pipeline_code = 'OFFICIAL_STAFF_2026' AND p.pipeline_version = 2 AND p.delete_flag = 0
  AND p.stage > 7
  AND (c.taxable_flag = 1 OR c.tax_deductible_flag = 1);

-- 5.4 确认 V1 完全未被改动（期望：5 个阶段；个税仍是 stage 4 / 999）
SELECT COUNT(DISTINCT stage) AS v1_stage_cnt
FROM salary_calc_pipeline_step
WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND pipeline_version = 1 AND delete_flag = 0;
SELECT stage, sort_order, rule_code
FROM salary_calc_pipeline_step
WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND pipeline_version = 1
  AND rule_code IN ('AUTO_TAX_CALC', 'PREV_MONTH_ADJUSTMENT');

-- ---------------------------------------------------------------------------
-- 6. 回滚（逻辑删除，符合实体 @TableLogic(delval = "id") 约定）
-- ---------------------------------------------------------------------------
-- 6.1 默认切回 V1
-- UPDATE salary_calc_pipeline_info SET default_flag = 0 WHERE delete_flag = 0;
-- UPDATE salary_calc_pipeline_info SET default_flag = 1
--   WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND version = 1 AND delete_flag = 0;
-- 6.2 逻辑删除 V2（delete_flag = 自己的 id）
-- UPDATE salary_calc_pipeline_step SET delete_flag = id
--   WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND pipeline_version = 2;
-- UPDATE salary_calc_pipeline_info SET delete_flag = id
--   WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND version = 2;
-- 6.3（可选）物理删除
-- DELETE FROM salary_calc_pipeline_step
--   WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND pipeline_version = 2;
-- DELETE FROM salary_calc_pipeline_info
--   WHERE pipeline_code = 'OFFICIAL_STAFF_2026' AND version = 2;

-- ---------------------------------------------------------------------------
-- 7.（可选）同步 stage 列注释，反映 S1-S9 阶段语义
-- ---------------------------------------------------------------------------
-- ALTER TABLE salary_calc_pipeline_step MODIFY COLUMN stage tinyint NOT NULL
--   COMMENT '阶段(S1基础/考勤 S2绩效/提成 S3津贴/福利 S4奖金/年终 S5补发/追溯 S6税前扣除 S7个税 S8税后扣款/返还 S9公司成本)';
