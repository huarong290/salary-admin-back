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