-- [1.1 保证幂等性：先清理旧字典结构]
DELETE FROM `sys_dict_item` WHERE `dict_type_code` IN ('salary_item_category', 'salary_item_sub_type');
DELETE FROM `sys_dict_type` WHERE `dict_type_code` IN ('salary_item_category', 'salary_item_sub_type');

-- [1.2 插入字典大类]
INSERT INTO `sys_dict_type` (`dict_type_code`, `dict_type_name`, `dict_category`, `remark`)
VALUES
    ('salary_item_category', '薪资项目大类', 'salary', '定义薪资项的顶层财务属性(收入/扣款等)'),
    ('salary_item_sub_type', '薪资项目细类', 'salary', '定义具体的业务逻辑标识，用于代码或脚本识别');

-- [1.3 插入大类明细]
INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`)
VALUES
    ('salary_item_category', '1', '收入', 10),
    ('salary_item_category', '2', '扣款', 20),
    ('salary_item_category', '3', '税费与个人社保', 30),
    ('salary_item_category', '4', '公司支出成本', 40);

-- [1.4 插入细类明细 (严格 1:1 细化分类)]
INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`)
VALUES
-- 收入细项
('salary_item_sub_type', 'INC_BASE', '基本工资', 10),
('salary_item_sub_type', 'INC_HOUSING_ALLOW', '住房补贴', 20),
('salary_item_sub_type', 'INC_MEAL_ALLOW', '餐饮补贴', 21),
('salary_item_sub_type', 'INC_POST_ALLOW', '岗位津贴', 22),
('salary_item_sub_type', 'INC_SHIFT_ALLOW', '排班补贴', 23),
('salary_item_sub_type', 'INC_SUBSIDY', '特殊补贴(隔离等)', 24),
('salary_item_sub_type', 'INC_OVERTIME', '加班工资', 30),
('salary_item_sub_type', 'INC_ATTENDANCE', '考勤奖金', 35),
('salary_item_sub_type', 'INC_BONUS', '绩效与业绩奖金', 40),
('salary_item_sub_type', 'INC_YEAR_END', '年终奖', 50),
('salary_item_sub_type', 'INC_FESTIVAL', '节日福利', 70),
('salary_item_sub_type', 'INC_OTHER', '其他收入', 80),

-- 扣款细项
('salary_item_sub_type', 'DED_ABSENT', '缺勤扣款', 100),
('salary_item_sub_type', 'DED_LATE', '迟到早退', 110),
('salary_item_sub_type', 'DED_FINE', '行政罚款', 115),
('salary_item_sub_type', 'DED_LOAN', '借款扣还', 118),
('salary_item_sub_type', 'DED_OTHER', '其他扣款', 120),

-- 税费与社保
('salary_item_sub_type', 'TAX_INCOME', '个人所得税', 200),
('salary_item_sub_type', 'SI_PENSION', '国内养老保险', 210),
('salary_item_sub_type', 'SI_MED', '国内医疗保险', 220),
('salary_item_sub_type', 'SI_UNEMPLOYMENT', '国内失业保险', 225),
('salary_item_sub_type', 'SI_HOUSING', '国内住房公积金', 230),
('salary_item_sub_type', 'PHP_SSS', 'SSS (菲律宾社保)', 240),
('salary_item_sub_type', 'PHP_PHILHEALTH', 'PhilHealth (菲律宾医保)', 241),
('salary_item_sub_type', 'PHP_PAGIBIG', 'Pag-IBIG (菲律宾公积金)', 242),
('salary_item_sub_type', 'TAX_LOCAL', '地方税', 250),

-- 公司支出
('salary_item_sub_type', 'ER_PENSION', '养老保险(公司缴纳)', 300),
('salary_item_sub_type', 'ER_MED', '医疗保险(公司缴纳)', 310),
('salary_item_sub_type', 'ER_HOUSING', '住房公积金(公司缴纳)', 320),
('salary_item_sub_type', 'ER_VISA', '签证手续费用', 330),
('salary_item_sub_type', 'ER_TRAVEL', '差旅费用', 340),
('salary_item_sub_type', 'ER_INSURANCE', '商业保险', 350),
('salary_item_sub_type', 'ER_OTHER', '其他公司支出', 360);