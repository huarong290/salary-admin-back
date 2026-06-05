INSERT INTO salary_calc_pipeline_step (pipeline_code,pipeline_version,rule_code,rule_name,rule_type,condition_script,stage,sort_order,block_flag,skip_if_null,status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('OFFICIAL_STAFF_2026',1,'BASE_SALARY','基本工资',1,NULL,1,10,1,0,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ATTENDANCE_BONUS','全勤奖',1,NULL,1,20,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'OVERTIME_PAY_DAY','日加班工资',1,NULL,1,30,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'OVERTIME_PAY_HOUR','时加班工资',1,NULL,1,40,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'KPI_BONUS','KPI绩效',1,NULL,1,50,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'COMMISSION_SALES','业绩提成',1,NULL,1,60,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'COMMISSION_AGENT','代理提成',1,NULL,1,70,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ATTENDANCE_REISSUE','考勤/薪资补发',1,NULL,1,80,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'HOUSING_ALLOW','住房补贴',1,NULL,2,100,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'MEAL_ALLOW','餐补',1,NULL,2,110,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_calc_pipeline_step (pipeline_code,pipeline_version,rule_code,rule_name,rule_type,condition_script,stage,sort_order,block_flag,skip_if_null,status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('OFFICIAL_STAFF_2026',1,'SHIFT_12H_ALLOWANCE','12小时补贴',1,NULL,2,120,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'QUARANTINE_ALLOWANCE','隔离补贴',1,NULL,2,130,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'OTHER_ALLOWANCE','其他补贴',1,NULL,2,135,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'SAFETY_CARD_BONUS','安全卡奖励',1,NULL,2,140,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_LEAVE_BONUS','年假奖金',1,NULL,2,150,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'REFERRAL_BONUS','内推奖金',1,NULL,2,160,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'BIRTHDAY_BONUS','生日礼金',1,NULL,2,170,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'FESTIVAL_SPRING_GIFT','春节福利',1,NULL,2,180,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'FESTIVAL_SPRING_BONUS','春节礼金',1,NULL,2,190,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'FESTIVAL_DRAGON_BOAT_GIFT','端午节福利',1,NULL,2,200,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_calc_pipeline_step (pipeline_code,pipeline_version,rule_code,rule_name,rule_type,condition_script,stage,sort_order,block_flag,skip_if_null,status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('OFFICIAL_STAFF_2026',1,'FESTIVAL_DRAGON_BOAT_BONUS','端午节礼金',1,NULL,2,210,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'FESTIVAL_MID_AUTUMN_GIFT','中秋节福利',1,NULL,2,220,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'FESTIVAL_MID_AUTUMN_BONUS','中秋节礼金',1,NULL,2,230,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'EVENT_EURO_CUP','欧洲杯激励奖金',1,NULL,2,240,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'EVENT_WORLD_CUP','世界杯激励奖金',1,NULL,2,250,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_13','年终奖13薪',1,NULL,2,300,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_13_5','年终奖13.5薪',1,NULL,2,301,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_14','年终奖14薪',1,NULL,2,302,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_14_5','年终奖14.5薪',1,NULL,2,303,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_15','年终奖15薪',1,NULL,2,304,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_calc_pipeline_step (pipeline_code,pipeline_version,rule_code,rule_name,rule_type,condition_script,stage,sort_order,block_flag,skip_if_null,status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_15_5','年终奖15.5薪',1,NULL,2,305,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_16','年终奖16薪',1,NULL,2,306,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_16_5','年终奖16.5薪',1,NULL,2,307,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_17','年终奖17薪',1,NULL,2,308,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_17_5','年终奖17.5薪',1,NULL,2,309,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_18','年终奖18薪',1,NULL,2,310,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_18_5','年终奖18.5薪',1,NULL,2,311,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ANNUAL_BONUS_19','年终奖19薪',1,NULL,2,312,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'LOYALTY_BONUS_2Y','忠诚奖金(二年度)',1,NULL,2,320,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'LOYALTY_BONUS_5Y','忠诚奖金(五年度)',1,NULL,2,330,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_calc_pipeline_step (pipeline_code,pipeline_version,rule_code,rule_name,rule_type,condition_script,stage,sort_order,block_flag,skip_if_null,status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('OFFICIAL_STAFF_2026',1,'LOYALTY_BONUS_10Y','忠诚奖金(十年度)',1,NULL,2,340,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ABSENT_DEDUCTION','缺勤扣款',1,NULL,3,400,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'LATE_DEDUCTION','迟到早退扣款',1,NULL,3,410,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'UTILITY_DEDUCTION','水电网扣款',1,NULL,3,420,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'FINE_DEDUCTION','管理罚款',1,NULL,3,430,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'PASSPORT_FEE_DEDUCTION','护照费用代扣',1,NULL,3,440,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'DEPOSIT_DEDUCTION_CURRENT','本月押金扣除',1,NULL,3,450,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'OTHER_DEDUCTION','其他扣除',1,NULL,3,455,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'SI_PENSION_IND','养老保险(个人)',1,NULL,3,460,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'SI_MED_IND','医疗保险(个人)',1,NULL,3,470,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_calc_pipeline_step (pipeline_code,pipeline_version,rule_code,rule_name,rule_type,condition_script,stage,sort_order,block_flag,skip_if_null,status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('OFFICIAL_STAFF_2026',1,'SI_HOUSING_IND','公积金(个人)',1,NULL,3,480,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ER_PENSION_COMP','养老保险(公司)',1,NULL,3,490,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'ER_VISA_COMP','海外签证费用',1,NULL,3,500,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'EXPENSE_REIMBURSE_ONBOARD','入职费用报销',1,NULL,5,600,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'DEPOSIT_REFUND_CURRENT','本月押金返还',1,NULL,5,610,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'FINE_REBATE','管理罚款返还',1,NULL,5,620,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'UTILITY_REBATE','水电网费返还',1,NULL,5,630,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'PASSPORT_FEE_REBATE','护照费用返还',1,NULL,5,640,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'SI_REISSUE_IND','个人社保退费/补发',1,NULL,5,650,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'PREV_MONTH_ADJUSTMENT','上月补发/续扣',1,NULL,5,660,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_calc_pipeline_step (pipeline_code,pipeline_version,rule_code,rule_name,rule_type,condition_script,stage,sort_order,block_flag,skip_if_null,status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('OFFICIAL_STAFF_2026',1,'RESIGNATION_SETTLEMENT','离职费用结算',1,NULL,5,670,0,1,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OFFICIAL_STAFF_2026',1,'AUTO_TAX_CALC','智能个税核算',1,NULL,4,999,1,0,1,0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
