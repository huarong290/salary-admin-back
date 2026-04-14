-- ============================================================================
-- 模块 2：薪资项目配置明细表 (Salary Items Config)
-- ============================================================================

-- [2.1 清空旧数据防重复]
TRUNCATE TABLE `salary_item_config`;

-- [2.2 插入全量配置数据]
INSERT INTO `salary_item_config`
(`item_code`, `item_name`, `item_category`, `category_dict_value`, `env_var_name`, `calc_priority`, `taxable_flag`, `tax_deductible_flag`, `fixed_flag`, `pinyin_code`, `sort_value`, `remark`)
VALUES
-- 【1】收入类 - 档案固定项 (Fixed Items)
('BASE_SALARY',          '基本工资',     1, 'INC_BASE',          'baseSalary',          10, 1, 0, 1, 'jbgz',   10, '核心底薪'),
('HOUSING_ALLOW',        '住房补贴',     1, 'INC_HOUSING_ALLOW', 'housingAllow',        11, 1, 0, 1, 'zfbt',   11, '每月固定房补'),
('MEAL_ALLOW',           '餐补',         1, 'INC_MEAL_ALLOW',    'mealAllow',           12, 0, 0, 1, 'cb',     12, '固定餐补'),
('SHIFT_12H_ALLOWANCE',  '12小时补贴',   1, 'INC_SHIFT_ALLOW',   'shift12hAllowance',   13, 1, 0, 1, '12xsbt', 13, '特殊排班补贴'),
('QUARANTINE_ALLOWANCE', '隔离补贴',     1, 'INC_SUBSIDY',       'quarantineAllowance', 14, 1, 0, 0, 'glbt',   14, '特殊隔离补贴'),
('OTHER_ALLOWANCE',      '其他补贴',     1, 'INC_OTHER',         'otherAllowance',      19, 1, 0, 0, 'qtbt',   19, '非固定通用补贴'),

-- 【2】收入类 - 动态变动项 (Attendance & Performance)
('OVERTIME_PAY_DAY',     '日加班工资',   1, 'INC_OVERTIME',      'overtimePayDay',      20, 1, 0, 0, 'rjbgz',  20, '按天加班费'),
('OVERTIME_PAY_HOUR',    '时加班工资',   1, 'INC_OVERTIME',      'overtimePayHour',     21, 1, 0, 0, 'sjbgz',  21, '按时加班费'),
('KPI_BONUS',            'KPI绩效',      1, 'INC_BONUS',         'kpiBonus',            30, 1, 0, 0, 'kpi',    30, '月度绩效'),
('COMMISSION_SALES',     '业绩提成',     1, 'INC_BONUS',         'commissionSales',     31, 1, 0, 0, 'yjtc',   31, '业务提成'),
('COMMISSION_AGENT',     '代理提成',     1, 'INC_BONUS',         'commissionAgent',     32, 1, 0, 0, 'dltc',   32, '代理提成'),
('ATTENDANCE_BONUS',     '全勤奖',       1, 'INC_ATTENDANCE',    'attendanceBonus',     40, 1, 0, 0, 'qqj',    40, '全勤奖金'),
('ATTENDANCE_REISSUE',   '考勤/薪资补发',1, 'INC_ATTENDANCE',    'attendanceReissue',   41, 1, 0, 0, 'kqbf',   41, '漏打卡或考勤误差补发'),

-- 【3】各类奖励与节日福利 (Bonus & Festival)
('SAFETY_CARD_BONUS',    '安全卡奖励',   1, 'INC_OTHER',         'safetyCardBonus',     50, 1, 0, 0, 'aqkjl',  50, '安全奖励'),
('ANNUAL_LEAVE_BONUS',   '年假奖金',     1, 'INC_OTHER',         'annualLeaveBonus',    51, 1, 0, 0, 'njjj',   51, '年假折现'),
('REFERRAL_BONUS',       '内推奖金',     1, 'INC_OTHER',         'referralBonus',       52, 1, 0, 0, 'ntjj',   52, '内推奖励'),
('BIRTHDAY_BONUS',       '生日礼金',     1, 'INC_FESTIVAL',      'birthdayBonus',       53, 0, 0, 0, 'srlj',   53, '生日福利'),

-- 节日现金/实物
('FESTIVAL_SPRING_GIFT',       '春节福利',   1, 'INC_FESTIVAL',  'festivalSpringGift',      60, 1, 0, 0, 'cjfw', 60, '实物'),
('FESTIVAL_SPRING_BONUS',      '春节礼金',   1, 'INC_FESTIVAL',  'festivalSpringBonus',     61, 1, 0, 0, 'cjlj', 61, '现金'),
('FESTIVAL_DRAGON_BOAT_GIFT',  '端午节福利', 1, 'INC_FESTIVAL',  'festivalDragonBoatGift',  62, 1, 0, 0, 'dwfw', 62, '实物'),
('FESTIVAL_DRAGON_BOAT_BONUS', '端午节礼金', 1, 'INC_FESTIVAL',  'festivalDragonBoatBonus', 63, 1, 0, 0, 'dwlj', 63, '现金'),
('FESTIVAL_MID_AUTUMN_GIFT',   '中秋节福利', 1, 'INC_FESTIVAL',  'festivalMidAutumnGift',   64, 1, 0, 0, 'zqfw', 64, '实物'),
('FESTIVAL_MID_AUTUMN_BONUS',  '中秋节礼金', 1, 'INC_FESTIVAL',  'festivalMidAutumnBonus',  65, 1, 0, 0, 'zqlj', 65, '现金'),

