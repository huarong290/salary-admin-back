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
                                                                                                           ('salary_item_sub_type', 'INC_BONUS', '绩效奖金', 40);

-- 扣款类细分
INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`) VALUES
                                                                                                           ('salary_item_sub_type', 'DED_ABSENT', '缺勤扣款', 100),
                                                                                                           ('salary_item_sub_type', 'DED_LATE', '迟到早退', 110),
                                                                                                           ('salary_item_sub_type', 'DED_OTHER', '其他扣款', 120);

-- 统筹与税费 (涉及 PHP 多币种计算的关键项)
INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`) VALUES
                                                                                                           ('salary_item_sub_type', 'TAX_INCOME', '个人所得税', 200),
                                                                                                           ('salary_item_sub_type', 'SI_PENSION', '养老保险(个人)', 210),
                                                                                                           ('salary_item_sub_type', 'SI_MED', '医疗保险(个人)', 220),
                                                                                                           ('salary_item_sub_type', 'PHP_SSS', 'SSS (菲律宾社保)', 230); -- 扩展例子