-- 执行以下 SQL 增加冗余快照字段
ALTER TABLE `salary_archive_item`
ADD COLUMN `type_name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '冗余：收入/扣款项名称快照' AFTER `type_id`,
ADD COLUMN `category_name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '冗余：分类名称快照' AFTER `type_name`;

-- 初始化收入项快照
UPDATE salary_archive_item item
JOIN salary_income_type dict ON item.type_id = dict.id
SET item.type_name = dict.type_name,
    item.category_name = dict.category_name
WHERE item.item_type = 1 AND item.type_name ='';

-- 初始化扣款项快照
UPDATE salary_archive_item item
JOIN salary_deduction_type dict ON item.type_id = dict.id
SET item.type_name = dict.type_name,
    item.category_name = dict.category_name
WHERE item.item_type = 2 AND item.type_name ='';



-- 1. 扩展收入类型表：标记是否计税
ALTER TABLE `salary_income_type`
ADD COLUMN `taxable_flag` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否纳入个税计税基数: 0-否, 1-是';

-- 2. 扩展扣款类型表：标记是否为税前扣除项
ALTER TABLE `salary_deduction_type`
ADD COLUMN `tax_deductible_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否为税前合法扣除项(如五险一金): 0-否, 1-是';

-- 3. 扩展档案主表：增加计税方案
ALTER TABLE `salary_archive`
ADD COLUMN `tax_scheme` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '计税方案: 0-不计税, 1-居民个人所得税, 2-劳务报酬税';

-- 给周期表补充结算币种字段
ALTER TABLE `salary_period` ADD COLUMN `currency` VARCHAR(50) DEFAULT 'CNY' COMMENT '结算币种' AFTER `settlement_month`;
5651.61290323

-- 1. 增加冗余与快照字段
ALTER TABLE `salary_summary`
ADD COLUMN `employee_code` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '员工工号(快照)' AFTER `employee_id`,
ADD COLUMN `employee_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '员工姓名(快照)' AFTER `employee_code`,
ADD COLUMN `period_start_date` DATE DEFAULT NULL COMMENT '周期开始(快照)' AFTER `settlement_month`,
ADD COLUMN `period_end_date` DATE DEFAULT NULL COMMENT '周期结束(快照)' AFTER `period_start_date`;

-- 2. 核心查询性能优化：添加大盘搜索联合索引 (月份 + 员工)
-- 如果之前有重复索引建议先 DROP，确保查询走这个高性能索引
ALTER TABLE `salary_summary` ADD INDEX `idx_summary_month_emp` (`settlement_month`, `employee_id`);

-- 1. 增加冗余结算月份，作为查询和未来分表的片键
ALTER TABLE `salary_payment_record`
ADD COLUMN `settlement_month` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '结算月份(冗余快照)' AFTER `archive_id`;

-- 2. 补齐多币种与支付方式的核心财务字段（针对 DECIMAL 8位高精度优化）
ALTER TABLE `salary_payment_record`
MODIFY COLUMN `exchange_rate` DECIMAL(18, 8) NOT NULL DEFAULT '1.00000000' COMMENT '核算汇率(相对于本位币)',
ADD COLUMN `base_final_salary` DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '折合本位币实发金额' AFTER `exchange_rate`,
ADD COLUMN `payment_method` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '发放方式(银行卡/USDT等)' AFTER `base_final_salary`;

-- 3. 🛡️ 核心防资损锁：防止同一个员工在同一个汇总单下重复生成发薪记录
ALTER TABLE `salary_payment_record`
ADD UNIQUE KEY `uk_summary_emp_delete` (`summary_id`, `employee_id`, `delete_flag`);

-- 4. 增加查询索引
ALTER TABLE `salary_payment_record` ADD INDEX `idx_settlement_month` (`settlement_month`);
8211.550000
关于2月的休假：和人事确认2月的月休是3天；然后春节假是从30日到初六，期间有上班就是+1倍工资，如果是选择休息就是带薪。

春节 休2天以上（不含正常月休3天） 要走oa


8211.550000

5840
1168

300

5651.61
1130.32
290.32
7308

7072.258064

753.54
300

5840
4380
1168
300
625.71428

10036.77
9060
430
376.77

7728

560

8237-8167=70