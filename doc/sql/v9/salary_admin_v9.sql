-- ==========================================================
-- 模块一：系统基础权限与字典 (System Base)
-- ==========================================================
-- ==========================================================
-- 1. 用户表 sys_user
-- ==========================================================
CREATE TABLE `sys_user`
(
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`        VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`        VARCHAR(255) NOT NULL COMMENT '加密密码',
    `salt`            VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '密码盐值',
    `nickname`        VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '用户昵称',
    `email`           VARCHAR(100) NOT NULL DEFAULT '' COMMENT '邮箱',
    `phone`           VARCHAR(30)  NOT NULL DEFAULT '' COMMENT '手机号',
    `sex`             TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '用户性别 (0:未知, 1:男, 2:女)',
    `avatar`          VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像地址URL',
    `status`          TINYINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '状态 (1:正常, 0:禁用)',
    `last_login_time` DATETIME     NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '最后登录时间',
    `delete_flag`     BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`       VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username_del` (`username`, `delete_flag`),
    UNIQUE KEY `uk_email_del` (`email`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ==========================================================
-- 2. 角色表 sys_role
-- ==========================================================
CREATE TABLE `sys_role`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    `role_code`   VARCHAR(50)  NOT NULL COMMENT '角色编码',
    `role_sort`   INT          NOT NULL DEFAULT '0' COMMENT '显示顺序',
    `role_status` TINYINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '状态 (1:正常, 0:禁用)',
    `role_desc`   VARCHAR(100) NOT NULL DEFAULT '' COMMENT '角色描述',
    `remark`      VARCHAR(500) NOT NULL DEFAULT '' COMMENT '备注',
    `delete_flag` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`   VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rolecode_del` (`role_code`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ==========================================================
-- 3. 菜单表 sys_menu
-- ==========================================================
CREATE TABLE `sys_menu`
(
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name`       VARCHAR(50)  NOT NULL COMMENT '菜单名称',
    `menu_code`       VARCHAR(100) NOT NULL COMMENT '菜单编码',
    `menu_path`       VARCHAR(200) NOT NULL DEFAULT '' COMMENT '前端路由地址',
    `menu_component`  VARCHAR(200) NOT NULL DEFAULT '' COMMENT '前端组件路径',
    `menu_redirect`   VARCHAR(200) NOT NULL DEFAULT '' COMMENT '重定向地址',
    `menu_icon`       VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '菜单图标',
    `menu_permission` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '后端权限标识',
    `menu_type`       TINYINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '菜单类型 (1:目录, 2:菜单, 3:按钮)',
    `menu_parent_id`  BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '父级ID',
    `menu_sort`       INT          NOT NULL DEFAULT '0' COMMENT '排序',
    `menu_visible`    TINYINT(1)   NOT NULL DEFAULT '1' COMMENT '是否可见 (1:可见, 0:隐藏)',
    `menu_status`     TINYINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '状态 (1:正常, 0:停用)',
    `delete_flag`     BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`       VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_menucode_del` (`menu_code`, `delete_flag`),
    KEY               `idx_parent_id` (`menu_parent_id`),
    KEY               `idx_permission` (`menu_permission`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限菜单表';

-- ==========================================================
-- 4. 用户-角色关联表 sys_user_role
-- ==========================================================
CREATE TABLE `sys_user_role`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `role_id`     BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    `delete_flag` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role_del` (`user_id`, `role_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与角色关联表';

-- ==========================================================
-- 5. 角色-菜单关联表 sys_role_menu
-- ==========================================================
CREATE TABLE `sys_role_menu`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id`     BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    `menu_id`     BIGINT UNSIGNED NOT NULL COMMENT '菜单ID',
    `delete_flag` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu_del` (`role_id`, `menu_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与菜单关联表';

-- ==========================================================
-- 6. 系统字典类型表 sys_dict_type
-- ==========================================================
CREATE TABLE `sys_dict_type`
(
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_type_code` VARCHAR(50)  NOT NULL COMMENT '字典类型编码',
    `dict_type_name` VARCHAR(100) NOT NULL COMMENT '字典类型名称',
    `dict_category`  VARCHAR(50)  NOT NULL DEFAULT 'common' COMMENT '所属业务模块：如 system(系统), hr(人事), salary(薪资), finance(财务)',
    `status`         TINYINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '状态 (1:启用, 0:禁用)',
    `remark`         VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注说明',
    `delete_flag`    BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`      VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code_del` (`dict_type_code`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典类型表';

-- ==========================================================
-- 7. 系统字典项表 sys_dict_item
-- ==========================================================
CREATE TABLE `sys_dict_item`
(
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_type_code`  VARCHAR(50)  NOT NULL COMMENT '字典类型编码',
    `dict_item_value` VARCHAR(100) NOT NULL COMMENT '字典项值',
    `dict_item_label` VARCHAR(100) NOT NULL COMMENT '字典项名称',
    `dict_item_sort`  INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`          TINYINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '状态 (1:启用, 0:禁用)',
    `remark`          varchar(255) NOT NULL DEFAULT '' COMMENT '备注说明',
    `delete_flag`     BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`       VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_item_code_del` (`dict_type_code`, `dict_item_value`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典项表';

-- ==========================================================
-- 第二部分：薪资系统基础配置与员工档案 (Config & Employee)
-- ==========================================================
-- ==========================================================
-- 8. 系统字典明细表 salary_config
-- ==========================================================
CREATE TABLE `salary_config`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `config_key`   VARCHAR(64)  NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(255) NOT NULL COMMENT '配置值',
    `config_name`  VARCHAR(128) NOT NULL COMMENT '配置名称',
    `config_type`  VARCHAR(20)  NOT NULL DEFAULT 'string' COMMENT '值类型: string, number, boolean, json',
    `config_group` VARCHAR(64)  NOT NULL DEFAULT 'default' COMMENT '配置分组 (如: calc_rule, notification)',
    `active_flag`  TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活 (1:是,0:否)',
    `remark`       VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `delete_flag`  BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`    VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`    VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_config_key_del` (`config_key`, `delete_flag`),
    KEY            `idx_active_flag` (`active_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资系统全局配置表';
-- ==========================================================
-- 9. 系统字典明细表 salary_item_config
-- ==========================================================
CREATE TABLE `salary_item_config`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `item_code`           VARCHAR(64) NOT NULL COMMENT '项编码 (如：BASIC_SALARY, LATE_DEDUCTION)',
    `item_name`           VARCHAR(64) NOT NULL COMMENT '项名称',
    `item_category`       TINYINT UNSIGNED NOT NULL COMMENT '项目分类: 1-收入, 2-扣款, 3-税费, 4-公司支出/补贴',
    `category_dict_value` VARCHAR(64) NOT NULL COMMENT '业务分类字典值 (如: allowance, bonus)',
    `env_var_name`        VARCHAR(64) NOT NULL COMMENT '引擎上下文变量名',
    `default_rule_script` TEXT COMMENT '默认表达式脚本模板',
    `calc_priority`       INT         NOT NULL DEFAULT 0 COMMENT '计算优先级 (数值越小越靠前)',
    `decimal_places`      TINYINT     NOT NULL DEFAULT 2 COMMENT '保留小数位数',
    `rounding_mode`       VARCHAR(20) NOT NULL DEFAULT 'HALF_UP' COMMENT '舍入规则: HALF_UP(四舍五入), DOWN(截断), UP(向上进位)',
    `taxable_flag`        TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否计税 (仅对收入有效)',
    `tax_deductible_flag` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否税前扣除 (仅对扣款有效)',
    `fixed_flag`          TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否固定项',
    `pinyin_code`         VARCHAR(64) NOT NULL DEFAULT '' COMMENT '拼音缩写',
    `sort_value`          INT         NOT NULL DEFAULT 0 COMMENT '显示排序',
    `status`              TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态 (1:启用, 0:禁用)',
    `remark`              VARCHAR(255)         DEFAULT '' COMMENT '备注',
    `delete_flag`         BIGINT UNSIGNED NOT NULL DEFAULT 0,
    `create_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_item_code_del` (`item_code`, `delete_flag`),
    KEY                   `idx_category` (`item_category`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资项目统一配置表';


-- ==========================================================
-- 10. 员工基本信息表 salary_employee
-- ==========================================================
CREATE TABLE `salary_employee`
(
    `id`                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '员工ID',
    `employee_code`        VARCHAR(32)  NOT NULL COMMENT '员工编号',
    `employee_name`        VARCHAR(64)  NOT NULL COMMENT '姓名',
    `company_name`         VARCHAR(128) NOT NULL DEFAULT '' COMMENT '所属公司',
    `department`           VARCHAR(128) NOT NULL DEFAULT '' COMMENT '部门',
    `job_title`            VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '岗位名称/职级',
    `employment_status`    TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '在职状态: 1-正式, 2-试用, 3-实习, 4-兼职/外包, 0-离职',
    `entry_date`           DATE         NOT NULL COMMENT '入职日期',
    `probation_end_date`   DATE                  DEFAULT NULL COMMENT '预计转正日期',
    `actual_leave_date`    DATE                  DEFAULT NULL COMMENT '实际离职日期',
    `transfer_flag`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否转岗: 1-是, 0-否',
    `accommodation_status` TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '住宿情况: 0-不住宿, 1-公司宿舍, 2-外宿补贴',
    `platform_account`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '平台账号(如工号/考勤号)',
    `remark`               VARCHAR(255) NOT NULL DEFAULT '' COMMENT '财务备注',
    `delete_flag`          BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`            VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `create_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`            VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `update_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_code_del` (`employee_code`, `delete_flag`),
    KEY                    `idx_dept_status` (`department`, `employment_status`),
    KEY                    `idx_entry_date` (`entry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工基本信息表';

-- ==========================================================
-- 11. 员工薪资标准配置表 (档案主表)
-- ==========================================================
CREATE TABLE `salary_archive`
(
    `id`                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `employee_id`           BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
    `version`               INT            NOT NULL DEFAULT 1 COMMENT '版本号',
    `latest_flag`           TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否最新版本 (1:是,0:否)',
    `effective_date`        DATE           NOT NULL COMMENT '生效日期',
    `expiry_date`           DATE           NOT NULL DEFAULT '9999-12-31' COMMENT '失效日期',
    `audit_status`          TINYINT(1) NOT NULL DEFAULT 0 COMMENT '审核状态 (0:草稿,1:已生效,2:驳回)',
    `base_salary`           DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '基本工资',
    `probation_base_salary` DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '试用期底薪',
    `currency`              VARCHAR(16)    NOT NULL DEFAULT 'CNY' COMMENT '结算币种',
    `change_reason`         VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '调薪原因',
    `tax_rule_code`         VARCHAR(64)    NOT NULL DEFAULT 'TAX_RESIDENT_CN' COMMENT '个税规则Code',
    `remark`                VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '备注',
    `delete_flag`           BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`             VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `create_time`           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`             VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `update_time`           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_emp_version_del` (`employee_id`, `version`, `delete_flag`),
    KEY                     `idx_emp_latest_status` (`employee_id`, `latest_flag`, `audit_status`),
    KEY                     `idx_emp_time_slice` (`employee_id`, `effective_date`, `expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工薪资标准配置表(含版本历史)';
-- ==========================================================
-- 12. 薪资档案固定项明细表 (档案从表) (🚀 动态模式增强版)
-- ==========================================================
CREATE TABLE `salary_archive_item`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `archive_id`          BIGINT UNSIGNED NOT NULL COMMENT '档案ID',
    `item_type`           TINYINT(1) NOT NULL COMMENT '项目类型: 1-收入项, 2-扣款项',
    `item_config_id`      BIGINT UNSIGNED NOT NULL COMMENT '关联salary_item_config.id',
    `type_name`           VARCHAR(64)    NOT NULL COMMENT '项目名称快照',
    `category_dict_value` VARCHAR(64)    NOT NULL COMMENT '分类字典值快照',
    `calc_mode`           TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '计算模式: 1-按月固定, 2-按出勤天数计算, 3-按现场出勤天数计算,4-按居家出勤天数计算',
    `rule_script`         TEXT COMMENT '特异性自定义表达式脚本（留空则默认走全局规则库）',
    `amount`              DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '基准标准金额 (若按月固定则代表月总额，如500; 若按天计算则代表日单价，如20)',
    `taxable_flag`        TINYINT(1) NULL DEFAULT NULL COMMENT '计税标识: NULL-继承全局配置, 0-不计税, 1-计税',
    `delete_flag`         BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`           VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `create_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`           VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `update_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_archive_item_del` (`archive_id`, `item_type`, `item_config_id`, `delete_flag`),
    KEY                   `idx_archive_item_type` (`archive_id`, `item_type`, `item_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资档案固定项明细表';
-- ==========================================================
-- 第三部分：计算引擎核心规则与管道 (Rules Engine)
-- ==========================================================
-- ==========================================================
-- 13. 薪资全局计算规则库表 (🌟 全新引擎核心表)
-- ==========================================================
CREATE TABLE `salary_calc_rule`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `rule_code`   VARCHAR(64)  NOT NULL COMMENT '规则编码',
    `rule_name`   VARCHAR(64)  NOT NULL COMMENT '规则名称',
    `rule_type`   TINYINT(1) NOT NULL DEFAULT 1 COMMENT '规则类型 (1:公式,2:函数)',
    `rule_script` TEXT         NOT NULL COMMENT '表达式脚本',
    `return_type` VARCHAR(32)  NOT NULL DEFAULT 'Decimal' COMMENT '返回值类型',
    `sort_value`  INT          NOT NULL DEFAULT 0 COMMENT '默认显示排序(仅用于字典列表展示)',
    `status`      TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 (1:启用,0:停用)',
    `depends_on`  VARCHAR(255)          DEFAULT NULL COMMENT '依赖变量',
    `param_json`  JSON                  DEFAULT NULL COMMENT '参数配置',
    `stage`       INT          NOT NULL DEFAULT 1 COMMENT '所属阶段',
    `remark`      VARCHAR(255) NOT NULL DEFAULT '' COMMENT '财务备注',
    `delete_flag` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
    `create_by`   VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`   VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_rule_code_del` (`rule_code`, `delete_flag`),
    KEY           `idx_rule_type_status` (`rule_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资计算规则库表';
-- ==========================================================
-- 14. 薪资计算流程管道表
-- ==========================================================
CREATE TABLE `salary_calc_pipeline_info`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `pipeline_code` VARCHAR(64)  NOT NULL COMMENT '唯一编码',
    `pipeline_name` VARCHAR(128) NOT NULL COMMENT '流程名称',
    `version`       INT          NOT NULL DEFAULT 1 COMMENT '版本号',
    `default_flag`  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否默认流程',
    `status`        TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态(1启用 0停用)',
    `remark`        VARCHAR(255) NOT NULL DEFAULT '' COMMENT '财务备注',
    `delete_flag`   BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
    `create_by`     VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`     VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_pipeline_code_ver` (`pipeline_code`, `version`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资计算管道-主表';
-- ==========================================================
-- 15. 薪资计算管道步骤
-- ==========================================================
CREATE TABLE `salary_calc_pipeline_step`
(
    `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `pipeline_code`    VARCHAR(64) NOT NULL COMMENT '所属管道编码',
    `pipeline_version` INT         NOT NULL DEFAULT 1 COMMENT '管道版本',
    `rule_code`        VARCHAR(64) NOT NULL COMMENT '规则编码',
    `rule_name`        VARCHAR(64) NOT NULL DEFAULT '' COMMENT '规则名称快照',
    `rule_type`        TINYINT(1) DEFAULT NULL COMMENT '规则类型快照',
    `condition_script` TEXT COMMENT '执行条件表达式',
    `stage`            TINYINT     NOT NULL COMMENT '阶段(1基础 2补贴 3扣款 4税 5汇总)',
    `sort_order`       INT         NOT NULL COMMENT '执行顺序',
    `block_flag`       TINYINT(1) NOT NULL DEFAULT 1 COMMENT '失败是否阻断',
    `skip_if_null`     TINYINT(1) NOT NULL DEFAULT 0 COMMENT '结果为空是否跳过',
    `status`           TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `delete_flag`      BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
    `create_by`        VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`        VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_pipeline_step` (`pipeline_code`, `pipeline_version`, `rule_code`, `delete_flag`),
    KEY                `idx_pipeline_exec` (`pipeline_code`, `pipeline_version`, `status`, `stage`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资计算管道步骤-明细表';
-- ==========================================================
-- 16. 薪资计算上下文快照表
-- ==========================================================
CREATE TABLE `salary_calc_context`
(
    `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `employee_id`      BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
    `period_id`        BIGINT UNSIGNED NOT NULL COMMENT '薪资周期ID',
    `archive_id`       BIGINT UNSIGNED NOT NULL COMMENT '来源档案ID',
    `pipeline_version` INT          NOT NULL DEFAULT 1 COMMENT '使用的流程管道版本号',
    `env_json`         JSON         NOT NULL COMMENT '上下文变量JSON（env）',
    `pipeline_code`    VARCHAR(64)  NOT NULL COMMENT '使用的流程编码',
    `version`          INT          NOT NULL DEFAULT 1 COMMENT '版本号',
    `remark`           VARCHAR(255) NOT NULL DEFAULT '' COMMENT '财务备注',
    `delete_flag`      BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`        VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`        VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_emp_period_ver` (`employee_id`, `period_id`, `version`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资计算上下文快照表';
-- ==========================================================
-- 17. 薪资计算日志表
-- ==========================================================
CREATE TABLE `salary_calc_log`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `employee_id`  BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
    `period_id`    BIGINT UNSIGNED NOT NULL COMMENT '薪资周期ID',
    `rule_code`    VARCHAR(64) NOT NULL COMMENT '规则编码',
    `stage`        INT         NOT NULL COMMENT '执行阶段',
    `input_json`   JSON COMMENT '输入参数',
    `output_value` DECIMAL(18, 8)       DEFAULT NULL COMMENT '输出结果',
    `error_msg`    VARCHAR(500)         DEFAULT NULL COMMENT '错误信息',
    `execute_time` BIGINT               DEFAULT NULL COMMENT '耗时(ms)',
    `delete_flag`  BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`    VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`    VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY            `idx_emp_period` (`employee_id`, `period_id`),
    KEY            `idx_rule_code` (`rule_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资计算日志表';

-- ==========================================================
-- 第四部分：薪资周期与交易明细 (Transactional Data)
-- ==========================================================
-- ==========================================================
-- 18. 薪资周期信息表 salary_period
-- ==========================================================
CREATE TABLE `salary_period`
(
    `id`                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '周期ID',
    `employee_id`          BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
    `work_month`           INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '累计在岗月份计数 (入职首月为1, 递增)',
    `settlement_month`     CHAR(6)       NOT NULL COMMENT '结算月份 (YYYYMM)',
    `start_date`           DATE                   DEFAULT NULL COMMENT '开始日期',
    `end_date`             DATE                   DEFAULT NULL COMMENT '结束日期',
    `month_days`           DECIMAL(10, 2) NOT NULL DEFAULT '0.00' COMMENT '月天数',
    `standard_rest_days`   DECIMAL(10, 2) NOT NULL DEFAULT '0.00' COMMENT '标准/制度月休天数 (如4.00, 6.00, 8.00)',
    `attendance_days`      DECIMAL(10, 2) NOT NULL DEFAULT '0.00' COMMENT '出勤天数',
    `office_days`          DECIMAL(10, 2) NOT NULL DEFAULT '0.00' COMMENT '现场出勤天数',
    `wfh_days`             DECIMAL(10, 2) NOT NULL DEFAULT '0.00' COMMENT '居家出勤天数',
    `unpaid_leave_days`    DECIMAL(10, 2)NOT NULL DEFAULT '0.00' COMMENT '非带薪假/欠勤天数',
    `paid_leave_days`      DECIMAL(10, 2) NOT NULL DEFAULT '0.00' COMMENT '带薪假天数(如年假、调休)' ,
    `full_attendance_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否满勤 (1:是, 0:否)',
    `delete_flag`          BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`            VARCHAR(64)   NOT NULL DEFAULT 'admin',
    `create_time`          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`            VARCHAR(64)   NOT NULL DEFAULT 'admin',
    `update_time`          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_emp_month_del` (`employee_id`, `settlement_month`, `delete_flag`),
    KEY                    `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资周期信息表';


-- ==========================================================
-- 19. 薪资汇总与结算表 salary_summary
-- ==========================================================
CREATE TABLE `salary_summary`
(
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '汇总ID',
    `employee_id`       BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
    `employee_code`     VARCHAR(32)    NOT NULL COMMENT '员工编号快照',
    `employee_name`     VARCHAR(100)   NOT NULL COMMENT '员工姓名快照',
    `period_id`         BIGINT UNSIGNED NOT NULL COMMENT '薪资周期ID',
    `settlement_month`  CHAR(6)        NOT NULL COMMENT '结算月份 (YYYYMM)',
    `period_start_date` DATE                    DEFAULT NULL COMMENT '周期开始快照',
    `period_end_date`   DATE                    DEFAULT NULL COMMENT '周期结束快照',
    `income_total`      DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '收入合计（item_type=1）',
    `deduction_total`   DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '扣款合计（item_type=2）',
    `tax_total`         DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '税费合计（item_type=3）',
    `gross_salary`      DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '应发工资（税前）通常 = income_total',
    `net_salary`        DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '实发工资（最终） net = income - deduction - tax',
    `manual_payment_amount` DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '手动发放总额',
    `calc_version`      INT            NOT NULL DEFAULT 1 COMMENT '计算版本号(用于重算/历史追溯)',
    `calc_status`       TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT ' 计算状态:0-未计算 1-成功 2-失败',
    `payment_status`    TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '发放状态：0-未支付 1-已支付 2-支付失败',
    `lock_flag`         TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否锁定(1:锁定, 0:未锁定, 发放后锁定不可重算)',
    `detail_json`       JSON                    DEFAULT NULL COMMENT '汇总快照(用于展示工资单):{"income": [...],"deduction": [...],"tax": [...]}',
    `remark`            VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '财务备注',
    `delete_flag`       BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`         VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `create_time`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`         VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `update_time`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_emp_period_del` (`employee_id`, `period_id`, `delete_flag`),
    KEY                 `idx_summary_month_emp` (`settlement_month`, `employee_id`),
    KEY                 `idx_payment_status_month` (`payment_status`, `settlement_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资汇总与结算表';
-- ==========================================================
-- 20. 统一收支明细表 salary_item_detail
-- ==========================================================
CREATE TABLE `salary_item_detail`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id`         BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
    `period_id`           BIGINT UNSIGNED NOT NULL COMMENT '薪资周期ID',
    `summary_id`          BIGINT UNSIGNED NOT NULL COMMENT '汇总ID',
    `item_type`           TINYINT(1) UNSIGNED NOT NULL COMMENT '项目分类: 1-收入, 2-扣款, 3-税费, 4-公司支出/补贴',
    `item_config_id`      BIGINT UNSIGNED NOT NULL COMMENT '项目配置ID(关联salary_item_config.id)',
    `item_code`           VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '类型编码快照',
    `item_name`           VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '类型名称快照',
    `category_dict_value` VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '分类字典值快照',
    `source_type`         TINYINT(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '数据来源类型：1-薪资档案 2-引擎计算 3-手动调整 4-外部导入',
    `archive_id`          BIGINT UNSIGNED         DEFAULT NULL COMMENT '来源档案ID',
    `archive_item_id`     BIGINT UNSIGNED         DEFAULT NULL COMMENT '来源档案明细ID',
    `rule_code`           VARCHAR(64)             DEFAULT NULL COMMENT '计算规则编码',
    `original_currency`   VARCHAR(16)    NOT NULL DEFAULT 'CNY' COMMENT '原始币种',
    `original_amount`     DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '原始金额',
    `exchange_rate`       DECIMAL(18, 8) NOT NULL DEFAULT '1.00000000' COMMENT '汇率',
    `settlement_amount`   DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '结算金额',
    `settlement_currency` VARCHAR(16)    NOT NULL DEFAULT 'CNY' COMMENT '结算币种',
    `calc_priority`       INT            NOT NULL DEFAULT 0 COMMENT '计算优先级',
    `calc_snapshot`       JSON                    DEFAULT NULL COMMENT '计算快照（用于解释计算过程）',
    `remark`              VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '备注说明',
    `delete_flag`         BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标识',
    `create_by`           VARCHAR(64)    NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`           VARCHAR(64)    NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY                   `idx_emp_period` (`employee_id`, `period_id`),
    KEY                   `idx_summary` (`summary_id`),
    KEY                   `idx_item_type` (`item_type`),
    KEY                   `idx_item_config_id` (`item_config_id`),
    KEY                   `idx_source` (`source_type`),
    KEY                   `idx_archive_trace` (`archive_id`, `archive_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统一收支明细表';
-- ==========================================================
-- 21. 薪资计算结果快照表 salary_payment_record
-- ==========================================================
CREATE TABLE `salary_payment_record`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `summary_id`          BIGINT UNSIGNED NOT NULL COMMENT '汇总ID',
    `employee_id`         BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
    `transaction_no`      VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '支付流水号/批次号',
    `base_amount`         DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '本次核销本位币金额',
    `settlement_currency` VARCHAR(16)    NOT NULL DEFAULT 'CNY' COMMENT '结算币种',
    `exchange_rate`       DECIMAL(18, 8) NOT NULL DEFAULT '1.00000000' COMMENT '汇率',
    `actual_amount`       DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '实际到账金额 (base_amount * exchange_rate)',
    `payment_method`      VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '支付方式',
    `channel_code`        VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '渠道编码: 如 BDO, ICBC, ALIPAY',
    `channel_name`        VARCHAR(128)   NOT NULL DEFAULT '' COMMENT '渠道名称快照: 如 BDO Unibank, 工商银行',
    `target_account`      VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '收款账号/钱包地址',
    `target_account_name` VARCHAR(128)   NOT NULL DEFAULT '' COMMENT '收款人户名快照',
    `payment_status`      TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '支付单状态: 0-待处理, 1-支付中, 2-支付成功, 3-支付失败',
    `payment_time`        DATETIME                DEFAULT NULL COMMENT '实际打款/到账时间',
    `error_msg`           VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '支付失败原因',
    `remark`              VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '财务备注',
    `delete_flag`         BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`           VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `create_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`           VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `update_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_transaction_no` (`transaction_no`, `delete_flag`),
    KEY                   `idx_summary_emp_month` (`summary_id`, `employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资结算明细记录表';


-- ==========================================================
-- 22.员工月度绩效考核记录表 ( salary_kpi_record )
-- 业务定位：独立存储员工的月度绩效考核结果，与发薪周期(period)强绑定。
-- 引擎对接：算薪引擎在执行时，根据 employee_id 和 period_id 获取生效的绩效系数。
-- ==========================================================
CREATE TABLE `salary_kpi_record`
(
    `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id`      BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
    `period_id`        BIGINT UNSIGNED NOT NULL COMMENT '关联薪资周期ID (硬关联：确保绩效与发薪周期绝对对齐)',
    `settlement_month` CHAR(6)       NOT NULL COMMENT '考核/结算月份 (格式: YYYYMM，方便按月快速检索)',
    `kpi_grade`        VARCHAR(16)   NOT NULL DEFAULT '' COMMENT '最终绩效评级 (例如: S, A, B, C, D 等)',
    `kpi_score`        DECIMAL(6, 2) NOT NULL DEFAULT 0.00 COMMENT '最终考核打分 (例如: 95.50，用于精细化计算)',
    `kpi_coefficient`  DECIMAL(6, 4) NOT NULL DEFAULT 1.0000 COMMENT '绩效发放系数 (核心参数：例如 1.2000，算薪引擎直接乘以绩效基数)',
    `evaluate_by`      VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '考核人 (通常记录直属主管或HR的账号/工号)',
    `evaluate_remark`  VARCHAR(500)  NOT NULL DEFAULT '' COMMENT '考核评语/说明 (用于申诉或审计备查)',
    `audit_status`     TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '审核流转状态 (0:打分中/草稿, 1:已确认/审核通过, 2:被驳回/申诉中。注：引擎只抓取=1的数据)',
    `effective_flag`   TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '版本生效标识 (1:当前生效版本, 0:历史作废版本。用于处理重新打分时的历史数据保留)',
    `taxable_flag`        TINYINT(1) NULL DEFAULT NULL COMMENT '计税标识: NULL-继承全局/档案, 0-不计税, 1-计税 (KPI绩效)',
    `delete_flag`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标识 (0:未删除, >0:已删除)',
    `create_by`        VARCHAR(64)   NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        VARCHAR(64)   NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_emp_period_del` (`employee_id`, `period_id`, `delete_flag`),
    KEY                `idx_month_status` (`settlement_month`, `audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工月度绩效考核记录表';
-- ==========================================================
-- 23.薪资周期专项调整表 ( salary_adjustment )
-- ==========================================================
CREATE TABLE `salary_adjustment`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `employee_id`       bigint(20) NOT NULL COMMENT '员工ID',
    `period_id`         bigint(20) NOT NULL COMMENT '关联核算周期ID (本记录仅在该周期生效)',

    `item_code`         varchar(50)    NOT NULL COMMENT '薪资项编码 (如：HOLIDAY_BONUS, LATE_DEDUCTION)',
    `item_name`         varchar(50)    NOT NULL COMMENT '项目名称 (如：中秋节礼金, 迟到扣款)',

    `currency`          varchar(20)     NOT NULL DEFAULT 'CNY' COMMENT '原币种代码 (ISO 4217, 如 CNY, USD)',
    `original_amount`   decimal(15, 4) NOT NULL DEFAULT '0.0000' COMMENT '原币发生金额',
    `exchange_rate`     decimal(10, 6) NOT NULL DEFAULT '1.000000' COMMENT '当期核算汇率 (原币兑换本币的汇率)',
    `settlement_amount` decimal(15, 4) NOT NULL DEFAULT '0.0000' COMMENT '折算本币金额 (实际参与引擎运算的金额)',

    `adjust_type`       tinyint(4) NOT NULL COMMENT '调账类型: 1-增加(发钱), 2-扣减(扣钱)',
    `source_type`       tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据来源: 1-手工录入, 2-系统生成, 3-API对接',
    `status`            tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态: 0-草稿, 1-已生效(参与算薪)',
    `taxable_flag`        TINYINT(1) NOT NULL DEFAULT 0 COMMENT '计税标识: 0-不计税(默认), 1-计税',
    `remark`            varchar(255)            DEFAULT NULL COMMENT '调账原因及备注 (审计依据)',

    `delete_flag`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标识 (0:未删除, >0:已删除)',
    `create_by`        VARCHAR(64)   NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        VARCHAR(64)   NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',

    PRIMARY KEY (`id`),
    KEY                 `idx_period_emp` (`period_id`,`employee_id`),
    KEY                 `idx_item_code` (`item_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资周期专项调整表 (处理各类动态奖金与扣款)';

-- =========================================================
-- RBAC 权限系统初始化数据脚本
-- =========================================================

-- ==========================================================
-- 1. 初始化用户数据 (sys_user)
-- 初始密码均为: (密码统一为 123456 的 BCrypt 加密串)
-- ==========================================================
INSERT INTO `sys_user` (`id`, `username`, `password`, `salt`, `nickname`, `email`, `phone`, `sex`, `status`,
                        `create_by`)
VALUES (1, 'system', '$2a$10$4FGmjRDysGbW1t0yPDGxg.99sA3Qf97aHM0yhB6R4vektOZ/d3GFu', '', '系统维护员',
        'system@example.com', '13800000001', 1, 1, 'system'),
       (2, 'admin', '$2a$10$4FGmjRDysGbW1t0yPDGxg.99sA3Qf97aHM0yhB6R4vektOZ/d3GFu', '', '薪资管理员',
        'admin@example.com', '13800000002', 2, 1, 'system'),
       (3, 'user', '$2a$10$4FGmjRDysGbW1t0yPDGxg.99sA3Qf97aHM0yhB6R4vektOZ/d3GFu', '', '普通员工', 'user@example.com',
        '13800000003', 1, 1, 'system'),
       (4, 'test', '$2a$10$4FGmjRDysGbW1t0yPDGxg.99sA3Qf97aHM0yhB6R4vektOZ/d3GFu', '', '测试账号', 'test@example.com',
        '13800000004', 0, 1, 'system');

-- ==========================================================
-- 2. 初始化角色数据
-- ==========================================================
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_sort`, `role_status`, `role_desc`, `remark`, `create_by`)
VALUES (1, '超级管理员', 'SUPER_ADMIN', 1, 1, '系统最高权限', '拥有系统所有资源和操作权限', 'system'),
       (2, '普通管理员', 'ADMIN', 2, 1, '普通管理员权限', '普通管理员权限', 'system'),
       (3, '普通员工', 'USER', 2, 1, '普通员工权限', '普通业务线办理权限', 'system'),
       (4, '测试人员', 'TEST', 3, 1, '测试人员权限', '仅用于查看系统的只读账号', 'system');

-- ==========================================================
-- 3. 初始化用户角色关联 (sys_user_role)
-- ==========================================================
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `create_by`)
VALUES (1, 1, 'system'), -- system -> 超级管理员
       (2, 2, 'system'), -- admin  -> 普通管理员
       (3, 3, 'system'), -- user   -> 普通员工
       (4, 4, 'system');
-- test   -> 测试人员

-- ==========================================================
-- 4. 初始化菜单与权限数据 (严格对应前端 Layout 与 views 目录结构)
-- 菜单类型 (1:目录, 2:菜单, 3:按钮)
-- ==========================================================
TRUNCATE TABLE `sys_menu`;

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`,
                        `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`)
VALUES
-- ==========================================================
-- 模块 A：工作台与系统基础管理
-- ==========================================================
(1, '工作台', 'dashboard_dir', '/dashboard', 'Layout', '/dashboard/index', 'Odometer', '', 1, 0, 1, 1, 1),
(2, '工作台面板', 'dashboard_index', 'index', 'dashboard/index', '', 'DataBoard', '', 2, 1, 1, 1, 1),

(10, '系统管理', 'sys_dir', '/system', 'Layout', '', 'Setting', '', 1, 0, 2, 1, 1),
-- 用户管理
(100, '用户管理', 'sys_user', 'user', 'system/user/UserPage', '', 'User', 'sys:user:list', 2, 10, 1, 1, 1),
(101, '用户查询', 'sys_user_query', '', '', '', '', 'sys:user:query', 3, 100, 1, 1, 1),
(102, '用户新增', 'sys_user_add', '', '', '', '', 'sys:user:add', 3, 100, 2, 1, 1),
(103, '用户修改', 'sys_user_edit', '', '', '', '', 'sys:user:edit', 3, 100, 3, 1, 1),
(104, '用户删除', 'sys_user_del', '', '', '', '', 'sys:user:del', 3, 100, 4, 1, 1),
(105, '分配角色', 'sys_user_assign', '', '', '', '', 'sys:user:assign', 3, 100, 5, 1, 1),
-- 角色管理
(110, '角色管理', 'sys_role', 'role', 'system/role/RolePage', '', 'Avatar', 'sys:role:list', 2, 10, 2, 1, 1),
(111, '角色查询', 'sys_role_query', '', '', '', '', 'sys:role:query', 3, 110, 1, 1, 1),
(112, '角色新增', 'sys_role_add', '', '', '', '', 'sys:role:add', 3, 110, 2, 1, 1),
(113, '角色修改', 'sys_role_edit', '', '', '', '', 'sys:role:edit', 3, 110, 3, 1, 1),
(114, '角色删除', 'sys_role_del', '', '', '', '', 'sys:role:del', 3, 110, 4, 1, 1),
(115, '分配权限', 'sys_role_assign', '', '', '', '', 'sys:role:assign', 3, 110, 5, 1, 1),
-- 菜单管理
(120, '菜单管理', 'sys_menu', 'menu', 'system/menu/MenuPage', '', 'Menu', 'sys:menu:list', 2, 10, 3, 1, 1),
(121, '菜单查询', 'sys_menu_query', '', '', '', '', 'sys:menu:query', 3, 120, 1, 1, 1),
(122, '菜单新增', 'sys_menu_add', '', '', '', '', 'sys:menu:add', 3, 120, 2, 1, 1),
(123, '菜单修改', 'sys_menu_edit', '', '', '', '', 'sys:menu:edit', 3, 120, 3, 1, 1),
(124, '菜单删除', 'sys_menu_del', '', '', '', '', 'sys:menu:del', 3, 120, 4, 1, 1),
-- 字典管理
(130, '字典管理', 'sys_dict', 'dict', 'system/dict/DictPage', '', 'Collection', 'sys:dict:list', 2, 10, 4, 1, 1),
(131, '新增字典类型', 'sys_dict_type_add', '', '', '', '', 'sys:dict_type:add', 3, 130, 1, 1, 1),
(132, '删除字典类型', 'sys_dict_type_del', '', '', '', '', 'sys:dict_type:del', 3, 130, 2, 1, 1),
(133, '修改字典类型', 'sys_dict_type_edit', '', '', '', '', 'sys:dict_type:edit', 3, 130, 3, 1, 1),
(134, '查询字典类型', 'sys_dict_type_query', '', '', '', '', 'sys:dict_type:query', 3, 130, 4, 1, 1),
(135, '新增字段项', 'sys_dict_item_add', '', '', '', '', 'sys:dict_item:add', 3, 130, 5, 1, 1),
(136, '删除字典项', 'sys_dict_item_del', '', '', '', '', 'sys:dict_item:del', 3, 130, 6, 1, 1),
(137, '修改字典项', 'sys_dict_item_edit', '', '', '', '', 'sys:dict_item:edit', 3, 130, 7, 1, 1),
(138, '查询字典类型', 'sys_dict_item_query', '', '', '', '', 'sys:dict_item:query', 3, 130, 8, 1, 1),

-- 【0】薪资管理 (父级主目录)
(150, '薪资管理', 'salary_manage', '/salary', 'Layout', '', 'Money', '', 1, 0, 10, 1, 1),

-- ==========================================================
-- 【1】员工基础档案 (子菜单，Sort = 1，160号段)
-- ==========================================================
(160, '员工基础档案', 'salary_employee', 'employee', 'salary/employee/EmployeePage', '', 'User', 'salary:employee:list',
 2, 150, 1, 1, 1),
(161, '查看员工列表', 'salary_employee_query', '', '', '', '', 'salary:employee:query', 3, 160, 1, 1, 1),
(162, '新增员工档案', 'salary_employee_add', '', '', '', '', 'salary:employee:add', 3, 160, 2, 1, 1),
(163, '修改员工档案', 'salary_employee_edit', '', '', '', '', 'salary:employee:edit', 3, 160, 3, 1, 1),
(164, '销毁员工档案', 'salary_employee_del', '', '', '', '', 'salary:employee:del', 3, 160, 4, 1, 1),
(165, '查看档案详情', 'salary_employee_detail', '', '', '', '', 'salary:employee:detail', 3, 160, 5, 1, 1),

-- ==========================================================
-- 【2】薪资周期管理 (子菜单，Sort = 2，170号段)
-- ==========================================================
(170, '薪资周期管理', 'salary_period', 'period', 'salary/period/PeriodPage', '', 'Calendar', 'salary:period:list', 2,
 150, 2, 1, 1),
(171, '查看周期列表', 'salary_period_query', '', '', '', '', 'salary:period:query', 3, 170, 1, 1, 1),
(172, '新增/开启周期', 'salary_period_add', '', '', '', '', 'salary:period:add', 3, 170, 2, 1, 1),
(173, '修改周期数据', 'salary_period_edit', '', '', '', '', 'salary:period:edit', 3, 170, 3, 1, 1),
(174, '批量初始化周期', 'salary_period_init', '', '', '', '', 'salary:period:init', 3, 170, 4, 1, 1),
(175, '删除/销毁周期', 'salary_period_del', '', '', '', '', 'salary:period:del', 3, 170, 5, 1, 1),

-- ==========================================================
-- 【3】薪资项目配置 (子菜单，Sort = 3，180号段)
-- ==========================================================
(180, '薪资项目配置', 'salary_item_config', 'itemconfig', 'salary/itemconfig/ItemConfigPage', '', 'Setting',
 'salary:item_config:list', 2, 150, 3, 1, 1),
(181, '新增项目', 'salary_item_add', '', '', '', '', 'salary:item_config:add', 3, 180, 1, 1, 1),
(182, '修改项目', 'salary_item_edit', '', '', '', '', 'salary:item_config:edit', 3, 180, 2, 1, 1),
(183, '删除项目', 'salary_item_del', '', '', '', '', 'salary:item_config:del', 3, 180, 3, 1, 1),
(184, '同步配置', 'salary_item_refresh', '', '', '', '', 'salary:item_config:refresh', 3, 180, 4, 1, 1),

-- ==========================================================
-- 【4】薪资档案管理 (子菜单，Sort = 4，190号段)
-- ==========================================================
(190, '薪资档案管理', 'salary_archive', 'archive', 'salary/archive/ArchivePage', '', 'Document', 'salary:archive:list',
 2, 150, 4, 1, 1),
(191, '新员工定薪', 'salary_archive_init', '', '', '', '', 'salary:archive:init', 3, 190, 1, 1, 1),
(192, '调薪申请', 'salary_archive_adjust', '', '', '', '', 'salary:archive:adjust', 3, 190, 2, 1, 1),
(193, '调薪审批', 'salary_archive_audit', '', '', '', '', 'salary:archive:audit', 3, 190, 3, 1, 1),
(194, '查看详情', 'salary_archive_detail', '', '', '', '', 'salary:archive:detail', 3, 190, 4, 1, 1),
(195, '导出档案', 'salary_archive_export', '', '', '', '', 'salary:archive:export', 3, 190, 5, 1, 1),

-- ==========================================================
-- 【5】月度绩效大盘管理 (🔥 新增子菜单，Sort = 5，200号段)
-- ==========================================================
(200, '月度绩效管理', 'salary_kpi_ecord', 'kpirecord', 'salary/kpirecord/KpiRecordPage', '', 'TrendCharts',
 'salary:kpi_record:list', 2, 150, 5, 1, 1),
(201, '查询绩效大盘', 'salary_kpi_record_query', '', '', '', '', 'salary:kpi_record:query', 3, 200, 1, 1, 1),
(202, '派发绩效单', 'salary_kpi_record_init', '', '', '', '', 'salary:kpi_record:init', 3, 200, 2, 1, 1),
(203, '评估打分', 'salary_kpi_record_evaluate', '', '', '', '', 'salary:kpi_record:evaluate', 3, 200, 3, 1, 1),
(204, '审核定稿', 'salary_kpi_record_confirm', '', '', '', '', 'salary:kpi_record:confirm', 3, 200, 4, 1, 1),

-- ==========================================================
-- 【6】薪资引擎配置 (目录级别，Sort = 6，统领 210~230 号段)
-- ==========================================================
(210, '薪资引擎配置', 'salary_engine', 'engine', '', '', 'Operation', '', 1, 150, 6, 1, 1),

-- 6.1 薪资引擎 -> 计算规则库 (220 号段，父级ID为 210)
(220, '计算规则库', 'salary_calc_rule', 'calc-rule', 'salary/calcrule/CalcRulePage', '', 'Collection',
 'salary:rule:list', 2, 210, 1, 1, 1),
(221, '查询规则', 'salary_rule_query', '', '', '', '', 'salary:rule:query', 3, 220, 1, 1, 1),
(222, '新增规则', 'salary_rule_add', '', '', '', '', 'salary:rule:add', 3, 220, 2, 1, 1),
(223, '修改规则', 'salary_rule_edit', '', '', '', '', 'salary:rule:edit', 3, 220, 3, 1, 1),
(224, '删除规则', 'salary_rule_del', '', '', '', '', 'salary:rule:del', 3, 220, 4, 1, 1),

-- 6.2 薪资引擎 -> 核算管道编排 (230 号段，父级ID为 210)
(230, '核算管道编排', 'salary_calc_pipeline', 'calc-pipeline', 'salary/calcpipeline/CalcPipelinePage', '', 'Connection',
 'salary:pipeline:list', 2, 210, 2, 1, 1),
(231, '查询管道', 'salary_pipeline_query', '', '', '', '', 'salary:pipeline:query', 3, 230, 1, 1, 1),
(232, '新建管道', 'salary_pipeline_add', '', '', '', '', 'salary:pipeline:add', 3, 230, 2, 1, 1),
(233, '修改管道元数据', 'salary_pipeline_edit', '', '', '', '', 'salary:pipeline:edit', 3, 230, 3, 1, 1),
(234, '删除管道', 'salary_pipeline_del', '', '', '', '', 'salary:pipeline:del', 3, 230, 4, 1, 1),
(235, '发布瀑布流配置', 'salary_pipeline_design', '', '', '', '', 'salary:pipeline:design', 3, 230, 5, 1, 1),
(236, '设为系统默认', 'salary_pipeline_default', '', '', '', '', 'salary:pipeline:default', 3, 230, 6, 1, 1),
(237, '升级新版本', 'salary_pipeline_upgrade', '', '', '', '', 'salary:pipeline:upgrade', 3, 230, 7, 1, 1),


-- ==========================================================
-- 【7】专项调整(手工账) (子菜单，Sort = 7，240号段)
-- ==========================================================
(240, '专项调整(手工账)', 'salary_adjustment', 'adjustment', 'salary/adjustment/AdjustmentPage', '', 'PriceTag',
 'salary:adjustment:list', 2, 150, 7, 1, 1),
(241, '查询手工账', 'salary_adjustment_query', '', '', '', '', 'salary:adjustment:query', 3, 240, 1, 1, 1),
(242, '新增手工账', 'salary_adjustment_add', '', '', '', '', 'salary:adjustment:add', 3, 240, 2, 1, 1),
(243, '修改手工账', 'salary_adjustment_edit', '', '', '', '', 'salary:adjustment:edit', 3, 240, 3, 1, 1),
(244, '删除手工账', 'salary_adjustment_del', '', '', '', '', 'salary:adjustment:del', 3, 240, 4, 1, 1),
(245, '批量生效/撤回', 'salary_adjustment_audit', '', '', '', '', 'salary:adjustment:audit', 3, 240, 5, 1, 1),

-- ==========================================================
-- 【8】薪资汇总与发薪 (子菜单，Sort = 8，250号段)
-- ==========================================================
(250, '薪资汇总与发薪', 'salary_summary', 'summary', 'salary/summary/SummaryPage', '', 'Wallet', 'salary:summary:list',
 2, 150, 8, 1, 1),
(251, '查看汇总列表', 'salary_summary_query', '', '', '', '', 'salary:summary:query', 3, 250, 1, 1, 1),
(252, '查看工资条明细', 'salary_summary_detail', '', '', '', '', 'salary:summary:detail', 3, 250, 2, 1, 1),
(253, '锁定与解锁单据', 'salary_summary_lock', '', '', '', '', 'salary:summary:lock', 3, 250, 3, 1, 1),
(254, '执行引擎核算', 'salary_summary_calc', '', '', '', '', 'salary:summary:calc', 3, 250, 4, 1, 1);

-- 5. 初始化角色菜单关联 (sys_role_menu)
-- ==========================================================

-- ----------------------------------------------------------
-- 5.1 超级管理员 (SUPER_ADMIN): 拥有所有菜单和按钮权限 (ID: 1-124)
-- ----------------------------------------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
SELECT 1, id, 'system'
FROM `sys_menu`;

-- ----------------------------------------------------------
-- 5.2 普通管理员 (ADMIN): 拥有工作台 + 系统管理(除敏感操作)
-- ----------------------------------------------------------
-- 基础目录与工作台
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
VALUES (2, 1, 'system'),
       (2, 2, 'system'),
       (2, 10, 'system');

-- 用户/角色/菜单的查询、新增、修改权限 (剔除删除 104, 114, 124 和 分配权限 115)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
SELECT 2, id, 'system'
FROM `sys_menu`
WHERE id IN (100, 101, 102, 103, 105, 110, 111, 112, 113, 120, 121, 122, 123);

-- ----------------------------------------------------------
-- 5.3 普通员工 (USER) & 测试人员 (TEST): 仅工作台 + 基础查看
-- ----------------------------------------------------------
-- 工作台 (1, 2)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
VALUES (3, 1, 'system'),
       (3, 2, 'system'),
       (4, 1, 'system'),
       (4, 2, 'system');

-- 给测试人员增加系统管理的只读查看权限 (101, 111, 121)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
VALUES (4, 10, 'system'),
       (4, 100, 'system'),
       (4, 101, 'system'),
       (4, 110, 'system'),
       (4, 111, 'system'),
       (4, 120, 'system'),
       (4, 121, 'system');


-- ==========================================================
-- 6. 字典类型表 (sys_dict_type)
-- ==========================================================
-- ==========================================================
-- 初始化字典类型数据
-- ==========================================================
INSERT INTO `sys_dict_type` (`dict_type_code`, `dict_type_name`, `dict_category`, `remark`, `create_by`)
VALUES
-- 薪资模块 (salary)
('salary_item_category', '薪资项目大类', 'salary', '定义薪资项的物理分类：收入、扣款、税费等', 'system'),
('salary_item_sub_type', '薪资项目细类', 'salary', '定义具体的业务逻辑标识，用于代码或脚本识别', 'system'),
-- ------------
-- 财务模块 (finance)
('payment_channel', '支付打款渠道', 'finance', '出纳打款的资金渠道', 'system'),
('settlement_currency', '结算本位币种', 'finance', '用于薪资计算和发放的币种', 'system'),

-- 人事模块 (hr)
('employment_status', '员工在职状态', 'hr', '影响薪资周期计算的状态', 'system');

-- ==========================================================
-- 7. 字典项明细表 (sys_dict_item) - 严格对应你调整后的字段 dict_item_label
-- ==========================================================
-- ==========================================================
-- 初始化字典明细项数据
-- ==========================================================
INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`, `create_by`)
VALUES

    ('salary_item_category', '1', '收入', 10, 'system'),
    ('salary_item_category', '2', '扣款', 20, 'system'),
    ('salary_item_category', '3', '税费', 30, 'system'),
    ('salary_item_category', '4', '公司支出', 40, 'system'),
    ('salary_item_sub_type', 'INC_BASE', '基本工资', 10, 'system'),
    ('salary_item_sub_type', 'INC_ALLOWANCE', '岗位津贴', 20, 'system'),
    ('salary_item_sub_type', 'INC_OVERTIME', '加班工资', 30, 'system'),
    ('salary_item_sub_type', 'INC_ATTENDANCE', '全勤奖', 35, 'system'),
    ('salary_item_sub_type', 'INC_BONUS', '绩效奖金', 40, 'system'),
    ('salary_item_sub_type', 'INC_YEAR_END', '年终奖', 50, 'system'),
    ('salary_item_sub_type', 'INC_SUBSIDY', '补贴', 60, 'system'),
    ('salary_item_sub_type', 'INC_FESTIVAL', '节日礼金', 70, 'system'),
    ('salary_item_sub_type', 'INC_OTHER', '其他收入', 80, 'system'),
    ('salary_item_sub_type', 'DED_ABSENT', '缺勤扣款', 100, 'system'),
    ('salary_item_sub_type', 'DED_LATE', '迟到早退', 110, 'system'),
    ('salary_item_sub_type', 'DED_FINE', '罚款', 115, 'system'),
    ('salary_item_sub_type', 'DED_LOAN', '借款扣还', 118, 'system'),
    ('salary_item_sub_type', 'DED_OTHER', '其他扣款', 120, 'system'),

    ('salary_item_sub_type', 'TAX_INCOME', '个人所得税', 200, 'system'),
    ('salary_item_sub_type', 'SI_PENSION', '养老保险(个人)', 210, 'system'),
    ('salary_item_sub_type', 'SI_MED', '医疗保险(个人)', 220, 'system'),
    ('salary_item_sub_type', 'SI_UNEMPLOYMENT', '失业保险(个人)', 225, 'system'),
    ('salary_item_sub_type', 'SI_HOUSING', '住房公积金(个人)', 230, 'system'),
    ('salary_item_sub_type', 'PHP_SSS', 'SSS (菲律宾社保)', 240, 'system'),
    ('salary_item_sub_type', 'TAX_LOCAL', '地方税', 250, 'system'),

    ('salary_item_sub_type', 'ER_PENSION', '养老保险(公司缴纳)', 300, 'system'),
    ('salary_item_sub_type', 'ER_MED', '医疗保险(公司缴纳)', 310, 'system'),
    ('salary_item_sub_type', 'ER_HOUSING', '住房公积金(公司缴纳)', 320, 'system'),
    ('salary_item_sub_type', 'ER_VISA', '签证费用', 330, 'system'),
    ('salary_item_sub_type', 'ER_TRAVEL', '差旅费', 340, 'system'),
    ('salary_item_sub_type', 'ER_INSURANCE', '商业保险', 350, 'system'),
    ('salary_item_sub_type', 'ER_OTHER', '其他公司支出', 360, 'system'),

-- ==========================================
-- 5. 支付打款渠道
-- ==========================================
('payment_channel', 'bank_transfer_cmb', '招商银行企业代发', 10, 'system'),
('payment_channel', 'bank_transfer_icbc', '工商银行企业代发', 20, 'system'),
('payment_channel', 'bank_transfer_bdo', 'BDO Unibank', 30, 'system'),
('payment_channel', 'wallet_gcash', 'GCash 企业转账', 40, 'system'),
('payment_channel', 'alipay_batch', '支付宝批量代发', 50, 'system'),
('payment_channel', 'overseas_swift', '跨境电汇(SWIFT)', 60, 'system'),

-- ==========================================
-- 6. 结算本位币种
-- ==========================================
('settlement_currency', 'CNY', '人民币 (CNY)', 10, 'system'),
('settlement_currency', 'PHP', '菲律宾比索 (PHP)', 20, 'system'),
('settlement_currency', 'USDT', '泰达币(USDT)', 30, 'system'),
('settlement_currency', 'USD', '美元 (USD)', 40, 'system'),

-- ==========================================
-- 7. 员工在职状态
-- ==========================================
('employment_status', '1', '正式员工', 10, 'system'),
('employment_status', '2', '试用期员工', 20, 'system'),
('employment_status', '3', '实习生', 30, 'system'),
('employment_status', '4', '兼职/外包', 40, 'system'),
('employment_status', '0', '已离职', 50, 'system');




-- 分配给普通管理员 (ADMIN ID: 2, 剔除删除权限)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
SELECT 2, id, 'system'
FROM `sys_menu`
WHERE id IN (130, 131, 132, 133, 135);



-- ==========================================================
-- 9. 统一标准化：系统薪资项目配置表 (salary_item_config)
-- 标准：env_var_name 严格遵循 lowerCamelCase (小驼峰)
-- ==========================================================
TRUNCATE TABLE `salary_item_config`;

-- 建议：如果表内已有数据，可以先 TRUNCATE TABLE salary_item_config;
INSERT INTO `salary_item_config`
(`item_code`, `item_name`, `item_category`, `category_dict_value`, `env_var_name`, `calc_priority`, `taxable_flag`, `tax_deductible_flag`, `fixed_flag`, `pinyin_code`, `sort_value`, `remark`)
VALUES
-- ----------------------------------------------------------
-- 【1】收入类 - 档案固定项 (Fixed Items)
-- ----------------------------------------------------------
('BASE_SALARY', '基本工资', 1, 'INC_BASE', 'baseSalary', 10, 1, 0, 1, 'jbgz', 10, '核心底薪'),
('HOUSING_ALLOW', '住房补贴', 1, 'INC_ALLOWANCE', 'housingAllow', 11, 1, 0, 1, 'zfbt', 11, '每月固定房补'),
('MEAL_ALLOW', '餐补', 1, 'INC_ALLOWANCE', 'mealAllow', 12, 0, 0, 1, 'cb', 12, '固定餐补'),
('SHIFT_12H_ALLOWANCE', '12小时补贴', 1, 'INC_ALLOWANCE', 'shift12hAllowance', 13, 1, 0, 1, '12xsbt', 13, '特殊排班补贴'),
('QUARANTINE_ALLOWANCE', '隔离补贴', 1, 'INC_SUBSIDY', 'quarantineAllowance', 14, 1, 0, 0, 'glbt', 14, '特殊隔离补贴'),
('OTHER_ALLOWANCE', '其他补贴', 1, 'INC_ALLOWANCE', 'otherAllowance', 19, 1, 0, 0, 'qtbt', 19, '非固定通用补贴'),

-- ----------------------------------------------------------
-- 【2】收入类 - 动态变动项 (Attendance & Performance)
-- ----------------------------------------------------------
('OVERTIME_PAY_DAY', '日加班工资', 1, 'INC_OVERTIME', 'overtimePayDay', 20, 1, 0, 0, 'rjbgz', 20, '按天加班费'),
('OVERTIME_PAY_HOUR', '时加班工资', 1, 'INC_OVERTIME', 'overtimePayHour', 21, 1, 0, 0, 'sjbgz', 21, '按时加班费'),
('KPI_BONUS', 'KPI绩效', 1, 'INC_BONUS', 'kpiBonus', 30, 1, 0, 0, 'kpi', 30, '月度绩效'),
('COMMISSION_SALES', '业绩提成', 1, 'INC_BONUS', 'commissionSales', 31, 1, 0, 0, 'yjtc', 31, '业务提成'),
('COMMISSION_AGENT', '代理提成', 1, 'INC_BONUS', 'commissionAgent', 32, 1, 0, 0, 'dltc', 32, '代理提成'),
('ATTENDANCE_BONUS', '全勤奖', 1, 'INC_ATTENDANCE', 'attendanceBonus', 40, 1, 0, 0, 'qqj', 40, '全勤奖金'),
('ATTENDANCE_REISSUE', '考勤/薪资补发', 1, 'INC_ATTENDANCE', 'attendanceReissue', 41, 1, 0, 0, 'kqbf', 41, '漏打卡或考勤误差补发'),

-- ----------------------------------------------------------
-- 【3】各类奖励与节日福利 (Bonus & Festival)
-- ----------------------------------------------------------
('SAFETY_CARD_BONUS', '安全卡奖励', 1, 'INC_OTHER', 'safetyCardBonus', 50, 1, 0, 0, 'aqkjl', 50, '安全奖励'),
('ANNUAL_LEAVE_BONUS', '年假奖金', 1, 'INC_OTHER', 'annualLeaveBonus', 51, 1, 0, 0, 'njjj', 51, '年假折现'),
('REFERRAL_BONUS', '内推奖金', 1, 'INC_OTHER', 'referralBonus', 52, 1, 0, 0, 'ntjj', 52, '内推奖励'),
('BIRTHDAY_BONUS', '生日礼金', 1, 'INC_FESTIVAL', 'birthdayBonus', 53, 0, 0, 0, 'srlj', 53, '生日福利'),

-- 节日现金/实物
('FESTIVAL_SPRING_GIFT', '春节福利', 1, 'INC_FESTIVAL', 'festivalSpringGift', 60, 1, 0, 0, 'cjfw', 60, '实物'),
('FESTIVAL_SPRING_BONUS', '春节礼金', 1, 'INC_FESTIVAL', 'festivalSpringBonus', 61, 1, 0, 0, 'cjlj', 61, '现金'),
('FESTIVAL_DRAGON_BOAT_GIFT', '端午节福利', 1, 'INC_FESTIVAL', 'festivalDragonBoatGift', 62, 1, 0, 0, 'dwfw', 62, '实物'),
('FESTIVAL_DRAGON_BOAT_BONUS', '端午节礼金', 1, 'INC_FESTIVAL', 'festivalDragonBoatBonus', 63, 1, 0, 0, 'dwlj', 63, '现金'),
('FESTIVAL_MID_AUTUMN_GIFT', '中秋节福利', 1, 'INC_FESTIVAL', 'festivalMidAutumnGift', 64, 1, 0, 0, 'zqfw', 64, '实物'),
('FESTIVAL_MID_AUTUMN_BONUS', '中秋节礼金', 1, 'INC_FESTIVAL', 'festivalMidAutumnBonus', 65, 1, 0, 0, 'zqlj', 65, '现金'),

-- 赛事激励
('EVENT_EURO_CUP', '欧洲杯激励奖金', 1, 'INC_BONUS', 'eventEuroCup', 70, 1, 0, 0, 'ozb', 70, '欧洲杯奖金'),
('EVENT_WORLD_CUP', '世界杯激励奖金', 1, 'INC_BONUS', 'eventWorldCup', 71, 1, 0, 0, 'sjb', 71, '世界杯奖金'),

-- 年终奖系列 (统一移除小数点，使用小驼峰)
('ANNUAL_BONUS_13', '年终奖13薪', 1, 'INC_YEAR_END', 'annualBonus13', 77, 1, 0, 0, 'nzj13', 77, '13薪'),
('ANNUAL_BONUS_13_5', '年终奖13.5薪', 1, 'INC_YEAR_END', 'annualBonus135', 78, 1, 0, 0, 'nzj135', 78, '13.5薪'),
('ANNUAL_BONUS_14', '年终奖14薪', 1, 'INC_YEAR_END', 'annualBonus14', 79, 1, 0, 0, 'nzj14', 79, '14薪'),
('ANNUAL_BONUS_14_5', '年终奖14.5薪', 1, 'INC_YEAR_END', 'annualBonus14_5', 80, 1, 0, 0, 'nzj14.5', 80, '14.5薪'),
('ANNUAL_BONUS_15', '年终奖15薪', 1, 'INC_YEAR_END', 'annualBonus15', 81, 1, 0, 0, 'nzj15', 81, '15薪'),
('ANNUAL_BONUS_15_5', '年终奖15.5薪', 1, 'INC_YEAR_END', 'annualBonus15_5', 82, 1, 0, 0, 'nzj15.5', 82, '15.5薪'),
('ANNUAL_BONUS_16', '年终奖16薪', 1, 'INC_YEAR_END', 'annualBonus16', 83, 1, 0, 0, 'nzj16', 83, '16薪'),
('ANNUAL_BONUS_16_5', '年终奖16.5薪', 1, 'INC_YEAR_END', 'annualBonus16_5', 84, 1, 0, 0, 'nzj16.5', 84, '16.5薪'),
('ANNUAL_BONUS_17', '年终奖17薪', 1, 'INC_YEAR_END', 'annualBonus17', 85, 1, 0, 0, 'nzj17', 85, '17薪'),
('ANNUAL_BONUS_17_5', '年终奖17.5薪', 1, 'INC_YEAR_END', 'annualBonus17_5', 86, 1, 0, 0, 'nzj17.5', 86, '17.5薪'),
('ANNUAL_BONUS_18', '年终奖18薪', 1, 'INC_YEAR_END', 'annualBonus18', 87, 1, 0, 0, 'nzj18', 87, '18薪'),
('ANNUAL_BONUS_18_5', '年终奖18.5薪', 1, 'INC_YEAR_END', 'annualBonus185', 88, 1, 0, 0, 'nzj185', 88, '18.5薪'),
('ANNUAL_BONUS_19', '年终奖19薪', 1, 'INC_YEAR_END', 'annualBonus19', 89, 1, 0, 0, 'nzj19', 89, '19薪'),

-- 忠诚奖
('LOYALTY_BONUS_2Y', '忠诚奖金(二年度)', 1, 'INC_BONUS', 'loyaltyBonus2y', 90, 1, 0, 0, 'zcj2', 90, '满2年'),
('LOYALTY_BONUS_5Y', '忠诚奖金(五年度)', 1, 'INC_BONUS', 'loyaltyBonus5y', 91, 1, 0, 0, 'zcj5', 91, '满5年'),
('LOYALTY_BONUS_10Y', '忠诚奖金(十年度)', 1, 'INC_BONUS', 'loyaltyBonus10y', 92, 1, 0, 0, 'zcj10', 92, '满10年'),

-- ----------------------------------------------------------
-- 【4】返还/报销项 (Rebate/Reimbursement - 对应扣款)
-- ----------------------------------------------------------
('EXPENSE_REIMBURSE_ONBOARD', '入职费用报销', 1, 'INC_OTHER', 'expenseReimburseOnboard', 95, 0, 0, 0, 'rzbx', 95, '免税报销'),
('DEPOSIT_REFUND_CURRENT', '本月押金返还', 1, 'INC_OTHER', 'depositRefundCurrent', 96, 0, 0, 0, 'yjfh', 96, '对应押金扣除'),
('FINE_REBATE', '管理罚款返还', 1, 'INC_OTHER', 'fineRebate', 97, 0, 0, 0, 'fkfh', 97, '罚款申诉退回'),
('UTILITY_REBATE', '水电网费返还', 1, 'INC_OTHER', 'utilityRebate', 98, 0, 0, 0, 'sdwfh', 98, '水电费多扣返还'),
('PASSPORT_FEE_REBATE', '护照费用返还', 1, 'INC_OTHER', 'passportFeeRebate', 99, 0, 0, 0, 'hzfh', 99, '护照费多扣返还'),

-- ----------------------------------------------------------
-- 【5】扣款类 (Deductions)
-- ----------------------------------------------------------
('ABSENT_DEDUCTION', '缺勤扣款', 2, 'DED_ABSENT', 'absentDeduction', 100, 0, 1, 0, 'qqkk', 100, '税前扣'),
('LATE_DEDUCTION', '迟到早退扣款', 2, 'DED_LATE', 'lateDeduction', 110, 0, 1, 0, 'cdzt', 110, '税前扣'),
('UTILITY_DEDUCTION', '水电网扣款', 2, 'DED_OTHER', 'utilityDeduction', 120, 0, 0, 0, 'sdwkk', 120, '税后扣'),
('FINE_DEDUCTION', '管理罚款', 2, 'DED_FINE', 'fineDeduction', 121, 0, 0, 0, 'glfk', 121, '税后扣'),
('PASSPORT_FEE_DEDUCTION', '护照费用代扣', 2, 'DED_OTHER', 'passportFeeDeduction', 122, 0, 0, 0, 'hzdk', 122, '护照费'),
('DEPOSIT_DEDUCTION_CURRENT', '本月押金扣除', 2, 'DED_OTHER', 'depositDeductionCurrent', 123, 0, 0, 0, 'byyj', 123, '押金扣'),
('OTHER_DEDUCTION', '其他扣除', 2, 'DED_OTHER', 'otherDeduction', 124, 0, 0, 0, 'qtkc', 130, '通用非固定扣款'),
-- ----------------------------------------------------------
-- 【6】系统调整与结算
-- ----------------------------------------------------------
('RESIGNATION_SETTLEMENT', '离职费用结算', 2, 'DED_OTHER', 'resignationSettlement', 140, 0, 0, 0, 'lzjs', 140, '离职清算扣款'),
('PREV_MONTH_ADJUSTMENT', '上月补发/续扣', 1, 'INC_OTHER', 'prevMonthAdjustment', 141, 1, 0, 0, 'sybf', 141, '人工调账'),

-- ----------------------------------------------------------
-- 【7】税费与社保 (Personal SI & Tax)
-- ----------------------------------------------------------
('SI_PENSION_IND', '养老保险(个人)', 3, 'SI_PENSION', 'siPensionInd', 200, 0, 1, 1, 'ylbx', 200, '个人养老'),
('SI_MED_IND', '医疗保险(个人)', 3, 'SI_MED', 'siMedInd', 210, 0, 1, 1, 'ylbx', 210, '个人医疗'),
('SI_HOUSING_IND', '公积金(个人)', 3, 'SI_HOUSING', 'siHousingInd', 220, 0, 1, 1, 'gjj', 220, '个人公积金'),
('SI_REISSUE_IND', '个人社保退费/补发', 1, 'INC_OTHER', 'siReissueInd', 230, 0, 0, 0, 'sbgjjbf', 230, '社保多扣返还'),
('AUTO_TAX_CALC', '智能个税核算', 3, 'TAX_INCOME', 'autoTaxCalc', 999, 0, 0, 0, 'zngs', 999, '个税终结节点'),

-- ----------------------------------------------------------
-- 【8】公司成本 (Employer Cost - 不进个人工资条实发)
-- ----------------------------------------------------------
('ER_PENSION_COMP', '养老保险(公司)', 4, 'ER_PENSION', 'erPensionComp', 300, 0, 0, 1, 'ylbx', 300, '公司成本'),
('ER_VISA_COMP', '海外签证费用', 4, 'ER_VISA', 'erVisaComp', 310, 0, 0, 0, 'qzfy', 310, '公司承担签证');


INSERT INTO salary_calc_pipeline_info
(id, pipeline_code, pipeline_name, version, default_flag, status, remark, delete_flag, create_by, create_time, update_by, update_time)
VALUES
    (1, 'OFFICIAL_STAFF_2026', '2026年度正式员工核算流', 1, 0, 1, '本管道适用于集团 2026 年度全体正式员工月度核算。
制度依据：遵循 2026 版薪酬管理办法，包含基本工资、五险一金及各项绩效奖金。
逻辑特性：计算顺序严格遵循 [基础->补贴->扣款->税->汇总] 阶段，已同步 2026 年最新公积金缴存基数上限。
维护人：HR-薪酬组 / 技术支撑部', 0, 'system', '2026-04-02 14:11:34', 'system', '2026-04-02 14:11:34');

-- =================================================================================
-- 10. 初始化 薪资计算规则表 salary_calc_rule (全量59项大满贯版)
-- 标准：全部采用 env_var_name (小驼峰) + decimal() 强制防精度丢失 + nil 空值防御
-- =================================================================================
TRUNCATE TABLE `salary_calc_rule`;

INSERT INTO `salary_calc_rule`
(`rule_code`, `rule_name`, `rule_type`, `rule_script`, `return_type`, `sort_value`, `status`, `remark`)
VALUES
-- ----------------------------------------------------------
-- 【1】基础与考勤绩效 (Base & Perf)
-- ----------------------------------------------------------
('BASE_SALARY', '基本工资', 1, 'let base = (baseSalary == nil) ? 0.0M : decimal(baseSalary); let mode = (baseSalary_calcMode == nil) ? 1 : decimal(baseSalary_calcMode); let md = (monthDays == nil || monthDays <= 0M) ? 1.0M : decimal(monthDays); let att = (attendanceDays == nil) ? 0.0M : decimal(attendanceDays); mode == 1 ? base : (base / md * att)', 'Decimal', 10, 1, '底薪(按月固定全额/按出勤折算)'),
('HOUSING_ALLOW', '住房补贴', 1, 'let allow = (housingAllow == nil) ? 0.0M : decimal(housingAllow); let mode = (housingAllow_calcMode == nil) ? 1 : decimal(housingAllow_calcMode); let att = (attendanceDays == nil) ? 0.0M : decimal(attendanceDays); let off = (officeDays == nil) ? 0.0M : decimal(officeDays); let wfh = (wfhDays == nil) ? 0.0M : decimal(wfhDays); mode == 1 ? allow : (mode == 2 ? (allow * att) : (mode == 3 ? (allow * off) : (allow * wfh)))', 'Decimal', 11, 1, '房补(按月固定/日单价×天数)'),
('MEAL_ALLOW', '餐补', 1, 'let allow = (mealAllow == nil) ? 0.0M : decimal(mealAllow); let mode = (mealAllow_calcMode == nil) ? 1 : decimal(mealAllow_calcMode); let att = (attendanceDays == nil) ? 0.0M : decimal(attendanceDays); let off = (officeDays == nil) ? 0.0M : decimal(officeDays); let wfh = (wfhDays == nil) ? 0.0M : decimal(wfhDays); mode == 1 ? allow : (mode == 2 ? (allow * att) : (mode == 3 ? (allow * off) : (allow * wfh)))', 'Decimal', 12, 1, '餐补(按月固定/日单价×天数)'),
('SHIFT_12H_ALLOWANCE', '12小时补贴', 1, 'shift12hAllowance == nil ? 0.0M : decimal(shift12hAllowance)', 'Decimal', 13, 1, '排班补贴'),
('QUARANTINE_ALLOWANCE', '隔离补贴', 1, 'quarantineAllowance == nil ? 0.0M : decimal(quarantineAllowance)', 'Decimal', 14, 1, '隔离补贴'),
('OTHER_ALLOWANCE', '其他补贴', 1, 'otherAllowance == nil ? 0.0M : decimal(otherAllowance)', 'Decimal', 19, 1, '其他非固定补贴'),

('OVERTIME_PAY_DAY', '日加班工资', 1, 'overtimePayDay == nil ? 0.0M : decimal(overtimePayDay)', 'Decimal', 20, 1, '按天加班'),
('OVERTIME_PAY_HOUR', '时加班工资', 1, 'overtimePayHour == nil ? 0.0M : decimal(overtimePayHour)', 'Decimal', 21, 1, '按时加班'),
('KPI_BONUS', 'KPI绩效', 1, 'let base = (baseSalary == nil) ? 0.0M : decimal(baseSalary); let coeff = (kpiCoefficient == nil) ? 0.0M : decimal(kpiCoefficient); base * coeff', 'Decimal', 30, 1, '绩效系数'),
('COMMISSION_SALES', '业绩提成', 1, 'commissionSales == nil ? 0.0M : decimal(commissionSales)', 'Decimal', 31, 1, '销售提成'),
('COMMISSION_AGENT', '代理提成', 1, 'commissionAgent == nil ? 0.0M : decimal(commissionAgent)', 'Decimal', 32, 1, '代理提成'),
('ATTENDANCE_BONUS', '全勤奖', 1, '(isFullAttendance == true) ? (attendanceBonus == nil ? 0.0M : decimal(attendanceBonus)) : 0.0M', 'Decimal', 40, 1, '满勤触发'),
('ATTENDANCE_REISSUE', '考勤/薪资补发', 1, 'attendanceReissue == nil ? 0.0M : decimal(attendanceReissue)', 'Decimal', 41, 1, '漏打卡补发'),

-- ----------------------------------------------------------
-- 【2】福利、节日与赛事 (Bonus & Festival)
-- ----------------------------------------------------------
('SAFETY_CARD_BONUS', '安全卡奖励', 1, 'safetyCardBonus == nil ? 0.0M : decimal(safetyCardBonus)', 'Decimal', 50, 1, '安全奖励'),
('ANNUAL_LEAVE_BONUS', '年假奖金', 1, 'annualLeaveBonus == nil ? 0.0M : decimal(annualLeaveBonus)', 'Decimal', 51, 1, '年假折现'),
('REFERRAL_BONUS', '内推奖金', 1, 'referralBonus == nil ? 0.0M : decimal(referralBonus)', 'Decimal', 52, 1, '内推奖'),
('BIRTHDAY_BONUS', '生日礼金', 1, 'birthdayBonus == nil ? 0.0M : decimal(birthdayBonus)', 'Decimal', 53, 1, '生日红包'),

('FESTIVAL_SPRING_GIFT', '春节福利', 1, 'festivalSpringGift == nil ? 0.0M : decimal(festivalSpringGift)', 'Decimal', 60, 1, '春节实物'),
('FESTIVAL_SPRING_BONUS', '春节礼金', 1, 'festivalSpringBonus == nil ? 0.0M : decimal(festivalSpringBonus)', 'Decimal', 61, 1, '春节现金'),
('FESTIVAL_DRAGON_BOAT_GIFT', '端午节福利', 1, 'festivalDragonBoatGift == nil ? 0.0M : decimal(festivalDragonBoatGift)', 'Decimal', 62, 1, '端午实物'),
('FESTIVAL_DRAGON_BOAT_BONUS', '端午节礼金', 1, 'festivalDragonBoatBonus == nil ? 0.0M : decimal(festivalDragonBoatBonus)', 'Decimal', 63, 1, '端午现金'),
('FESTIVAL_MID_AUTUMN_GIFT', '中秋节福利', 1, 'festivalMidAutumnGift == nil ? 0.0M : decimal(festivalMidAutumnGift)', 'Decimal', 64, 1, '中秋实物'),
('FESTIVAL_MID_AUTUMN_BONUS', '中秋节礼金', 1, 'festivalMidAutumnBonus == nil ? 0.0M : decimal(festivalMidAutumnBonus)', 'Decimal', 65, 1, '中秋现金'),

('EVENT_EURO_CUP', '欧洲杯激励奖金', 1, 'eventEuroCup == nil ? 0.0M : decimal(eventEuroCup)', 'Decimal', 70, 1, '欧洲杯'),
('EVENT_WORLD_CUP', '世界杯激励奖金', 1, 'eventWorldCup == nil ? 0.0M : decimal(eventWorldCup)', 'Decimal', 71, 1, '世界杯'),

-- ----------------------------------------------------------
-- 【3】年终奖与忠诚奖 (Year End & Loyalty)
-- ----------------------------------------------------------
('ANNUAL_BONUS_13', '年终奖13薪', 1, 'annualBonus13 == nil ? 0.0M : decimal(annualBonus13)', 'Decimal', 79, 1, '13薪'),
('ANNUAL_BONUS_13_5', '年终奖13.5薪', 1, 'annualBonus135 == nil ? 0.0M : decimal(annualBonus135)', 'Decimal', 80, 1, '13.5薪'),
('ANNUAL_BONUS_14', '年终奖14薪', 1, 'annualBonus14 == nil ? 0.0M : decimal(annualBonus14)', 'Decimal', 81, 1, '14薪'),
('ANNUAL_BONUS_14_5', '年终奖14.5薪', 1, 'annualBonus145 == nil ? 0.0M : decimal(annualBonus145)', 'Decimal', 82, 1, '14.5薪'),
('ANNUAL_BONUS_15', '年终奖15薪', 1, 'annualBonus15 == nil ? 0.0M : decimal(annualBonus15)', 'Decimal', 83, 1, '15薪'),
('ANNUAL_BONUS_15_5', '年终奖15.5薪', 1, 'annualBonus155 == nil ? 0.0M : decimal(annualBonus155)', 'Decimal', 84, 1, '15.5薪'),
('ANNUAL_BONUS_16', '年终奖16薪', 1, 'annualBonus16 == nil ? 0.0M : decimal(annualBonus16)', 'Decimal', 85, 1, '16薪'),
('ANNUAL_BONUS_16_5', '年终奖16.5薪', 1, 'annualBonus165 == nil ? 0.0M : decimal(annualBonus165)', 'Decimal', 86, 1, '16.5薪'),
('ANNUAL_BONUS_17', '年终奖17薪', 1, 'annualBonus17 == nil ? 0.0M : decimal(annualBonus17)', 'Decimal', 87, 1, '17薪'),
('ANNUAL_BONUS_17_5', '年终奖17.5薪', 1, 'annualBonus175 == nil ? 0.0M : decimal(annualBonus175)', 'Decimal', 88, 1, '17.5薪'),
('ANNUAL_BONUS_18', '年终奖18薪', 1, 'annualBonus18 == nil ? 0.0M : decimal(annualBonus18)', 'Decimal', 89, 1, '18薪'),
('ANNUAL_BONUS_18_5', '年终奖18.5薪', 1, 'annualBonus185 == nil ? 0.0M : decimal(annualBonus185)', 'Decimal', 90, 1, '18.5薪'),
('ANNUAL_BONUS_19', '年终奖19薪', 1, 'annualBonus19 == nil ? 0.0M : decimal(annualBonus19)', 'Decimal', 91, 1, '19薪'),

('LOYALTY_BONUS_2Y', '忠诚奖金(二年度)', 1, 'loyaltyBonus2y == nil ? 0.0M : decimal(loyaltyBonus2y)', 'Decimal', 92, 1, '满2年'),
('LOYALTY_BONUS_5Y', '忠诚奖金(五年度)', 1, 'loyaltyBonus5y == nil ? 0.0M : decimal(loyaltyBonus5y)', 'Decimal', 93, 1, '满5年'),
('LOYALTY_BONUS_10Y', '忠诚奖金(十年度)', 1, 'loyaltyBonus10y == nil ? 0.0M : decimal(loyaltyBonus10y)', 'Decimal', 94, 1, '满10年'),

-- ----------------------------------------------------------
-- 【4】返还/报销项 (Rebates)
-- ----------------------------------------------------------
('EXPENSE_REIMBURSE_ONBOARD', '入职费用报销', 1, 'expenseReimburseOnboard == nil ? 0.0M : decimal(expenseReimburseOnboard)', 'Decimal', 95, 1, '免税报销'),
('DEPOSIT_REFUND_CURRENT', '本月押金返还', 1, 'depositRefundCurrent == nil ? 0.0M : decimal(depositRefundCurrent)', 'Decimal', 96, 1, '押金返还'),
('FINE_REBATE', '管理罚款返还', 1, 'fineRebate == nil ? 0.0M : decimal(fineRebate)', 'Decimal', 97, 1, '罚款申诉返还'),
('UTILITY_REBATE', '水电网费返还', 1, 'utilityRebate == nil ? 0.0M : decimal(utilityRebate)', 'Decimal', 98, 1, '多扣返还'),
('PASSPORT_FEE_REBATE', '护照费用返还', 1, 'passportFeeRebate == nil ? 0.0M : decimal(passportFeeRebate)', 'Decimal', 99, 1, '护照费返还'),

-- ----------------------------------------------------------
-- 【5】扣款与调账类 (Deductions & Adjustments)
-- ----------------------------------------------------------
('ABSENT_DEDUCTION', '缺勤扣款', 1, 'absentDeduction == nil ? 0.0M : decimal(absentDeduction)', 'Decimal', 100, 1, '税前扣'),
('LATE_DEDUCTION', '迟到早退扣款', 1, 'lateDeduction == nil ? 0.0M : decimal(lateDeduction)', 'Decimal', 110, 1, '税前扣'),
('UTILITY_DEDUCTION', '水电网扣款', 1, 'utilityDeduction == nil ? 0.0M : decimal(utilityDeduction)', 'Decimal', 120, 1, '税后扣'),
('FINE_DEDUCTION', '管理罚款', 1, 'fineDeduction == nil ? 0.0M : decimal(fineDeduction)', 'Decimal', 121, 1, '税后扣'),
('PASSPORT_FEE_DEDUCTION', '护照费用代扣', 1, 'passportFeeDeduction == nil ? 0.0M : decimal(passportFeeDeduction)', 'Decimal', 122, 1, '护照代扣'),
('DEPOSIT_DEDUCTION_CURRENT', '本月押金扣除', 1, 'depositDeductionCurrent == nil ? 0.0M : decimal(depositDeductionCurrent)', 'Decimal', 123, 1, '押金扣'),
('OTHER_DEDUCTION', '其他扣除', 1, 'otherDeduction == nil ? 0.0M : decimal(otherDeduction)', 'Decimal', 130, 1, '其他非固定扣款'),

('RESIGNATION_SETTLEMENT', '离职费用结算', 1, 'resignationSettlement == nil ? 0.0M : decimal(resignationSettlement)', 'Decimal', 140, 1, '离职清算扣款'),
('PREV_MONTH_ADJUSTMENT', '上月补发/续扣', 1, 'prevMonthAdjustment == nil ? 0.0M : decimal(prevMonthAdjustment)', 'Decimal', 141, 1, '人工回溯调账'),

-- ----------------------------------------------------------
-- 【6】社保、税费与公司成本 (SI, Tax & ER Costs)
-- ----------------------------------------------------------
('SI_PENSION_IND', '养老保险(个人)', 1, 'siPensionInd == nil ? 0.0M : decimal(siPensionInd)', 'Decimal', 200, 1, '个人社保'),
('SI_MED_IND', '医疗保险(个人)', 1, 'siMedInd == nil ? 0.0M : decimal(siMedInd)', 'Decimal', 210, 1, '个人社保'),
('SI_HOUSING_IND', '公积金(个人)', 1, 'siHousingInd == nil ? 0.0M : decimal(siHousingInd)', 'Decimal', 220, 1, '个人公积金'),
('SI_REISSUE_IND', '个人社保退费/补发', 1, 'siReissueInd == nil ? 0.0M : decimal(siReissueInd)', 'Decimal', 230, 1, '社保多扣补发'),

('ER_PENSION_COMP', '养老保险(公司)', 1, 'erPensionComp == nil ? 0.0M : decimal(erPensionComp)', 'Decimal', 300, 1, '公司成本'),
('ER_VISA_COMP', '海外签证费用', 1, 'erVisaComp == nil ? 0.0M : decimal(erVisaComp)', 'Decimal', 310, 1, '公司承担'),

('AUTO_TAX_CALC', '智能个税核算', 1, '0.0M', 'Decimal', 999, 1, '触发Java内置计税引擎');

TRUNCATE TABLE `salary_calc_pipeline_step`;

TRUNCATE TABLE `salary_calc_pipeline_step`;

INSERT INTO `salary_calc_pipeline_step`
(`pipeline_code`, `pipeline_version`, `rule_code`, `rule_name`, `rule_type`, `condition_script`, `stage`, `sort_order`, `block_flag`, `skip_if_null`, `status`)
VALUES
-- ----------------------------------------------------------
-- 【Stage 1: 基础与绩效计算】 权重 10-99
-- ----------------------------------------------------------
('OFFICIAL_STAFF_2026', 1, 'BASE_SALARY', '基本工资', 1, NULL, 1, 10, 1, 0, 1),
('OFFICIAL_STAFF_2026', 1, 'ATTENDANCE_BONUS', '全勤奖', 1, NULL, 1, 20, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'OVERTIME_PAY_DAY', '日加班工资', 1, NULL, 1, 30, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'OVERTIME_PAY_HOUR', '时加班工资', 1, NULL, 1, 40, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'KPI_BONUS', 'KPI绩效', 1, NULL, 1, 50, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'COMMISSION_SALES', '业绩提成', 1, NULL, 1, 60, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'COMMISSION_AGENT', '代理提成', 1, NULL, 1, 70, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ATTENDANCE_REISSUE', '考勤/薪资补发', 1, NULL, 1, 80, 0, 1, 1),

-- ----------------------------------------------------------
-- 【Stage 2: 补贴与各项奖励】 权重 100-399
-- ----------------------------------------------------------
('OFFICIAL_STAFF_2026', 1, 'HOUSING_ALLOW', '住房补贴', 1, NULL, 2, 100, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'MEAL_ALLOW', '餐补', 1, NULL, 2, 110, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'SHIFT_12H_ALLOWANCE', '12小时补贴', 1, NULL, 2, 120, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'QUARANTINE_ALLOWANCE', '隔离补贴', 1, NULL, 2, 130, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'OTHER_ALLOWANCE', '其他补贴', 1, NULL, 2, 135, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'SAFETY_CARD_BONUS', '安全卡奖励', 1, NULL, 2, 140, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_LEAVE_BONUS', '年假奖金', 1, NULL, 2, 150, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'REFERRAL_BONUS', '内推奖金', 1, NULL, 2, 160, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'BIRTHDAY_BONUS', '生日礼金', 1, NULL, 2, 170, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'FESTIVAL_SPRING_GIFT', '春节福利', 1, NULL, 2, 180, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FESTIVAL_SPRING_BONUS', '春节礼金', 1, NULL, 2, 190, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FESTIVAL_DRAGON_BOAT_GIFT', '端午节福利', 1, NULL, 2, 200, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FESTIVAL_DRAGON_BOAT_BONUS', '端午节礼金', 1, NULL, 2, 210, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FESTIVAL_MID_AUTUMN_GIFT', '中秋节福利', 1, NULL, 2, 220, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FESTIVAL_MID_AUTUMN_BONUS', '中秋节礼金', 1, NULL, 2, 230, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'EVENT_EURO_CUP', '欧洲杯激励奖金', 1, NULL, 2, 240, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'EVENT_WORLD_CUP', '世界杯激励奖金', 1, NULL, 2, 250, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_13', '年终奖13薪', 1, NULL, 2, 300, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_13_5', '年终奖13.5薪', 1, NULL, 2, 301, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_14', '年终奖14薪', 1, NULL, 2, 302, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_14_5', '年终奖14.5薪', 1, NULL, 2, 303, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_15', '年终奖15薪', 1, NULL, 2, 304, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_15_5', '年终奖15.5薪', 1, NULL, 2, 305, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_16', '年终奖16薪', 1, NULL, 2, 306, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_16_5', '年终奖16.5薪', 1, NULL, 2, 307, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_17', '年终奖17薪', 1, NULL, 2, 308, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_17_5', '年终奖17.5薪', 1, NULL, 2, 309, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_18', '年终奖18薪', 1, NULL, 2, 310, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_18_5', '年终奖18.5薪', 1, NULL, 2, 311, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ANNUAL_BONUS_19', '年终奖19薪', 1, NULL, 2, 312, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'LOYALTY_BONUS_2Y', '忠诚奖金(二年度)', 1, NULL, 2, 320, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'LOYALTY_BONUS_5Y', '忠诚奖金(五年度)', 1, NULL, 2, 330, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'LOYALTY_BONUS_10Y', '忠诚奖金(十年度)', 1, NULL, 2, 340, 0, 1, 1),

-- ----------------------------------------------------------
-- 【Stage 3: 考勤扣减与法务代扣】 权重 400-599
-- ----------------------------------------------------------
('OFFICIAL_STAFF_2026', 1, 'ABSENT_DEDUCTION', '缺勤扣款', 1, NULL, 3, 400, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'LATE_DEDUCTION', '迟到早退扣款', 1, NULL, 3, 410, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'UTILITY_DEDUCTION', '水电网扣款', 1, NULL, 3, 420, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FINE_DEDUCTION', '管理罚款', 1, NULL, 3, 430, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'PASSPORT_FEE_DEDUCTION', '护照费用代扣', 1, NULL, 3, 440, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'DEPOSIT_DEDUCTION_CURRENT', '本月押金扣除', 1, NULL, 3, 450, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'OTHER_DEDUCTION', '其他扣除', 1, NULL, 3, 455, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'SI_PENSION_IND', '养老保险(个人)', 1, NULL, 3, 460, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'SI_MED_IND', '医疗保险(个人)', 1, NULL, 3, 470, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'SI_HOUSING_IND', '公积金(个人)', 1, NULL, 3, 480, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ER_PENSION_COMP', '养老保险(公司)', 1, NULL, 3, 490, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'ER_VISA_COMP', '海外签证费用', 1, NULL, 3, 500, 0, 1, 1),

-- ----------------------------------------------------------
-- 【Stage 5: 汇总结算与返还调账】 权重 600-899
-- ----------------------------------------------------------
('OFFICIAL_STAFF_2026', 1, 'EXPENSE_REIMBURSE_ONBOARD', '入职费用报销', 1, NULL, 5, 600, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'DEPOSIT_REFUND_CURRENT', '本月押金返还', 1, NULL, 5, 610, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'FINE_REBATE', '管理罚款返还', 1, NULL, 5, 620, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'UTILITY_REBATE', '水电网费返还', 1, NULL, 5, 630, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'PASSPORT_FEE_REBATE', '护照费用返还', 1, NULL, 5, 640, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'SI_REISSUE_IND', '个人社保退费/补发', 1, NULL, 5, 650, 0, 1, 1),

('OFFICIAL_STAFF_2026', 1, 'PREV_MONTH_ADJUSTMENT', '上月补发/续扣', 1, NULL, 5, 660, 0, 1, 1),
('OFFICIAL_STAFF_2026', 1, 'RESIGNATION_SETTLEMENT', '离职费用结算', 1, NULL, 5, 670, 0, 1, 1),

-- ----------------------------------------------------------
-- 【Stage 4: 个税核心 (最终节点)】 权重 999
-- ----------------------------------------------------------
('OFFICIAL_STAFF_2026', 1, 'AUTO_TAX_CALC', '智能个税核算', 1, NULL, 4, 999, 1, 0, 1);

-- ============================================================================
-- ==================== V9 补丁 (企业级初始化增强) ============================
-- ============================================================================

-- ----------------------------------------------------------
-- 补丁 1：薪资汇总与发薪 补充按钮权限 (初始化账套 / 手工账)
-- 对齐前端 SummaryPage.vue 中的 v-hasPerm 权限点
-- ----------------------------------------------------------
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`)
VALUES
    (255, '初始化账套', 'salary_summary_init', '', '', '', '', 'salary:summary:init', 3, 250, 5, 1, 1),
    (256, '录入手工账', 'salary_summary_adjust', '', '', '', '', 'salary:summary:adjust', 3, 250, 6, 1, 1);

-- ----------------------------------------------------------
-- 补丁 2：薪资全局配置页面 (SalaryConfigPage) + 按钮权限
-- ----------------------------------------------------------
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`)
VALUES
    (260, '薪资全局配置', 'salary_config', 'config', 'salary/config/SalaryConfigPage', '', 'Tools', 'salary:config:list', 2, 150, 7, 1, 1),
    (261, '新增配置', 'salary_config_add', '', '', '', '', 'salary:config:add', 3, 260, 1, 1, 1),
    (262, '修改配置', 'salary_config_edit', '', '', '', '', 'salary:config:edit', 3, 260, 2, 1, 1);

-- ----------------------------------------------------------
-- 补丁 3：打款流水页面 (PaymentRecordPage) + 按钮权限
-- 说明：发薪台"流水"按钮跳转 /salary/paymentrecord 依赖此菜单生成动态路由
-- ----------------------------------------------------------
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `menu_path`, `menu_component`, `menu_redirect`, `menu_icon`, `menu_permission`, `menu_type`, `menu_parent_id`, `menu_sort`, `menu_visible`, `menu_status`)
VALUES
    (270, '打款流水', 'salary_payment_record', 'paymentrecord', 'salary/paymentrecord/PaymentRecordPage', '', 'Money', 'salary:record:list', 2, 150, 8, 1, 1),
    (271, '修正流水', 'salary_record_edit', '', '', '', '', 'salary:record:edit', 3, 270, 1, 1, 1);

-- ----------------------------------------------------------
-- 补丁 4：超级管理员 (role_id=1) 补齐上述新增菜单授权
-- ----------------------------------------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_by`)
SELECT 1, id, 'system'
FROM `sys_menu`
WHERE id IN (255, 256, 260, 261, 262, 270, 271);

-- ----------------------------------------------------------
-- 补丁 5：薪资全局配置种子数据
-- ----------------------------------------------------------
INSERT INTO `salary_config` (`config_key`, `config_value`, `config_name`, `config_type`, `config_group`, `active_flag`, `remark`, `create_by`)
VALUES
    ('SETTLEMENT_CURRENCY', 'CNY', '本位结算币种', 'string', 'base', 1, '系统默认结算币种', 'system'),
    ('TAX_ENABLED', 'true', '是否启用个税累计预扣', 'boolean', 'calc', 1, '关闭后个税恒为 0', 'system'),
    ('SOCIAL_INSURANCE_ENABLED', 'false', '是否启用社保自动计算', 'boolean', 'calc', 0, '开启后按比例自动计算五险一金 (预留)', 'system'),
    ('OVERTIME_RATE_DAY', '1.5', '平日加班费率倍数', 'number', 'calc', 1, '平日加班按 1.5 倍折算', 'system'),
    ('OVERTIME_RATE_WEEKEND', '2.0', '周末加班费率倍数', 'number', 'calc', 1, '休息日加班按 2 倍折算', 'system'),
    ('OVERTIME_RATE_HOLIDAY', '3.0', '法定节假日加班费率', 'number', 'calc', 1, '法定节假日 3 倍', 'system');

-- ----------------------------------------------------------
-- 补丁 6：AUTO_TAX_CALC 规则脚本说明 (引擎已内置 Java 累计预扣计税)
-- 说明：PipelineStepExecutor 对 AUTO_TAX_CALC 节点走 SalaryTaxCalculator 组件，
--       此处脚本仅为数据库占位，实际计算不依赖该表达式。
-- ----------------------------------------------------------
UPDATE `salary_calc_rule`
SET `rule_script` = '0.0M'
WHERE `rule_code` = 'AUTO_TAX_CALC';

-- ----------------------------------------------------------
-- 补丁 7：月度汇率表 (多币种结算支撑)
-- 用途：按 结算月份 × 币种 维护对基准币(CNY)的汇率，
--       引擎核算时按员工档案币种查询，未配置回退 1:1
-- ----------------------------------------------------------
CREATE TABLE `salary_exchange_rate`
(
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `settlement_month` CHAR(6)      NOT NULL COMMENT '结算月份 (YYYYMM)',
    `currency`        VARCHAR(16)  NOT NULL COMMENT '币种 (如 CNY/PHP/USD/AED)',
    `exchange_rate`   DECIMAL(18, 8) NOT NULL DEFAULT '1.00000000' COMMENT '1 单位 currency = ? 基准币',
    `base_currency`   VARCHAR(16)  NOT NULL DEFAULT 'CNY' COMMENT '基准币种',
    `remark`          VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
    `delete_flag`     BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`       VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`       VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_month_currency_del` (`settlement_month`, `currency`, `delete_flag`),
    KEY               `idx_month` (`settlement_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月度汇率表 (多币种结算)';

-- 种子：2026-08 常见结算币种对 CNY 汇率 (供演示, 财务可按月维护)
INSERT INTO `salary_exchange_rate` (`settlement_month`, `currency`, `exchange_rate`, `base_currency`, `remark`, `create_by`)
VALUES
    ('202608', 'CNY', 1.00000000, 'CNY', '基准币', 'system'),
    ('202608', 'PHP', 0.12500000, 'CNY', '菲律宾比索 (1 PHP ≈ 0.125 CNY)', 'system'),
    ('202608', 'USD', 7.15000000, 'CNY', '美元', 'system'),
    ('202608', 'AED', 1.95000000, 'CNY', '阿联酋迪拉姆', 'system'),
    ('202608', 'SGD', 5.30000000, 'CNY', '新加坡元', 'system'),
    ('202608', 'JPY', 0.04800000, 'CNY', '日元 (1 JPY ≈ 0.048 CNY)', 'system'),
    ('202609', 'CNY', 1.00000000, 'CNY', '基准币', 'system'),
    ('202609', 'PHP', 0.12600000, 'CNY', '菲律宾比索', 'system'),
    ('202609', 'USD', 7.12000000, 'CNY', '美元', 'system');

-- ----------------------------------------------------------
-- 补丁 8：薪资计算模式字典 (档案明细"计算模式"下拉/展示)
-- ----------------------------------------------------------
INSERT INTO `sys_dict_type` (`dict_type_code`, `dict_type_name`, `dict_category`, `remark`, `create_by`)
VALUES ('salary_calc_mode', '薪资计算模式', 'salary', '薪资档案明细计算模式', 'system');

INSERT INTO `sys_dict_item` (`dict_type_code`, `dict_item_value`, `dict_item_label`, `dict_item_sort`, `create_by`)
VALUES
    ('salary_calc_mode', '1', '按月固定', 10, 'system'),
    ('salary_calc_mode', '2', '按出勤天数', 20, 'system'),
    ('salary_calc_mode', '3', '按现场出勤', 30, 'system'),
    ('salary_calc_mode', '4', '按居家/远程出勤', 40, 'system');
