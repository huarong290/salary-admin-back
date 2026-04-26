INSERT INTO salary_admin_v4.salary_calc_rule (rule_code,rule_name,rule_type,rule_script,return_type,sort_value,status,depends_on,param_json,stage,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('BASE_SALARY','基本工资',1,'let base = (baseSalary == nil) ? 0.0M : decimal(baseSalary); monthDays > 0M ? (base / monthDays * attendanceDays) : 0.0M','Decimal',10,1,NULL,NULL,1,'底薪折算',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('HOUSING_ALLOW','住房补贴',1,'let allow = (housingAllow == nil) ? 0.0M : decimal(housingAllow); monthDays > 0M ? (allow / monthDays * attendanceDays) : 0.0M','Decimal',11,1,NULL,NULL,1,'房补折算',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('MEAL_ALLOW','餐补',1,'let allow = (mealAllow == nil) ? 0.0M : decimal(mealAllow); monthDays > 0M ? (allow / monthDays * attendanceDays) : 0.0M','Decimal',12,1,NULL,NULL,1,'餐补折算',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('SHIFT_12H_ALLOWANCE','12小时补贴',1,'shift12hAllowance == nil ? 0.0M : decimal(shift12hAllowance)','Decimal',13,1,NULL,NULL,1,'排班补贴',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('QUARANTINE_ALLOWANCE','隔离补贴',1,'quarantineAllowance == nil ? 0.0M : decimal(quarantineAllowance)','Decimal',14,1,NULL,NULL,1,'隔离补贴',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OTHER_ALLOWANCE','其他补贴',1,'otherAllowance == nil ? 0.0M : decimal(otherAllowance)','Decimal',19,1,NULL,NULL,1,'其他非固定补贴',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OVERTIME_PAY_DAY','日加班工资',1,'overtimePayDay == nil ? 0.0M : decimal(overtimePayDay)','Decimal',20,1,NULL,NULL,1,'按天加班',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OVERTIME_PAY_HOUR','时加班工资',1,'overtimePayHour == nil ? 0.0M : decimal(overtimePayHour)','Decimal',21,1,NULL,NULL,1,'按时加班',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('KPI_BONUS','KPI绩效',1,'let base = (baseSalary == nil) ? 0.0M : decimal(baseSalary); let coeff = (kpiCoefficient == nil) ? 0.0M : decimal(kpiCoefficient); base * coeff','Decimal',30,1,NULL,NULL,1,'绩效系数',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('COMMISSION_SALES','业绩提成',1,'commissionSales == nil ? 0.0M : decimal(commissionSales)','Decimal',31,1,NULL,NULL,1,'销售提成',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_admin_v4.salary_calc_rule (rule_code,rule_name,rule_type,rule_script,return_type,sort_value,status,depends_on,param_json,stage,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('COMMISSION_AGENT','代理提成',1,'commissionAgent == nil ? 0.0M : decimal(commissionAgent)','Decimal',32,1,NULL,NULL,1,'代理提成',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ATTENDANCE_BONUS','全勤奖',1,'(isFullAttendance == true) ? (attendanceBonus == nil ? 0.0M : decimal(attendanceBonus)) : 0.0M','Decimal',40,1,NULL,NULL,1,'满勤触发',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ATTENDANCE_REISSUE','考勤/薪资补发',1,'attendanceReissue == nil ? 0.0M : decimal(attendanceReissue)','Decimal',41,1,NULL,NULL,1,'漏打卡补发',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('SAFETY_CARD_BONUS','安全卡奖励',1,'safetyCardBonus == nil ? 0.0M : decimal(safetyCardBonus)','Decimal',50,1,NULL,NULL,1,'安全奖励',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_LEAVE_BONUS','年假奖金',1,'annualLeaveBonus == nil ? 0.0M : decimal(annualLeaveBonus)','Decimal',51,1,NULL,NULL,1,'年假折现',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('REFERRAL_BONUS','内推奖金',1,'referralBonus == nil ? 0.0M : decimal(referralBonus)','Decimal',52,1,NULL,NULL,1,'内推奖',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('BIRTHDAY_BONUS','生日礼金',1,'birthdayBonus == nil ? 0.0M : decimal(birthdayBonus)','Decimal',53,1,NULL,NULL,1,'生日红包',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FESTIVAL_SPRING_GIFT','春节福利',1,'festivalSpringGift == nil ? 0.0M : decimal(festivalSpringGift)','Decimal',60,1,NULL,NULL,1,'春节实物',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FESTIVAL_SPRING_BONUS','春节礼金',1,'festivalSpringBonus == nil ? 0.0M : decimal(festivalSpringBonus)','Decimal',61,1,NULL,NULL,1,'春节现金',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FESTIVAL_DRAGON_BOAT_GIFT','端午节福利',1,'festivalDragonBoatGift == nil ? 0.0M : decimal(festivalDragonBoatGift)','Decimal',62,1,NULL,NULL,1,'端午实物',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_admin_v4.salary_calc_rule (rule_code,rule_name,rule_type,rule_script,return_type,sort_value,status,depends_on,param_json,stage,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('FESTIVAL_DRAGON_BOAT_BONUS','端午节礼金',1,'festivalDragonBoatBonus == nil ? 0.0M : decimal(festivalDragonBoatBonus)','Decimal',63,1,NULL,NULL,1,'端午现金',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FESTIVAL_MID_AUTUMN_GIFT','中秋节福利',1,'festivalMidAutumnGift == nil ? 0.0M : decimal(festivalMidAutumnGift)','Decimal',64,1,NULL,NULL,1,'中秋实物',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FESTIVAL_MID_AUTUMN_BONUS','中秋节礼金',1,'festivalMidAutumnBonus == nil ? 0.0M : decimal(festivalMidAutumnBonus)','Decimal',65,1,NULL,NULL,1,'中秋现金',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('EVENT_EURO_CUP','欧洲杯激励奖金',1,'eventEuroCup == nil ? 0.0M : decimal(eventEuroCup)','Decimal',70,1,NULL,NULL,1,'欧洲杯',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('EVENT_WORLD_CUP','世界杯激励奖金',1,'eventWorldCup == nil ? 0.0M : decimal(eventWorldCup)','Decimal',71,1,NULL,NULL,1,'世界杯',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_13','年终奖13薪',1,'annualBonus13 == nil ? 0.0M : decimal(annualBonus13)','Decimal',79,1,NULL,NULL,1,'13薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_13_5','年终奖13.5薪',1,'annualBonus135 == nil ? 0.0M : decimal(annualBonus135)','Decimal',80,1,NULL,NULL,1,'13.5薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_14','年终奖14薪',1,'annualBonus14 == nil ? 0.0M : decimal(annualBonus14)','Decimal',81,1,NULL,NULL,1,'14薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_14_5','年终奖14.5薪',1,'annualBonus145 == nil ? 0.0M : decimal(annualBonus145)','Decimal',82,1,NULL,NULL,1,'14.5薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_15','年终奖15薪',1,'annualBonus15 == nil ? 0.0M : decimal(annualBonus15)','Decimal',83,1,NULL,NULL,1,'15薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_admin_v4.salary_calc_rule (rule_code,rule_name,rule_type,rule_script,return_type,sort_value,status,depends_on,param_json,stage,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('ANNUAL_BONUS_15_5','年终奖15.5薪',1,'annualBonus155 == nil ? 0.0M : decimal(annualBonus155)','Decimal',84,1,NULL,NULL,1,'15.5薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_16','年终奖16薪',1,'annualBonus16 == nil ? 0.0M : decimal(annualBonus16)','Decimal',85,1,NULL,NULL,1,'16薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_16_5','年终奖16.5薪',1,'annualBonus165 == nil ? 0.0M : decimal(annualBonus165)','Decimal',86,1,NULL,NULL,1,'16.5薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_17','年终奖17薪',1,'annualBonus17 == nil ? 0.0M : decimal(annualBonus17)','Decimal',87,1,NULL,NULL,1,'17薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_17_5','年终奖17.5薪',1,'annualBonus175 == nil ? 0.0M : decimal(annualBonus175)','Decimal',88,1,NULL,NULL,1,'17.5薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_18','年终奖18薪',1,'annualBonus18 == nil ? 0.0M : decimal(annualBonus18)','Decimal',89,1,NULL,NULL,1,'18薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_18_5','年终奖18.5薪',1,'annualBonus185 == nil ? 0.0M : decimal(annualBonus185)','Decimal',90,1,NULL,NULL,1,'18.5薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ANNUAL_BONUS_19','年终奖19薪',1,'annualBonus19 == nil ? 0.0M : decimal(annualBonus19)','Decimal',91,1,NULL,NULL,1,'19薪',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('LOYALTY_BONUS_2Y','忠诚奖金(二年度)',1,'loyaltyBonus2y == nil ? 0.0M : decimal(loyaltyBonus2y)','Decimal',92,1,NULL,NULL,1,'满2年',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('LOYALTY_BONUS_5Y','忠诚奖金(五年度)',1,'loyaltyBonus5y == nil ? 0.0M : decimal(loyaltyBonus5y)','Decimal',93,1,NULL,NULL,1,'满5年',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_admin_v4.salary_calc_rule (rule_code,rule_name,rule_type,rule_script,return_type,sort_value,status,depends_on,param_json,stage,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('LOYALTY_BONUS_10Y','忠诚奖金(十年度)',1,'loyaltyBonus10y == nil ? 0.0M : decimal(loyaltyBonus10y)','Decimal',94,1,NULL,NULL,1,'满10年',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('EXPENSE_REIMBURSE_ONBOARD','入职费用报销',1,'expenseReimburseOnboard == nil ? 0.0M : decimal(expenseReimburseOnboard)','Decimal',95,1,NULL,NULL,1,'免税报销',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('DEPOSIT_REFUND_CURRENT','本月押金返还',1,'depositRefundCurrent == nil ? 0.0M : decimal(depositRefundCurrent)','Decimal',96,1,NULL,NULL,1,'押金返还',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FINE_REBATE','管理罚款返还',1,'fineRebate == nil ? 0.0M : decimal(fineRebate)','Decimal',97,1,NULL,NULL,1,'罚款申诉返还',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('UTILITY_REBATE','水电网费返还',1,'utilityRebate == nil ? 0.0M : decimal(utilityRebate)','Decimal',98,1,NULL,NULL,1,'多扣返还',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('PASSPORT_FEE_REBATE','护照费用返还',1,'passportFeeRebate == nil ? 0.0M : decimal(passportFeeRebate)','Decimal',99,1,NULL,NULL,1,'护照费返还',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ABSENT_DEDUCTION','缺勤扣款',1,'absentDeduction == nil ? 0.0M : decimal(absentDeduction)','Decimal',100,1,NULL,NULL,1,'税前扣',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('LATE_DEDUCTION','迟到早退扣款',1,'lateDeduction == nil ? 0.0M : decimal(lateDeduction)','Decimal',110,1,NULL,NULL,1,'税前扣',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('UTILITY_DEDUCTION','水电网扣款',1,'utilityDeduction == nil ? 0.0M : decimal(utilityDeduction)','Decimal',120,1,NULL,NULL,1,'税后扣',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('FINE_DEDUCTION','管理罚款',1,'fineDeduction == nil ? 0.0M : decimal(fineDeduction)','Decimal',121,1,NULL,NULL,1,'税后扣',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_admin_v4.salary_calc_rule (rule_code,rule_name,rule_type,rule_script,return_type,sort_value,status,depends_on,param_json,stage,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('PASSPORT_FEE_DEDUCTION','护照费用代扣',1,'passportFeeDeduction == nil ? 0.0M : decimal(passportFeeDeduction)','Decimal',122,1,NULL,NULL,1,'护照代扣',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('DEPOSIT_DEDUCTION_CURRENT','本月押金扣除',1,'depositDeductionCurrent == nil ? 0.0M : decimal(depositDeductionCurrent)','Decimal',123,1,NULL,NULL,1,'押金扣',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('OTHER_DEDUCTION','其他扣除',1,'otherDeduction == nil ? 0.0M : decimal(otherDeduction)','Decimal',130,1,NULL,NULL,1,'其他非固定扣款',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('RESIGNATION_SETTLEMENT','离职费用结算',1,'resignationSettlement == nil ? 0.0M : decimal(resignationSettlement)','Decimal',140,1,NULL,NULL,1,'离职清算扣款',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('PREV_MONTH_ADJUSTMENT','上月补发/续扣',1,'prevMonthAdjustment == nil ? 0.0M : decimal(prevMonthAdjustment)','Decimal',141,1,NULL,NULL,1,'人工回溯调账',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('SI_PENSION_IND','养老保险(个人)',1,'siPensionInd == nil ? 0.0M : decimal(siPensionInd)','Decimal',200,1,NULL,NULL,1,'个人社保',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('SI_MED_IND','医疗保险(个人)',1,'siMedInd == nil ? 0.0M : decimal(siMedInd)','Decimal',210,1,NULL,NULL,1,'个人社保',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('SI_HOUSING_IND','公积金(个人)',1,'siHousingInd == nil ? 0.0M : decimal(siHousingInd)','Decimal',220,1,NULL,NULL,1,'个人公积金',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('SI_REISSUE_IND','个人社保退费/补发',1,'siReissueInd == nil ? 0.0M : decimal(siReissueInd)','Decimal',230,1,NULL,NULL,1,'社保多扣补发',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('ER_PENSION_COMP','养老保险(公司)',1,'erPensionComp == nil ? 0.0M : decimal(erPensionComp)','Decimal',300,1,NULL,NULL,1,'公司成本',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
INSERT INTO salary_admin_v4.salary_calc_rule (rule_code,rule_name,rule_type,rule_script,return_type,sort_value,status,depends_on,param_json,stage,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('ER_VISA_COMP','海外签证费用',1,'erVisaComp == nil ? 0.0M : decimal(erVisaComp)','Decimal',310,1,NULL,NULL,1,'公司承担',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18'),
	 ('AUTO_TAX_CALC','智能个税核算',1,'0.0M','Decimal',999,1,NULL,NULL,1,'触发Java内置计税引擎',0,'admin','2026-04-17 05:37:18','admin','2026-04-17 05:37:18');
