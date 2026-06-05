INSERT INTO sys_role (role_name,role_code,role_sort,role_status,role_desc,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('超级管理员','SUPER_ADMIN',1,1,'系统最高权限','拥有系统所有资源和操作权限',0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('普通管理员','ADMIN',2,1,'普通管理员权限','普通管理员权限',0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('普通员工','USER',2,1,'普通员工权限','普通业务线办理权限',0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33'),
	 ('测试人员','TEST',3,1,'测试人员权限','仅用于查看系统的只读账号',0,'system','2026-04-17 05:34:33','system','2026-04-17 05:34:33');
