INSERT INTO oa_admin.sys_role (role_name,role_code,role_sort,role_status,role_desc,remark,delete_flag,create_by,create_time,update_by,update_time) VALUES
	 ('超级管理员','SUPER_ADMIN',1,1,'系统最高权限','拥有系统所有资源和操作权限',0,'system','2026-03-24 00:49:43','system','2026-03-24 00:49:43'),
	 ('管理员','ADMIN',2,1,'系统较高权限','拥有系统大部分资源和操作权限',0,'system','2026-03-24 00:51:27','system','2026-03-24 00:51:27'),
	 ('普通用户','USER',3,1,'系统用户权限','普通业务操作权限',0,'system','2026-03-24 00:52:06','system','2026-03-24 00:53:11'),
	 ('测试用户','TEST',4,1,'测试人员权限','测试业务操作权限',0,'system','2026-03-24 00:53:11','system','2026-03-24 00:53:11');
