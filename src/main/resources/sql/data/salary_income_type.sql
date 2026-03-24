INSERT INTO salary_admin.salary_income_type (type_code,type_name,pinyin_code,category_name,description,sort_value,delete_flag,create_by,create_time,update_by,update_time,taxable_flag,default_currency) VALUES
	 ('DD','本月底薪标准','BYDXBZ','固定工资','本月底薪标准',1,0,'admin','2026-03-12 16:17:48','admin','2026-03-12 17:26:31',1,'CNY'),
	 ('KK','KPI绩效','KPIJX','绩效奖金','用于录入月度考核浮动奖金，非固定金额',2,0,'admin','2026-03-12 16:24:47','admin','2026-03-16 15:19:09',1,'CNY'),
	 ('FB','外宿房补','WSFB','津贴补贴','发放给未住公司宿舍员工的住房补贴，通常为固定金额',3,0,'admin','2026-03-12 16:27:30','admin','2026-03-16 15:19:18',1,'CNY'),
	 ('QQJ','全勤奖','QQJ','加班考勤','与员工实际出勤天数或特定加班时间挂钩的激励项',4,0,'admin','2026-03-12 16:31:04','admin','2026-03-16 15:19:32',1,'CNY'),
	 ('CJZBSX','春节值班双薪','CJZBSX','加班考勤','与员工实际出勤天数或特定加班时间挂钩的激励项',5,0,'admin','2026-03-12 16:39:34','admin','2026-03-16 15:19:40',1,'CNY'),
	 ('CJLJ','春节礼金','CJLJ','津贴福利','春节礼金',8,0,'admin','2026-03-12 16:40:29','admin','2026-03-16 14:33:17',1,'CNY'),
	 ('NZJ13X','年终奖13薪','NZJ13X','专项奖金','属于年度或长期激励，不建议放在常规月薪计算逻辑中',6,0,'admin','2026-03-12 16:42:36','admin','2026-03-16 15:19:56',1,'CNY'),
	 ('LNZCJ','两年忠诚奖','LNZCJ','专项奖金','属于年度或长期激励，不建议放在常规月薪计算逻辑中',7,0,'admin','2026-03-12 16:45:11','admin','2026-03-16 15:20:01',1,'CNY'),
	 ('INC_RJBGZ','日加班工资','RJBGZ','考勤相关','基于员工底薪计算出的单位劳动对价，需配合加班时长使用',9,0,'admin','2026-03-16 14:33:06','admin','2026-03-16 15:20:13',1,'CNY'),
	 ('INC_SJBGZ','时加班工资','SJBGZ','考勤相关','基于员工底薪计算出的单位劳动对价，需配合加班时长使用',10,0,'admin','2026-03-16 14:39:16','admin','2026-03-16 15:20:17',1,'CNY');
INSERT INTO salary_admin.salary_income_type (type_code,type_name,pinyin_code,category_name,description,sort_value,delete_flag,create_by,create_time,update_by,update_time,taxable_flag,default_currency) VALUES
	 ('INC_YJTC','业绩提成','YJTC','专项奖金','基于个人业务完成额计算的提成',11,0,'admin','2026-03-16 14:40:37','admin','2026-03-16 15:17:46',1,'CNY'),
	 ('INC_DLTC','代理提成','DLTC','专项奖金','基于代理渠道或下级业务计算的提成',12,0,'admin','2026-03-16 14:41:15','admin','2026-03-16 15:18:23',1,'CNY'),
	 ('INC_12XSBT','12小时补贴','12XSBT','考勤相关','12小时补贴',13,0,'admin','2026-03-16 15:22:07','admin','2026-03-16 15:22:07',1,'CNY'),
	 ('INC_GLBT','隔离补贴','GLBT','津贴福利','隔离补贴',14,0,'admin','2026-03-16 15:23:30','admin','2026-03-16 15:23:30',1,'CNY'),
	 ('INC_CB','餐补','CB','津贴福利','餐补',15,0,'admin','2026-03-16 15:23:53','admin','2026-03-16 15:23:53',1,'CNY'),
	 ('INC_AQKJL','安全卡奖励','AQKJL','津贴福利','安全卡奖励',16,0,'admin','2026-03-16 15:24:26','admin','2026-03-16 15:24:26',1,'CNY'),
	 ('INC_NJJJ','年假奖金','NJJJ','专项奖金','年假奖金',18,0,'admin','2026-03-16 15:25:11','admin','2026-03-16 15:26:08',1,'CNY'),
	 ('INC_NTJJ','内推奖金','NTJJ','专项奖金','内推奖金',17,0,'admin','2026-03-16 15:26:00','admin','2026-03-16 15:26:00',1,'CNY'),
	 ('INC_ZQJZBFL','中秋节值班福利','ZQJZBFL','考勤相关','中秋节值班双薪',19,0,'admin','2026-03-16 15:28:05','admin','2026-03-17 08:56:21',1,'CNY'),
	 ('INC_ZQJLJ','中秋节礼金','ZQJLJ','津贴福利','中秋节礼金',20,0,'admin','2026-03-16 15:28:41','admin','2026-03-16 15:28:41',1,'CNY');
INSERT INTO salary_admin.salary_income_type (type_code,type_name,pinyin_code,category_name,description,sort_value,delete_flag,create_by,create_time,update_by,update_time,taxable_flag,default_currency) VALUES
	 ('INC_SRLJ','生日礼金','SRLJ','津贴福利','生日礼金',21,0,'admin','2026-03-16 15:29:34','admin','2026-03-16 15:29:34',1,'CNY'),
	 ('INC_XRZ、HGFYBX','新入职、回国费用报销','XRZ、HGFYBX','津贴福利','新入职、回国费用报销',22,0,'admin','2026-03-16 15:30:58','admin','2026-03-16 15:30:58',1,'CNY'),
	 ('INC_OZBJLJJ','欧洲杯激励奖金','OZBJLJJ','专项奖金','欧洲杯激励奖金',23,0,'admin','2026-03-16 17:55:26','admin','2026-03-16 17:55:26',1,'CNY'),
	 ('INC_DWJZBFL','端午节值班福利','DWJZBFL','考勤相关','端午节值班三薪',24,0,'admin','2026-03-17 16:50:13','admin','2026-03-17 08:56:01',1,'CNY'),
	 ('INC_DWJLJ','端午节礼金','DWJLJ','津贴福利','端午节礼金',25,0,'admin','2026-03-17 16:53:52','admin','2026-03-17 16:53:52',1,'CNY'),
	 ('INC_BYFH','本月返还','BYFH','专项奖金','本月返还',26,0,'admin','2026-03-23 15:15:56','admin','2026-03-23 15:15:56',1,'CNY'),
	 ('INC_SYBF','上月补发','SYBF','考勤相关','上月补发',99,0,'admin','2026-03-23 17:22:33','admin','2026-03-23 17:22:33',1,'CNY');