-- 赛事激励
('EVENT_EURO_CUP',       '欧洲杯奖金',   1, 'INC_BONUS',     'eventEuroCup',        70, 1, 0, 0, 'ozb',    70, '欧洲杯激励奖金'),
('EVENT_WORLD_CUP',      '世界杯奖金',   1, 'INC_BONUS',     'eventWorldCup',       71, 1, 0, 0, 'sjb',    71, '世界杯激励奖金'),

-- 年终奖系列 (已统一小驼峰规范)
('ANNUAL_BONUS_13',      '年终奖13薪',   1, 'INC_YEAR_END',  'annualBonus13',       77, 1, 0, 0, 'nzj13',  77, '13薪'),
('ANNUAL_BONUS_13_5',    '年终奖13.5薪', 1, 'INC_YEAR_END',  'annualBonus135',      78, 1, 0, 0, 'nzj135', 78, '13.5薪'),
('ANNUAL_BONUS_14',      '年终奖14薪',   1, 'INC_YEAR_END',  'annualBonus14',       79, 1, 0, 0, 'nzj14',  79, '14薪'),
('ANNUAL_BONUS_14_5',    '年终奖14.5薪', 1, 'INC_YEAR_END',  'annualBonus145',      80, 1, 0, 0, 'nzj145', 80, '14.5薪'),
('ANNUAL_BONUS_15',      '年终奖15薪',   1, 'INC_YEAR_END',  'annualBonus15',       81, 1, 0, 0, 'nzj15',  81, '15薪'),
('ANNUAL_BONUS_15_5',    '年终奖15.5薪', 1, 'INC_YEAR_END',  'annualBonus155',      82, 1, 0, 0, 'nzj155', 82, '15.5薪'),
('ANNUAL_BONUS_16',      '年终奖16薪',   1, 'INC_YEAR_END',  'annualBonus16',       83, 1, 0, 0, 'nzj16',  83, '16薪'),
('ANNUAL_BONUS_16_5',    '年终奖16.5薪', 1, 'INC_YEAR_END',  'annualBonus165',      84, 1, 0, 0, 'nzj165', 84, '16.5薪'),
('ANNUAL_BONUS_17',      '年终奖17薪',   1, 'INC_YEAR_END',  'annualBonus17',       85, 1, 0, 0, 'nzj17',  85, '17薪'),
('ANNUAL_BONUS_17_5',    '年终奖17.5薪', 1, 'INC_YEAR_END',  'annualBonus175',      86, 1, 0, 0, 'nzj175', 86, '17.5薪'),
('ANNUAL_BONUS_18',      '年终奖18薪',   1, 'INC_YEAR_END',  'annualBonus18',       87, 1, 0, 0, 'nzj18',  87, '18薪'),
('ANNUAL_BONUS_18_5',    '年终奖18.5薪', 1, 'INC_YEAR_END',  'annualBonus185',      88, 1, 0, 0, 'nzj185', 88, '18.5薪'),
('ANNUAL_BONUS_19',      '年终奖19薪',   1, 'INC_YEAR_END',  'annualBonus19',       89, 1, 0, 0, 'nzj19',  89, '19薪'),

-- 忠诚奖
('LOYALTY_BONUS_2Y',     '忠诚奖金(2年)',1, 'INC_BONUS',     'loyaltyBonus2y',      90, 1, 0, 0, 'zcj2',   90, '满2年'),
('LOYALTY_BONUS_5Y',     '忠诚奖金(5年)',1, 'INC_BONUS',     'loyaltyBonus5y',      91, 1, 0, 0, 'zcj5',   91, '满5年'),
('LOYALTY_BONUS_10Y',    '忠诚奖金(10年)',1,'INC_BONUS',     'loyaltyBonus10y',     92, 1, 0, 0, 'zcj10',  92, '满10年'),

-- 【4】返还/报销项 (Rebate/Reimbursement)
('EXPENSE_REIMB_ONBOARD','入职费用报销', 1, 'INC_OTHER',     'expenseReimbOnboard', 95, 0, 0, 0, 'rzbx',   95, '免税报销'),
('DEPOSIT_REFUND_CURR',  '本月押金返还', 1, 'INC_OTHER',     'depositRefundCurr',   96, 0, 0, 0, 'yjfh',   96, '对应押金扣除'),
('FINE_REBATE',          '管理罚款返还', 1, 'INC_OTHER',     'fineRebate',          97, 0, 0, 0, 'fkfh',   97, '罚款申诉退回'),
('UTILITY_REBATE',       '水电网费返还', 1, 'INC_OTHER',     'utilityRebate',       98, 0, 0, 0, 'sdwfh',  98, '水电费多扣返还'),
('PASSPORT_FEE_REBATE',  '护照费用返还', 1, 'INC_OTHER',     'passportFeeRebate',   99, 0, 0, 0, 'hzfh',   99, '护照费多扣返还'),

