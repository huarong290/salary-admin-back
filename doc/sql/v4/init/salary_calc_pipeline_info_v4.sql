INSERT INTO salary_admin_v4.salary_calc_pipeline_info (pipeline_code,pipeline_name,version,default_flag,status,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('OFFICIAL_STAFF_2026','2026年度正式员工核算流',1,0,1,'本管道适用于集团 2026 年度全体正式员工月度核算。
制度依据：遵循 2026 版薪酬管理办法，包含基本工资、五险一金及各项绩效奖金。
逻辑特性：计算顺序严格遵循 [基础->补贴->扣款->税->汇总] 阶段，已同步 2026 年最新公积金缴存基数上限。
维护人：HR-薪酬组 / 技术支撑部',0,'system','2026-04-02 14:11:34','system','2026-04-02 14:11:34');
