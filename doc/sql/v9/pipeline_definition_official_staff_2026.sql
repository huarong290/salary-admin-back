-- ============================================================================
-- OFFICIAL_STAFF_2026 薪资计算管道 —— 完整定义脚本（info + 62 steps）
-- ----------------------------------------------------------------------------
-- 文件    : doc/sql/v9/pipeline_definition_official_staff_2026.sql
-- 目标库  : salary_admin_v9
-- 影响表  : salary_calc_pipeline_info / salary_calc_pipeline_step
-- 用途    : 在任意环境完整重建该管道定义（全新库初始化 / 重置现有定义）
-- 特性    : 自包含（不依赖现有数据）、可重复执行（先按 code+version 清理再写入）
-- ----------------------------------------------------------------------------
-- 血缘关系（三个脚本的分工）：
--   · pipeline_definition_official_staff_2026.sql  本脚本：全量定义，环境无关
--   · pipeline_reorder_official_staff_2026.sql     增量：原地重排 V1
--   · pipeline_upgrade_v2_official_staff_2026.sql  增量：从 V1 复制出 V2 并切换默认
-- ----------------------------------------------------------------------------
-- 编排设计（个税为分水岭，见 doc/calc-pipeline-spec.md）：
--   S1 基础工资与考勤   S2 绩效与提成     S3 津贴与福利    S4 奖金与年终
--   S5 补发与追溯       S6 税前扣除       S7 个税(分水岭)  S8 税后扣款与返还
--   S9 公司成本
--   铁律：taxable_flag=1 / tax_deductible_flag=1 的项一律早于个税；税后项晚于个税
-- ----------------------------------------------------------------------------
-- 引擎排序规则：ORDER BY stage ASC, sort_order ASC
-- 步骤字段约定：rule_type 恒为 1(公式)；condition_script 恒为 NULL；
--              仅 BASE_SALARY / AUTO_TAX_CALC 为 block_flag=1 + skip_if_null=0，其余相反
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 可调参数
-- ---------------------------------------------------------------------------
SET @pipeline_code    = 'OFFICIAL_STAFF_2026';
SET @pipeline_version = 1;   -- 需要新版本时改为 2（同时修改下方 pipeline_name / remark）

-- ---------------------------------------------------------------------------
-- 0. 备份现有定义（可回滚）
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS salary_calc_pipeline_info_bak;
CREATE TABLE salary_calc_pipeline_info_bak AS
SELECT * FROM salary_calc_pipeline_info
WHERE pipeline_code = @pipeline_code AND version = @pipeline_version;

DROP TABLE IF EXISTS salary_calc_pipeline_step_bak;
CREATE TABLE salary_calc_pipeline_step_bak AS
SELECT * FROM salary_calc_pipeline_step
WHERE pipeline_code = @pipeline_code AND pipeline_version = @pipeline_version;
-- 预期备份行数：info 1 行、step 62 行（全新库为 0 行）
SELECT (SELECT COUNT(*) FROM salary_calc_pipeline_info_bak) AS bak_info_rows,
       (SELECT COUNT(*) FROM salary_calc_pipeline_step_bak) AS bak_step_rows;

-- ---------------------------------------------------------------------------
-- 1. 清理旧定义（按 code + version 重建，保证脚本可重复执行）
-- ---------------------------------------------------------------------------
DELETE FROM salary_calc_pipeline_step
WHERE pipeline_code = @pipeline_code AND pipeline_version = @pipeline_version;

DELETE FROM salary_calc_pipeline_info
WHERE pipeline_code = @pipeline_code AND version = @pipeline_version;

-- ---------------------------------------------------------------------------
-- 2. 写入管道主表（default_flag 先置 0，第 4 步统一设置唯一默认）
-- ---------------------------------------------------------------------------
INSERT INTO salary_calc_pipeline_info
    (pipeline_code, pipeline_name, version, default_flag, status, remark)
VALUES
    (@pipeline_code,
     '2026年度正式员工核算流',
     @pipeline_version,
     0,
     1,
     '个税适配编排 S1-S9：应税收入与税前扣除项全部前置到个税之前，税后项后置');

-- ---------------------------------------------------------------------------
-- 3. 写入 62 个步骤（stage / sort_order 已按新编排内联）
-- ---------------------------------------------------------------------------
INSERT INTO salary_calc_pipeline_step
    (pipeline_code, pipeline_version, rule_code, rule_name, rule_type, condition_script,
     stage, sort_order, block_flag, skip_if_null, status)