-- 【5】扣款类 (Deductions)
('ABSENT_DEDUCTION',     '缺勤扣款',     2, 'DED_ABSENT',    'absentDeduction',    100, 0, 1, 0, 'qqkk',  100, '税前扣'),
('LATE_DEDUCTION',       '迟到早退扣款', 2, 'DED_LATE',      'lateDeduction',      110, 0, 1, 0, 'cdzt',  110, '税前扣'),
('UTILITY_DEDUCTION',    '水电网扣款',   2, 'DED_OTHER',     'utilityDeduction',   120, 0, 0, 0, 'sdwkk', 120, '税后扣'),
('FINE_DEDUCTION',       '管理罚款',     2, 'DED_FINE',      'fineDeduction',      121, 0, 0, 0, 'glfk',  121, '税后扣'),
('PASSPORT_FEE_DEDUCT',  '护照费用代扣', 2, 'DED_OTHER',     'passportFeeDeduct',  122, 0, 0, 0, 'hzdk',  122, '护照费'),
('DEPOSIT_DEDUCT_CURR',  '本月押金扣除', 2, 'DED_OTHER',     'depositDeductCurr',  123, 0, 0, 0, 'byyj',  123, '押金扣'),
('OTHER_DEDUCTION',      '其他扣除',     2, 'DED_OTHER',     'otherDeduction',     124, 0, 0, 0, 'qtkc',  130, '通用非固定扣款'),

-- 【6】系统调整与结算
('RESIGNATION_SETTLE',   '离职费用结算', 2, 'DED_OTHER',     'resignationSettle',  140, 0, 0, 0, 'lzjs',  140, '离职清算扣款'),
('PREV_MONTH_ADJUST',    '上月补发/续扣',1, 'INC_OTHER',     'prevMonthAdjust',    141, 1, 0, 0, 'sybf',  141, '人工调账'),

-- 【7】税费与社保 (Personal SI & Tax - 包含本地化)
('SI_PENSION_IND',       '养老保险(个人)', 3, 'SI_PENSION',    'siPensionInd',       200, 0, 1, 1, 'ylbx',  200, '国内个人养老'),
('SI_MED_IND',           '医疗保险(个人)', 3, 'SI_MED',        'siMedInd',           210, 0, 1, 1, 'ylbx',  210, '国内个人医疗'),
('SI_HOUSING_IND',       '公积金(个人)',   3, 'SI_HOUSING',    'siHousingInd',       220, 0, 1, 1, 'gjj',   220, '国内个人公积金'),
('SI_REISSUE_IND',       '个人社保退补',   1, 'INC_OTHER',     'siReissueInd',       230, 0, 0, 0, 'sbgjj', 230, '社保多扣返还'),

-- 菲律宾强制社保矩阵 (菲律宾劳务/税务标准三件套)
('SI_PHP_SSS_IND',       'SSS(个人)',      3, 'PHP_SSS',       'siPhpSssInd',        240, 0, 1, 1, 'sss',   240, '菲国强制社保'),
('SI_PHP_PHILHEALTH_IND','PhilHealth(个人)',3, 'PHP_PHILHEALTH','siPhpPhilhealthInd', 241, 0, 1, 1, 'ph',    241, '菲国强制医保'),
('SI_PHP_PAGIBIG_IND',   'Pag-IBIG(个人)', 3, 'PHP_PAGIBIG',   'siPhpPagibigInd',    242, 0, 1, 1, 'pi',    242, '菲国强制住房公积金'),

('AUTO_TAX_CALC',        '智能个税核算',   3, 'TAX_INCOME',    'autoTaxCalc',        999, 0, 0, 0, 'zngs',  999, '个税终结节点'),

-- 【8】公司成本 (Employer Cost - 不进个人工资条实发)
('ER_PENSION_COMP',      '养老保险(公司)', 4, 'ER_PENSION',    'erPensionComp',      300, 0, 0, 1, 'ylbx',  300, '公司成本'),
('ER_VISA_COMP',         '海外签证费用',   4, 'ER_VISA',       'erVisaComp',         310, 0, 0, 0, 'qzfy',  310, '公司承担签证'),
('ER_PHP_SSS_COMP',      'SSS(公司)',      4, 'PHP_SSS',       'erPhpSssComp',       320, 0, 0, 1, 'sss',   320, '菲国强制社保成本'),
('ER_PHP_PHILHEALTH_COMP','PhilHealth(公司)',4,'PHP_PHILHEALTH','erPhpPhilhealthComp',321, 0, 0, 1, 'ph',    321, '菲国强制医保成本'),
('ER_PHP_PAGIBIG_COMP',  'Pag-IBIG(公司)', 4, 'PHP_PAGIBIG',   'erPhpPagibigComp',   322, 0, 0, 1, 'pi',    322, '菲国住房公积金成本');