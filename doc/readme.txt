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