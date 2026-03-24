INSERT INTO salary_admin.sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('工作台','dashboard_dir','/dashboard','Layout','/dashboard/index','Odometer','',1,0,1,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('工作台面板','dashboard_index','index','dashboard/index','','DataBoard','',2,1,1,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('系统管理','sys_dir','/system','Layout','','Setting','',1,0,2,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('用户管理','sys_user','user','system/user/UserPage','','User','sys:user:list',2,10,1,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('用户查询','sys_user_query','','','','','sys:user:query',3,100,1,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('用户新增','sys_user_add','','','','','sys:user:add',3,100,2,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('用户修改','sys_user_edit','','','','','sys:user:edit',3,100,3,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('用户删除','sys_user_del','','','','','sys:user:del',3,100,4,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('分配角色','sys_user_assign','','','','','sys:user:assign',3,100,5,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('角色管理','sys_role','role','system/role/RolePage','','Avatar','sys:role:list',2,10,2,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56');
INSERT INTO salary_admin.sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('角色查询','sys_role_query','','','','','sys:role:query',3,110,1,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('角色新增','sys_role_add','','','','','sys:role:add',3,110,2,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('角色修改','sys_role_edit','','','','','sys:role:edit',3,110,3,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('角色删除','sys_role_del','','','','','sys:role:del',3,110,4,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('分配权限','sys_role_assign','','','','','sys:role:assign',3,110,5,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('菜单管理','sys_menu_mgr','menu','system/menu/MenuPage','','Menu','sys:menu:list',2,10,3,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('菜单查询','sys_menu_query','','','','','sys:menu:query',3,120,1,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('菜单新增','sys_menu_add','','','','','sys:menu:add',3,120,2,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('菜单修改','sys_menu_edit','','','','','sys:menu:edit',3,120,3,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56'),
	 ('菜单删除','sys_menu_del','','','','','sys:menu:del',3,120,4,1,1,0,'system','2026-03-09 07:15:13','system','2026-03-09 07:16:56');
INSERT INTO salary_admin.sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('薪酬管理','salary_dir','/salary','Layout','','Money','',1,0,3,1,1,0,'admin','2026-03-12 10:14:54','admin','2026-03-12 10:15:10'),
	 ('员工管理','salary_employee','employee','salary/employee/EmployeePage','','Place','salary:employee:list',2,125,0,1,1,0,'admin','2026-03-12 10:19:33','admin','2026-03-12 10:20:24'),
	 ('查询员工','salary_employee_query','','','','','salary:employee:query',3,126,0,1,1,0,'admin','2026-03-12 10:26:09','admin','2026-03-12 10:26:09'),
	 ('新增员工','salary_employee_add','','','','','salary:employee:add',3,126,1,1,1,0,'admin','2026-03-12 10:27:19','admin','2026-03-12 10:27:19'),
	 ('修改员工','salary_employee_edit','','','','','salary:employee:edit',3,126,2,1,1,0,'admin','2026-03-12 10:28:05','admin','2026-03-12 10:28:05'),
	 ('删除员工','salary_employee_del','','','','','salary:employee:del',3,126,3,1,1,0,'admin','2026-03-12 10:28:50','admin','2026-03-12 10:28:50'),
	 ('薪资周期管理','salary_period','period','salary/period/PeriodPage','','AlarmClock','salary:period:list',2,125,2,1,1,0,'admin','2026-03-12 12:28:56','admin','2026-03-12 12:28:56'),
	 ('新增薪资周期','salary_period_add','','','','','salary:period:add',3,131,2,1,1,0,'admin','2026-03-12 12:41:31','admin','2026-03-12 12:42:36'),
	 ('修改薪资周期','salary_period_edit','','','','','salary:period:edit',3,131,3,1,1,0,'admin','2026-03-12 12:42:06','admin','2026-03-12 12:42:42'),
	 ('查询薪资周期','salary_period_query','','','','','salary:period:query',3,131,1,1,1,0,'admin','2026-03-12 12:43:39','admin','2026-03-12 12:43:39');
INSERT INTO salary_admin.sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('删除薪资周期','salary_period_del','','','','','salary:period:del',3,131,4,1,1,0,'admin','2026-03-12 12:44:31','admin','2026-03-12 12:44:31'),
	 ('收入项配置','salary_income_type','incometype','salary/incometype/IncomeTypePage','','ShoppingBag','salary:income_type:list',2,125,3,1,1,0,'admin','2026-03-12 15:20:14','admin','2026-03-12 15:56:50'),
	 ('扣款项配置','salary_deduction_type','deductiontype','salary/deductiontype/DeductionTypePage','','DeleteLocation','salary:deduction_type:list',2,125,4,1,1,0,'admin','2026-03-12 15:52:04','admin','2026-03-12 07:58:33'),
	 ('查询收入项配置','salary_income_type_query','','','','','salary:income_type:query',3,136,1,1,1,0,'admin','2026-03-12 16:01:53','admin','2026-03-16 06:14:58'),
	 ('新增收入项配置','salary_incomeType_add','','','','','salary:income_type:add',3,136,2,1,1,0,'admin','2026-03-12 16:03:08','admin','2026-03-16 06:14:58'),
	 ('修改收入项配置','salary_incomeType_edit','','','','','salary:income_type:edit',3,136,3,1,1,0,'admin','2026-03-12 16:05:04','admin','2026-03-16 06:14:58'),
	 ('删除收入项配置','salary_incomeType_del','','','','','salary:income_type:del',3,136,4,1,1,0,'admin','2026-03-12 16:05:53','admin','2026-03-16 06:14:58'),
	 ('查询扣款项配置','salary_deduction_type_query','','','','','salary:deduction_type:query',3,137,1,1,1,0,'admin','2026-03-12 16:07:14','admin','2026-03-16 06:14:58'),
	 ('新增扣款项配置','salary_deduction_type_add','','','','','salary:deduction_type:add',3,137,2,1,1,0,'admin','2026-03-12 16:09:11','admin','2026-03-16 06:14:58'),
	 ('修改扣款项配置','salary_deduction_type_edit','','','','','salary:deduction_type:edit',3,137,3,1,1,0,'admin','2026-03-12 16:10:14','admin','2026-03-16 06:14:58');
INSERT INTO salary_admin.sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('修改扣款项配置','salary_deduction_type_del','','','','','salary:deduction_type:del',3,137,4,1,1,0,'admin','2026-03-12 16:11:06','admin','2026-03-16 06:14:58'),
	 ('薪资档案管理','salary_archive_mgr','archive','salary/archive/ArchivePage','','Document','salary:archive:list',2,125,5,1,1,0,'system','2026-03-15 02:27:57','admin','2026-03-18 11:56:16'),
	 ('查询档案','salary_archive_query','','','','','salary:archive:query',3,170,1,1,1,0,'system','2026-03-15 02:27:57','system','2026-03-15 02:28:40'),
	 ('定薪调薪(新增)','salary_archive_add','','','','','salary:archive:add',3,170,2,1,1,0,'system','2026-03-15 02:27:57','system','2026-03-15 02:28:40'),
	 ('查看详情','salary_archive_detail','','','','','salary:archive:detail',3,170,3,1,1,0,'system','2026-03-15 02:27:57','system','2026-03-15 02:28:40'),
	 ('撤销版本','salary_archive_revoke','','','','','salary:archive:revoke',3,170,4,1,1,0,'system','2026-03-15 02:27:57','system','2026-03-15 02:28:40'),
	 ('调薪(修改)','salary_archive_edit','','','','','salary:archive:edit',3,170,5,1,1,0,'system','2026-03-15 03:01:50','system','2026-03-15 03:39:35'),
	 ('审核档案','salary_archive_audit','','','','','salary:archive:audit',3,170,6,1,1,0,'system','2026-03-15 03:05:38','system','2026-03-15 03:39:35'),
	 ('结算汇总管理','salary_summary_mgr','summary','salary/summary/SummaryPage','','DataAnalysis','salary:summary:list',2,125,7,1,1,0,'system','2026-03-15 10:31:56','admin','2026-03-18 11:56:32'),
	 ('查询汇总','salary_summary_query','','','','','salary:summary:query',3,180,1,1,1,0,'system','2026-03-15 10:31:56','system','2026-03-15 10:43:45');
INSERT INTO salary_admin.sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('一键核算','salary_summary_calc','','','','','salary:summary:calc',3,180,2,1,1,0,'system','2026-03-15 10:31:56','system','2026-03-15 10:43:45'),
	 ('查看详情','salary_summary_detail','','','','','salary:summary:detail',3,180,3,1,1,0,'system','2026-03-15 10:31:56','system','2026-03-15 10:43:45'),
	 ('作废汇总','salary_summary_del','','','','','salary:summary:del',3,180,4,1,1,0,'system','2026-03-15 10:31:56','system','2026-03-15 10:43:45'),
	 ('批量作废','salary_summary_batch_del','','','','','salary:summary:batch_del',3,180,5,1,1,0,'system','2026-03-15 10:31:56','system','2026-03-15 10:43:45'),
	 ('发薪明细记录','salary_record_mgr','paymentrecord','salary/paymentrecord/PaymentRecordPage','','List','salary:payment_record:list',2,125,8,1,1,0,'system','2026-03-15 10:31:56','admin','2026-03-18 11:56:45'),
	 ('查询明细','salary_record_query','','','','','salary:payment_record:query',3,190,1,1,1,0,'system','2026-03-15 10:31:56','system','2026-03-15 10:43:45'),
	 ('手动调整','salary_record_edit','','','','','salary:payment_record:edit',3,190,2,1,1,0,'system','2026-03-15 10:31:56','system','2026-03-15 10:43:45'),
	 ('删除明细','salary_record_del','','','','','salary:payment_record:del',3,190,3,1,1,0,'system','2026-03-15 10:31:56','system','2026-03-15 10:43:45'),
	 ('明细导出','salary_record_export','','','','','salary:payment_record:export',3,190,4,1,1,0,'system','2026-03-15 10:31:56','system','2026-03-15 10:43:45'),
	 ('月度变动管理','salary_variablepay','variablepay','salary/variablepay/VariablePayPage','','Edit','salary:variablepay:list',2,125,6,1,1,0,'admin','2026-03-18 11:55:44','admin','2026-03-18 11:56:24');
INSERT INTO salary_admin.sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('录入明细','salary_variablepay_add','','','','','salary:variablepay:add',3,195,1,1,1,0,'admin','2026-03-18 11:58:22','admin','2026-03-18 11:59:07'),
	 ('修改明细','salary_variablepay_edit','','','','','salary:variablepay:edit',3,195,2,1,1,0,'admin','2026-03-18 11:59:02','admin','2026-03-18 11:59:02'),
	 ('删除明细','salary_variablepay_del','','','','','salary:variablepay:del',3,195,3,1,1,0,'admin','2026-03-18 11:59:48','admin','2026-03-18 11:59:48'),
	 ('Excel导入','salary_variablepay_import','','','','','salary:variablepay:import',3,195,4,1,1,0,'admin','2026-03-18 12:00:24','admin','2026-03-18 12:00:24'),
	 ('薪酬全局配置管理','salary_config_mgr','config','salary/config/SalaryConfigPage','','ChromeFilled','salary:config:list',2,125,0,1,1,0,'admin','2026-03-20 15:56:41','admin','2026-03-20 08:18:31'),
	 ('新增配置项','salary_config_add','','','','','salary:config:add',3,200,0,1,1,0,'admin','2026-03-20 15:59:45','admin','2026-03-20 15:59:45'),
	 ('修改配置项','salary_config_edit','','','','','salary:config:edit',3,200,1,1,1,0,'admin','2026-03-20 16:00:50','admin','2026-03-20 16:00:50'),
	 ('字典管理','sys_dict_mgr','dict','system/dict/DictPage','','Notebook','sys:dict:list',2,10,0,1,1,0,'admin','2026-03-20 18:02:25','admin','2026-03-20 18:02:25'),
	 ('新增字典类型','sys_dict_type_add','','','','','sys:dict_type:add',3,203,1,1,1,0,'admin','2026-03-20 18:24:39','admin','2026-03-20 18:24:39'),
	 ('修改字典类型','sys_dict_type_edit','','','','','sys:dict_type:edit',3,203,2,1,1,0,'admin','2026-03-20 18:25:49','admin','2026-03-20 18:25:49');
INSERT INTO salary_admin.sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('删除字典类型','sys_dict_type_del','','','','','sys:dict_type:del',3,203,3,1,1,0,'admin','2026-03-20 18:26:45','admin','2026-03-20 18:26:45'),
	 ('新增字段项','sys_dict_item_add','','','','','sys:dict_item:add',3,203,4,1,1,0,'admin','2026-03-20 18:28:16','admin','2026-03-20 18:29:41'),
	 ('修改字典项','sys_dict_item_edit','','','','','sys:dict_item:edit',3,203,5,1,1,0,'admin','2026-03-20 18:29:33','admin','2026-03-20 18:29:33'),
	 ('删除字典项','sys_dict_item_del','','','','','sys:dict_item:del',3,203,6,1,1,0,'admin','2026-03-20 18:30:41','admin','2026-03-20 18:30:41'),
	 ('批量初始化薪资周期','salary_period_init','','','','','salary:period:init',3,131,0,1,1,0,'admin','2026-03-23 10:37:27','admin','2026-03-23 10:37:27'),
	 ('初始化本月账套','salary_summary_init','salary:summary:init','','','','salary:summary:init',3,180,0,1,1,0,'admin','2026-03-23 10:41:18','admin','2026-03-23 10:41:18');
