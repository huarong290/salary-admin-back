INSERT INTO sys_dict_type (dict_type_code,dict_type_name,dict_category,status,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('salary_item_category','薪资项目大类','salary',1,'定义薪资项的物理分类：收入、扣款、税费等',0,'system','2026-04-17 06:16:10','admin','2026-04-17 06:16:10'),
	 ('salary_item_sub_type','薪资项目细类','salary',1,'定义具体的业务逻辑标识，用于代码或脚本识别',0,'system','2026-04-17 06:16:10','admin','2026-04-17 06:16:10'),
	 ('salary_calc_stage','薪资核算阶段','salary',1,'定义薪资瀑布流引擎执行的物理次序阶段',0,'system','2026-04-17 06:16:10','admin','2026-04-17 06:16:10'),
	 ('salary_tax_rule','个税核算规则','salary',1,'定义员工发薪时适用的个人所得税计算标准及计税分支',0,'system','2026-04-17 06:16:10','admin','2026-04-17 06:16:10'),
	 ('payment_channel','支付打款渠道','finance',1,'出纳打款的资金渠道',0,'system','2026-04-17 06:16:10','admin','2026-04-17 06:16:10'),
	 ('settlement_currency','结算本位币种','finance',1,'用于薪资计算和发放的币种',0,'system','2026-04-17 06:16:10','admin','2026-04-17 06:16:10'),
	 ('employment_status','员工在职状态','hr',1,'影响薪资周期计算的状态',0,'system','2026-04-17 06:16:10','admin','2026-04-17 06:16:10');
