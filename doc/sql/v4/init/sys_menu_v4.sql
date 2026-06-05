INSERT INTO sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('工作台','dashboard_dir','/dashboard','Layout','/dashboard/index','Odometer','',1,0,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('工作台面板','dashboard_index','index','dashboard/index','','DataBoard','',2,1,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('系统管理','sys_dir','/system','Layout','','Setting','',1,0,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('用户管理','sys_user','user','system/user/UserPage','','User','sys:user:list',2,10,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('用户查询','sys_user_query','','','','','sys:user:query',3,100,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('用户新增','sys_user_add','','','','','sys:user:add',3,100,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('用户修改','sys_user_edit','','','','','sys:user:edit',3,100,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('用户删除','sys_user_del','','','','','sys:user:del',3,100,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('分配角色','sys_user_assign','','','','','sys:user:assign',3,100,5,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('角色管理','sys_role','role','system/role/RolePage','','Avatar','sys:role:list',2,10,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33');
INSERT INTO sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('角色查询','sys_role_query','','','','','sys:role:query',3,110,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('角色新增','sys_role_add','','','','','sys:role:add',3,110,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('角色修改','sys_role_edit','','','','','sys:role:edit',3,110,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('角色删除','sys_role_del','','','','','sys:role:del',3,110,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('分配权限','sys_role_assign','','','','','sys:role:assign',3,110,5,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('菜单管理','sys_menu','menu','system/menu/MenuPage','','Menu','sys:menu:list',2,10,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('菜单查询','sys_menu_query','','','','','sys:menu:query',3,120,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('菜单新增','sys_menu_add','','','','','sys:menu:add',3,120,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('菜单修改','sys_menu_edit','','','','','sys:menu:edit',3,120,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('菜单删除','sys_menu_del','','','','','sys:menu:del',3,120,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33');
INSERT INTO sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('字典管理','sys_dict','dict','system/dict/DictPage','','Collection','sys:dict:list',2,10,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('新增字典类型','sys_dict_type_add','','','','','sys:dict_type:add',3,130,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('删除字典类型','sys_dict_type_del','','','','','sys:dict_type:del',3,130,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('修改字典类型','sys_dict_type_edit','','','','','sys:dict_type:edit',3,130,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('查询字典类型','sys_dict_type_query','','','','','sys:dict_type:query',3,130,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('新增字段项','sys_dict_item_add','','','','','sys:dict_item:add',3,130,5,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('删除字典项','sys_dict_item_del','','','','','sys:dict_item:del',3,130,6,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('修改字典项','sys_dict_item_edit','','','','','sys:dict_item:edit',3,130,7,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('查询字典类型','sys_dict_item_query','','','','','sys:dict_item:query',3,130,8,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('薪资管理','salary_manage','/salary','Layout','','Money','',1,0,10,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33');
INSERT INTO sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('员工基础档案','salary_employee','employee','salary/employee/EmployeePage','','User','salary:employee:list',2,150,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('查看员工列表','salary_employee_query','','','','','salary:employee:query',3,160,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('新增员工档案','salary_employee_add','','','','','salary:employee:add',3,160,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('修改员工档案','salary_employee_edit','','','','','salary:employee:edit',3,160,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('销毁员工档案','salary_employee_del','','','','','salary:employee:del',3,160,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('查看档案详情','salary_employee_detail','','','','','salary:employee:detail',3,160,5,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('薪资周期管理','salary_period','period','salary/period/PeriodPage','','Calendar','salary:period:list',2,150,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('查看周期列表','salary_period_query','','','','','salary:period:query',3,170,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('新增/开启周期','salary_period_add','','','','','salary:period:add',3,170,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('修改周期数据','salary_period_edit','','','','','salary:period:edit',3,170,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33');
INSERT INTO sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('批量初始化周期','salary_period_init','','','','','salary:period:init',3,170,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('删除/销毁周期','salary_period_del','','','','','salary:period:del',3,170,5,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('薪资项目配置','salary_item_config','itemconfig','salary/itemconfig/ItemConfigPage','','Setting','salary:item_config:list',2,150,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('新增项目','salary_item_add','','','','','salary:item_config:add',3,180,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('修改项目','salary_item_edit','','','','','salary:item_config:edit',3,180,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('删除项目','salary_item_del','','','','','salary:item_config:del',3,180,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('同步配置','salary_item_refresh','','','','','salary:item_config:refresh',3,180,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('薪资档案管理','salary_archive','archive','salary/archive/ArchivePage','','Document','salary:archive:list',2,150,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('新员工定薪','salary_archive_init','','','','','salary:archive:init',3,190,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('调薪申请','salary_archive_adjust','','','','','salary:archive:adjust',3,190,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33');
INSERT INTO sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('调薪审批','salary_archive_audit','','','','','salary:archive:audit',3,190,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('查看详情','salary_archive_detail','','','','','salary:archive:detail',3,190,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('导出档案','salary_archive_export','','','','','salary:archive:export',3,190,5,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('月度绩效管理','salary_kpi_ecord','kpirecord','salary/kpirecord/KpiRecordPage','','TrendCharts','salary:kpi_record:list',2,150,5,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('查询绩效大盘','salary_kpi_record_query','','','','','salary:kpi_record:query',3,200,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('派发绩效单','salary_kpi_record_init','','','','','salary:kpi_record:init',3,200,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('评估打分','salary_kpi_record_evaluate','','','','','salary:kpi_record:evaluate',3,200,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('审核定稿','salary_kpi_record_confirm','','','','','salary:kpi_record:confirm',3,200,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('薪资引擎配置','salary_engine','engine','','','Operation','',1,150,6,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('计算规则库','salary_calc_rule','calc-rule','salary/calcrule/CalcRulePage','','Collection','salary:rule:list',2,210,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33');
INSERT INTO sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('查询规则','salary_rule_query','','','','','salary:rule:query',3,220,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('新增规则','salary_rule_add','','','','','salary:rule:add',3,220,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('修改规则','salary_rule_edit','','','','','salary:rule:edit',3,220,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('删除规则','salary_rule_del','','','','','salary:rule:del',3,220,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('核算管道编排','salary_calc_pipeline','calc-pipeline','salary/calcpipeline/CalcPipelinePage','','Connection','salary:pipeline:list',2,210,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('查询管道','salary_pipeline_query','','','','','salary:pipeline:query',3,230,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('新建管道','salary_pipeline_add','','','','','salary:pipeline:add',3,230,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('修改管道元数据','salary_pipeline_edit','','','','','salary:pipeline:edit',3,230,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('删除管道','salary_pipeline_del','','','','','salary:pipeline:del',3,230,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('发布瀑布流配置','salary_pipeline_design','','','','','salary:pipeline:design',3,230,5,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33');
INSERT INTO sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('设为系统默认','salary_pipeline_default','','','','','salary:pipeline:default',3,230,6,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('升级新版本','salary_pipeline_upgrade','','','','','salary:pipeline:upgrade',3,230,7,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('专项调整(手工账)','salary_adjustment','adjustment','salary/adjustment/AdjustmentPage','','PriceTag','salary:adjustment:list',2,150,7,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('查询手工账','salary_adjustment_query','','','','','salary:adjustment:query',3,240,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('新增手工账','salary_adjustment_add','','','','','salary:adjustment:add',3,240,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('修改手工账','salary_adjustment_edit','','','','','salary:adjustment:edit',3,240,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('删除手工账','salary_adjustment_del','','','','','salary:adjustment:del',3,240,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('批量生效/撤回','salary_adjustment_audit','','','','','salary:adjustment:audit',3,240,5,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('薪资汇总与发薪','salary_summary','summary','salary/summary/SummaryPage','','Wallet','salary:summary:list',2,150,8,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('查看汇总列表','salary_summary_query','','','','','salary:summary:query',3,250,1,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33');
INSERT INTO sys_menu (menu_name,menu_code,menu_path,menu_component,menu_redirect,menu_icon,menu_permission,menu_type,menu_parent_id,menu_sort,menu_visible,menu_status,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('查看工资条明细','salary_summary_detail','','','','','salary:summary:detail',3,250,2,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('锁定与解锁单据','salary_summary_lock','','','','','salary:summary:lock',3,250,3,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('执行引擎核算','salary_summary_calc','','','','','salary:summary:calc',3,250,4,1,1,0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('初始化本月账套','salary_summary_init','','','','','salary:summary:init',3,250,0,1,1,0,'system','2026-04-06 15:34:54','system','2026-04-06 15:34:54'),
	 ('手工发放总金额','salary_summary_adjust','','','','','salary:summary:adjust',3,250,5,1,1,0,'system','2026-04-11 15:08:15','system','2026-04-11 15:08:24');
