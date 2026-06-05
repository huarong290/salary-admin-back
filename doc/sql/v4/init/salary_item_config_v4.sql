INSERT INTO salary_item_config (item_code,item_name,item_category,category_dict_value,env_var_name,default_rule_script,calc_priority,decimal_places,rounding_mode,taxable_flag,tax_deductible_flag,fixed_flag,pinyin_code,sort_value,status,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('BASE_SALARY','基本工资',1,'INC_BASE','baseSalary',NULL,10,2,'HALF_UP',1,0,1,'jbgz',10,1,'核心底薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('HOUSING_ALLOW','住房补贴',1,'INC_ALLOWANCE','housingAllow',NULL,11,2,'HALF_UP',1,0,1,'zfbt',11,1,'每月固定房补',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('MEAL_ALLOW','餐补',1,'INC_ALLOWANCE','mealAllow',NULL,12,2,'HALF_UP',0,0,1,'cb',12,1,'固定餐补',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('SHIFT_12H_ALLOWANCE','12小时补贴',1,'INC_ALLOWANCE','shift12hAllowance',NULL,13,2,'HALF_UP',1,0,1,'12xsbt',13,1,'特殊排班补贴',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('QUARANTINE_ALLOWANCE','隔离补贴',1,'INC_SUBSIDY','quarantineAllowance',NULL,14,2,'HALF_UP',1,0,0,'glbt',14,1,'特殊隔离补贴',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OTHER_ALLOWANCE','其他补贴',1,'INC_ALLOWANCE','otherAllowance',NULL,19,2,'HALF_UP',1,0,0,'qtbt',19,1,'非固定通用补贴',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OVERTIME_PAY_DAY','日加班工资',1,'INC_OVERTIME','overtimePayDay',NULL,20,2,'HALF_UP',1,0,0,'rjbgz',20,1,'按天加班费',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OVERTIME_PAY_HOUR','时加班工资',1,'INC_OVERTIME','overtimePayHour',NULL,21,2,'HALF_UP',1,0,0,'sjbgz',21,1,'按时加班费',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('KPI_BONUS','KPI绩效',1,'INC_BONUS','kpiBonus',NULL,30,2,'HALF_UP',1,0,0,'kpi',30,1,'月度绩效',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('COMMISSION_SALES','业绩提成',1,'INC_BONUS','commissionSales',NULL,31,2,'HALF_UP',1,0,0,'yjtc',31,1,'业务提成',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_item_config (item_code,item_name,item_category,category_dict_value,env_var_name,default_rule_script,calc_priority,decimal_places,rounding_mode,taxable_flag,tax_deductible_flag,fixed_flag,pinyin_code,sort_value,status,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('COMMISSION_AGENT','代理提成',1,'INC_BONUS','commissionAgent',NULL,32,2,'HALF_UP',1,0,0,'dltc',32,1,'代理提成',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ATTENDANCE_BONUS','全勤奖',1,'INC_ATTENDANCE','attendanceBonus',NULL,40,2,'HALF_UP',1,0,0,'qqj',40,1,'全勤奖金',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ATTENDANCE_REISSUE','考勤/薪资补发',1,'INC_ATTENDANCE','attendanceReissue',NULL,41,2,'HALF_UP',1,0,0,'kqbf',41,1,'漏打卡或考勤误差补发',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('SAFETY_CARD_BONUS','安全卡奖励',1,'INC_OTHER','safetyCardBonus',NULL,50,2,'HALF_UP',1,0,0,'aqkjl',50,1,'安全奖励',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_LEAVE_BONUS','年假奖金',1,'INC_OTHER','annualLeaveBonus',NULL,51,2,'HALF_UP',1,0,0,'njjj',51,1,'年假折现',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('REFERRAL_BONUS','内推奖金',1,'INC_OTHER','referralBonus',NULL,52,2,'HALF_UP',1,0,0,'ntjj',52,1,'内推奖励',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('BIRTHDAY_BONUS','生日礼金',1,'INC_FESTIVAL','birthdayBonus',NULL,53,2,'HALF_UP',0,0,0,'srlj',53,1,'生日福利',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FESTIVAL_SPRING_GIFT','春节福利',1,'INC_FESTIVAL','festivalSpringGift',NULL,60,2,'HALF_UP',1,0,0,'cjfw',60,1,'实物',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FESTIVAL_SPRING_BONUS','春节礼金',1,'INC_FESTIVAL','festivalSpringBonus',NULL,61,2,'HALF_UP',1,0,0,'cjlj',61,1,'现金',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FESTIVAL_DRAGON_BOAT_GIFT','端午节福利',1,'INC_FESTIVAL','festivalDragonBoatGift',NULL,62,2,'HALF_UP',1,0,0,'dwfw',62,1,'实物',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_item_config (item_code,item_name,item_category,category_dict_value,env_var_name,default_rule_script,calc_priority,decimal_places,rounding_mode,taxable_flag,tax_deductible_flag,fixed_flag,pinyin_code,sort_value,status,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('FESTIVAL_DRAGON_BOAT_BONUS','端午节礼金',1,'INC_FESTIVAL','festivalDragonBoatBonus',NULL,63,2,'HALF_UP',1,0,0,'dwlj',63,1,'现金',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FESTIVAL_MID_AUTUMN_GIFT','中秋节福利',1,'INC_FESTIVAL','festivalMidAutumnGift',NULL,64,2,'HALF_UP',1,0,0,'zqfw',64,1,'实物',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FESTIVAL_MID_AUTUMN_BONUS','中秋节礼金',1,'INC_FESTIVAL','festivalMidAutumnBonus',NULL,65,2,'HALF_UP',1,0,0,'zqlj',65,1,'现金',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('EVENT_EURO_CUP','欧洲杯激励奖金',1,'INC_BONUS','eventEuroCup',NULL,70,2,'HALF_UP',1,0,0,'ozb',70,1,'欧洲杯奖金',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('EVENT_WORLD_CUP','世界杯激励奖金',1,'INC_BONUS','eventWorldCup',NULL,71,2,'HALF_UP',1,0,0,'sjb',71,1,'世界杯奖金',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_13','年终奖13薪',1,'INC_YEAR_END','annualBonus13',NULL,77,2,'HALF_UP',1,0,0,'nzj13',77,1,'13薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_13_5','年终奖13.5薪',1,'INC_YEAR_END','annualBonus135',NULL,78,2,'HALF_UP',1,0,0,'nzj135',78,1,'13.5薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_14','年终奖14薪',1,'INC_YEAR_END','annualBonus14',NULL,79,2,'HALF_UP',1,0,0,'nzj14',79,1,'14薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_14_5','年终奖14.5薪',1,'INC_YEAR_END','annualBonus14_5',NULL,80,2,'HALF_UP',1,0,0,'nzj14.5',80,1,'14.5薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_15','年终奖15薪',1,'INC_YEAR_END','annualBonus15',NULL,81,2,'HALF_UP',1,0,0,'nzj15',81,1,'15薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_item_config (item_code,item_name,item_category,category_dict_value,env_var_name,default_rule_script,calc_priority,decimal_places,rounding_mode,taxable_flag,tax_deductible_flag,fixed_flag,pinyin_code,sort_value,status,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('ANNUAL_BONUS_15_5','年终奖15.5薪',1,'INC_YEAR_END','annualBonus15_5',NULL,82,2,'HALF_UP',1,0,0,'nzj15.5',82,1,'15.5薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_16','年终奖16薪',1,'INC_YEAR_END','annualBonus16',NULL,83,2,'HALF_UP',1,0,0,'nzj16',83,1,'16薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_16_5','年终奖16.5薪',1,'INC_YEAR_END','annualBonus16_5',NULL,84,2,'HALF_UP',1,0,0,'nzj16.5',84,1,'16.5薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_17','年终奖17薪',1,'INC_YEAR_END','annualBonus17',NULL,85,2,'HALF_UP',1,0,0,'nzj17',85,1,'17薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_17_5','年终奖17.5薪',1,'INC_YEAR_END','annualBonus17_5',NULL,86,2,'HALF_UP',1,0,0,'nzj17.5',86,1,'17.5薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_18','年终奖18薪',1,'INC_YEAR_END','annualBonus18',NULL,87,2,'HALF_UP',1,0,0,'nzj18',87,1,'18薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_18_5','年终奖18.5薪',1,'INC_YEAR_END','annualBonus185',NULL,88,2,'HALF_UP',1,0,0,'nzj185',88,1,'18.5薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_19','年终奖19薪',1,'INC_YEAR_END','annualBonus19',NULL,89,2,'HALF_UP',1,0,0,'nzj19',89,1,'19薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('LOYALTY_BONUS_2Y','忠诚奖金(二年度)',1,'INC_BONUS','loyaltyBonus2y',NULL,90,2,'HALF_UP',1,0,0,'zcj2',90,1,'满2年',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('LOYALTY_BONUS_5Y','忠诚奖金(五年度)',1,'INC_BONUS','loyaltyBonus5y',NULL,91,2,'HALF_UP',1,0,0,'zcj5',91,1,'满5年',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_item_config (item_code,item_name,item_category,category_dict_value,env_var_name,default_rule_script,calc_priority,decimal_places,rounding_mode,taxable_flag,tax_deductible_flag,fixed_flag,pinyin_code,sort_value,status,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('LOYALTY_BONUS_10Y','忠诚奖金(十年度)',1,'INC_BONUS','loyaltyBonus10y',NULL,92,2,'HALF_UP',1,0,0,'zcj10',92,1,'满10年',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('EXPENSE_REIMBURSE_ONBOARD','入职费用报销',1,'INC_OTHER','expenseReimburseOnboard',NULL,95,2,'HALF_UP',0,0,0,'rzbx',95,1,'免税报销',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('DEPOSIT_REFUND_CURRENT','本月押金返还',1,'INC_OTHER','depositRefundCurrent',NULL,96,2,'HALF_UP',0,0,0,'yjfh',96,1,'对应押金扣除',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FINE_REBATE','管理罚款返还',1,'INC_OTHER','fineRebate',NULL,97,2,'HALF_UP',0,0,0,'fkfh',97,1,'罚款申诉退回',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('UTILITY_REBATE','水电网费返还',1,'INC_OTHER','utilityRebate',NULL,98,2,'HALF_UP',0,0,0,'sdwfh',98,1,'水电费多扣返还',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('PASSPORT_FEE_REBATE','护照费用返还',1,'INC_OTHER','passportFeeRebate',NULL,99,2,'HALF_UP',0,0,0,'hzfh',99,1,'护照费多扣返还',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ABSENT_DEDUCTION','缺勤扣款',2,'DED_ABSENT','absentDeduction',NULL,100,2,'HALF_UP',0,1,0,'qqkk',100,1,'税前扣',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('LATE_DEDUCTION','迟到早退扣款',2,'DED_LATE','lateDeduction',NULL,110,2,'HALF_UP',0,1,0,'cdzt',110,1,'税前扣',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('UTILITY_DEDUCTION','水电网扣款',2,'DED_OTHER','utilityDeduction',NULL,120,2,'HALF_UP',0,0,0,'sdwkk',120,1,'税后扣',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FINE_DEDUCTION','管理罚款',2,'DED_FINE','fineDeduction',NULL,121,2,'HALF_UP',0,0,0,'glfk',121,1,'税后扣',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_item_config (item_code,item_name,item_category,category_dict_value,env_var_name,default_rule_script,calc_priority,decimal_places,rounding_mode,taxable_flag,tax_deductible_flag,fixed_flag,pinyin_code,sort_value,status,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('PASSPORT_FEE_DEDUCTION','护照费用代扣',2,'DED_OTHER','passportFeeDeduction',NULL,122,2,'HALF_UP',0,0,0,'hzdk',122,1,'护照费',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('DEPOSIT_DEDUCTION_CURRENT','本月押金扣除',2,'DED_OTHER','depositDeductionCurrent',NULL,123,2,'HALF_UP',0,0,0,'byyj',123,1,'押金扣',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OTHER_DEDUCTION','其他扣除',2,'DED_OTHER','otherDeduction',NULL,124,2,'HALF_UP',0,0,0,'qtkc',130,1,'通用非固定扣款',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('RESIGNATION_SETTLEMENT','离职费用结算',2,'DED_OTHER','resignationSettlement',NULL,140,2,'HALF_UP',0,0,0,'lzjs',140,1,'离职清算扣款',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('PREV_MONTH_ADJUSTMENT','上月补发/续扣',1,'INC_OTHER','prevMonthAdjustment',NULL,141,2,'HALF_UP',1,0,0,'sybf',141,1,'人工调账',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('SI_PENSION_IND','养老保险(个人)',3,'SI_PENSION','siPensionInd',NULL,200,2,'HALF_UP',0,1,1,'ylbx',200,1,'个人养老',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('SI_MED_IND','医疗保险(个人)',3,'SI_MED','siMedInd',NULL,210,2,'HALF_UP',0,1,1,'ylbx',210,1,'个人医疗',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('SI_HOUSING_IND','公积金(个人)',3,'SI_HOUSING','siHousingInd',NULL,220,2,'HALF_UP',0,1,1,'gjj',220,1,'个人公积金',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('SI_REISSUE_IND','个人社保退费/补发',1,'INC_OTHER','siReissueInd',NULL,230,2,'HALF_UP',0,0,0,'sbgjjbf',230,1,'社保多扣返还',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('AUTO_TAX_CALC','智能个税核算',3,'TAX_INCOME','autoTaxCalc',NULL,999,2,'HALF_UP',0,0,0,'zngs',999,1,'个税终结节点',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_item_config (item_code,item_name,item_category,category_dict_value,env_var_name,default_rule_script,calc_priority,decimal_places,rounding_mode,taxable_flag,tax_deductible_flag,fixed_flag,pinyin_code,sort_value,status,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('ER_PENSION_COMP','养老保险(公司)',4,'ER_PENSION','erPensionComp',NULL,300,2,'HALF_UP',0,0,1,'ylbx',300,1,'公司成本',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ER_VISA_COMP','海外签证费用',4,'ER_VISA','erVisaComp',NULL,310,2,'HALF_UP',0,0,0,'qzfy',310,1,'公司承担签证',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