VALUES
    (@pipeline_code, @pipeline_version, 'BASE_SALARY', '基本工资', 1, NULL, 1, 10, 1, 0, 1),
    (@pipeline_code, @pipeline_version, 'OVERTIME_PAY_DAY', '日加班工资', 1, NULL, 1, 20, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'OVERTIME_PAY_HOUR', '时加班工资', 1, NULL, 1, 30, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ATTENDANCE_BONUS', '全勤奖', 1, NULL, 1, 40, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'KPI_BONUS', 'KPI绩效', 1, NULL, 2, 100, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'COMMISSION_SALES', '业绩提成', 1, NULL, 2, 110, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'COMMISSION_AGENT', '代理提成', 1, NULL, 2, 120, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'HOUSING_ALLOW', '住房补贴', 1, NULL, 3, 200, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'MEAL_ALLOW', '餐补', 1, NULL, 3, 210, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'SHIFT_12H_ALLOWANCE', '12小时补贴', 1, NULL, 3, 220, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'QUARANTINE_ALLOWANCE', '隔离补贴', 1, NULL, 3, 230, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'OTHER_ALLOWANCE', '其他补贴', 1, NULL, 3, 240, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'SAFETY_CARD_BONUS', '安全卡奖励', 1, NULL, 3, 250, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_LEAVE_BONUS', '年假奖金', 1, NULL, 3, 260, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'REFERRAL_BONUS', '内推奖金', 1, NULL, 3, 270, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'BIRTHDAY_BONUS', '生日礼金', 1, NULL, 3, 280, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'FESTIVAL_SPRING_GIFT', '春节福利', 1, NULL, 3, 290, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'FESTIVAL_SPRING_BONUS', '春节礼金', 1, NULL, 3, 300, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'FESTIVAL_DRAGON_BOAT_GIFT', '端午节福利', 1, NULL, 3, 310, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'FESTIVAL_DRAGON_BOAT_BONUS', '端午节礼金', 1, NULL, 3, 320, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'FESTIVAL_MID_AUTUMN_GIFT', '中秋节福利', 1, NULL, 3, 330, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'FESTIVAL_MID_AUTUMN_BONUS', '中秋节礼金', 1, NULL, 3, 340, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'EVENT_EURO_CUP', '欧洲杯激励奖金', 1, NULL, 3, 350, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'EVENT_WORLD_CUP', '世界杯激励奖金', 1, NULL, 3, 360, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_13', '年终奖13薪', 1, NULL, 4, 400, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_13_5', '年终奖13.5薪', 1, NULL, 4, 410, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_14', '年终奖14薪', 1, NULL, 4, 420, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_14_5', '年终奖14.5薪', 1, NULL, 4, 430, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_15', '年终奖15薪', 1, NULL, 4, 440, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_15_5', '年终奖15.5薪', 1, NULL, 4, 450, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_16', '年终奖16薪', 1, NULL, 4, 460, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_16_5', '年终奖16.5薪', 1, NULL, 4, 470, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_17', '年终奖17薪', 1, NULL, 4, 480, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_17_5', '年终奖17.5薪', 1, NULL, 4, 490, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_18', '年终奖18薪', 1, NULL, 4, 500, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_18_5', '年终奖18.5薪', 1, NULL, 4, 510, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ANNUAL_BONUS_19', '年终奖19薪', 1, NULL, 4, 520, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'LOYALTY_BONUS_2Y', '忠诚奖金(二年度)', 1, NULL, 4, 530, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'LOYALTY_BONUS_5Y', '忠诚奖金(五年度)', 1, NULL, 4, 540, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'LOYALTY_BONUS_10Y', '忠诚奖金(十年度)', 1, NULL, 4, 550, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ATTENDANCE_REISSUE', '考勤/薪资补发', 1, NULL, 5, 600, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'PREV_MONTH_ADJUSTMENT', '上月补发/续扣', 1, NULL, 5, 610, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'SI_REISSUE_IND', '个人社保退费/补发', 1, NULL, 5, 620, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'EXPENSE_REIMBURSE_ONBOARD', '入职费用报销', 1, NULL, 5, 630, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'SI_PENSION_IND', '养老保险(个人)', 1, NULL, 6, 700, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'SI_MED_IND', '医疗保险(个人)', 1, NULL, 6, 710, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'SI_HOUSING_IND', '公积金(个人)', 1, NULL, 6, 720, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ABSENT_DEDUCTION', '缺勤扣款', 1, NULL, 6, 730, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'LATE_DEDUCTION', '迟到早退扣款', 1, NULL, 6, 740, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'AUTO_TAX_CALC', '智能个税核算', 1, NULL, 7, 900, 1, 0, 1),
    (@pipeline_code, @pipeline_version, 'UTILITY_DEDUCTION', '水电网扣款', 1, NULL, 8, 1000, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'FINE_DEDUCTION', '管理罚款', 1, NULL, 8, 1010, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'PASSPORT_FEE_DEDUCTION', '护照费用代扣', 1, NULL, 8, 1020, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'DEPOSIT_DEDUCTION_CURRENT', '本月押金扣除', 1, NULL, 8, 1030, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'OTHER_DEDUCTION', '其他扣除', 1, NULL, 8, 1040, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'RESIGNATION_SETTLEMENT', '离职费用结算', 1, NULL, 8, 1050, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'DEPOSIT_REFUND_CURRENT', '本月押金返还', 1, NULL, 8, 1060, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'FINE_REBATE', '管理罚款返还', 1, NULL, 8, 1070, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'UTILITY_REBATE', '水电网费返还', 1, NULL, 8, 1080, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'PASSPORT_FEE_REBATE', '护照费用返还', 1, NULL, 8, 1090, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ER_PENSION_COMP', '养老保险(公司)', 1, NULL, 9, 1100, 0, 1, 1),
    (@pipeline_code, @pipeline_version, 'ER_VISA_COMP', '海外签证费用', 1, NULL, 9, 1110, 0, 1, 1);

-- ---------------------------------------------------------------------------
-- 4. 设置全局唯一默认管道（与 setDefaultPipeline 行为一致）
-- ---------------------------------------------------------------------------
UPDATE salary_calc_pipeline_info SET default_flag = 0 WHERE delete_flag = 0;

UPDATE salary_calc_pipeline_info SET default_flag = 1
WHERE pipeline_code = @pipeline_code AND version = @pipeline_version AND delete_flag = 0;

-- ---------------------------------------------------------------------------
-- 5. 验证
-- ---------------------------------------------------------------------------
-- 5.1 管道主表
SELECT id, pipeline_code, pipeline_name, version, default_flag, status
FROM salary_calc_pipeline_info
WHERE pipeline_code = @pipeline_code
ORDER BY version;

-- 5.2 步骤总数与阶段分布（期望：62 步 / 9 个阶段 / 个税落在 stage 7）
SELECT COUNT(*) AS total_steps FROM salary_calc_pipeline_step
WHERE pipeline_code = @pipeline_code AND pipeline_version = @pipeline_version AND delete_flag = 0;

SELECT stage, COUNT(*) AS steps, MIN(sort_order) AS min_sort, MAX(sort_order) AS max_sort
FROM salary_calc_pipeline_step
WHERE pipeline_code = @pipeline_code AND pipeline_version = @pipeline_version AND delete_flag = 0
GROUP BY stage ORDER BY stage;

-- 5.3 铁律校验：个税之后不应再出现"影响税基"的项（期望 0 行）
SELECT p.stage, p.sort_order, p.rule_code, c.taxable_flag, c.tax_deductible_flag
FROM salary_calc_pipeline_step p
JOIN salary_item_config c ON c.item_code = p.rule_code
WHERE p.pipeline_code = @pipeline_code AND p.pipeline_version = @pipeline_version
  AND p.delete_flag = 0
  AND p.stage > 7
  AND (c.taxable_flag = 1 OR c.tax_deductible_flag = 1);

-- 5.4 每个步骤都能匹配到项目配置（期望 matched = total）
SELECT COUNT(*) AS total,
       SUM(c.id IS NOT NULL) AS matched
FROM salary_calc_pipeline_step p
LEFT JOIN salary_item_config c ON c.item_code = p.rule_code
WHERE p.pipeline_code = @pipeline_code AND p.pipeline_version = @pipeline_version
  AND p.delete_flag = 0;

-- 5.5 个税步骤位置（期望 stage 7 / sort_order 900 / block_flag 1）
SELECT stage, sort_order, rule_code, block_flag, skip_if_null
FROM salary_calc_pipeline_step
WHERE pipeline_code = @pipeline_code AND pipeline_version = @pipeline_version
  AND rule_code = 'AUTO_TAX_CALC';

-- ---------------------------------------------------------------------------
-- 6. 回滚（从第 0 步的备份表恢复）
-- ---------------------------------------------------------------------------
-- DELETE FROM salary_calc_pipeline_step
--   WHERE pipeline_code = @pipeline_code AND pipeline_version = @pipeline_version;
-- DELETE FROM salary_calc_pipeline_info
--   WHERE pipeline_code = @pipeline_code AND version = @pipeline_version;
--
-- INSERT INTO salary_calc_pipeline_info SELECT * FROM salary_calc_pipeline_info_bak;
-- INSERT INTO salary_calc_pipeline_step SELECT * FROM salary_calc_pipeline_step_bak;

-- ---------------------------------------------------------------------------
-- 7.（可选）同步 stage 列注释，反映 S1-S9 阶段语义
-- ---------------------------------------------------------------------------
-- ALTER TABLE salary_calc_pipeline_step MODIFY COLUMN stage tinyint NOT NULL
--   COMMENT '阶段(S1基础/考勤 S2绩效/提成 S3津贴/福利 S4奖金/年终 S5补发/追溯 S6税前扣除 S7个税 S8税后扣款/返还 S9公司成本)';
