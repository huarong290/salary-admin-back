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
    `dict_category` VARCHAR(50) NOT NULL DEFAULT 'common' COMMENT '所属业务模块：如 system(系统), hr(人事), salary(薪资), finance(财务)',
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
    `dict_item_label`  VARCHAR(100) NOT NULL COMMENT '字典项名称',
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
    `category_dict_value` VARCHAR(64)  NOT NULL COMMENT '业务分类字典值 (如: allowance, bonus)',
    `env_var_name`        VARCHAR(64) NOT NULL COMMENT '引擎上下文变量名',
    `default_rule_script` TEXT COMMENT '默认表达式脚本模板',
    `calc_priority`       INT         NOT NULL DEFAULT 0 COMMENT '计算优先级 (数值越小越靠前)',
    `decimal_places`      TINYINT      NOT NULL DEFAULT 2 COMMENT '保留小数位数',
    `rounding_mode`       VARCHAR(20)  NOT NULL DEFAULT 'HALF_UP' COMMENT '舍入规则: HALF_UP(四舍五入), DOWN(截断), UP(向上进位)',
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
    `remark`              VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '财务备注',
    `delete_flag`          BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`            VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `create_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`            VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `update_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_code_del` (`employee_code`, `delete_flag`),
    KEY `idx_dept_status` (`department`, `employment_status`),
    KEY `idx_entry_date` (`entry_date`)
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
    KEY                     `uk_emp_latest_del` (`employee_id`, `latest_flag`, `audit_status`),
    KEY                     `idx_emp_time_slice` (`employee_id`, `effective_date`, `expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工薪资标准配置表(含版本历史)';
-- ==========================================================
-- 12. 薪资档案固定项明细表 (档案从表) (🚀 表达式 + 字典规范版)
-- ==========================================================
CREATE TABLE `salary_archive_item`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `archive_id`          BIGINT UNSIGNED NOT NULL COMMENT '档案ID',
    `item_type`           TINYINT(1) NOT NULL COMMENT '项目类型: 1-收入项, 2-扣款项',
    `item_config_id`      BIGINT UNSIGNED NOT NULL COMMENT '关联salary_item_config.id',
    `type_name`           VARCHAR(64)    NOT NULL COMMENT '项目名称快照',
    `category_dict_value` VARCHAR(64)    NOT NULL COMMENT '分类字典值快照',
    `rule_script`         TEXT COMMENT '表达式脚本',
    `amount`              DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '固定金额',
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
    `rule_code`   VARCHAR(64) NOT NULL COMMENT '规则编码',
    `rule_name`   VARCHAR(64) NOT NULL COMMENT '规则名称',
    `rule_type`   TINYINT(1) NOT NULL DEFAULT 1 COMMENT '规则类型 (1:公式,2:函数)',
    `rule_script` TEXT        NOT NULL COMMENT '表达式脚本',
    `return_type` VARCHAR(32) NOT NULL DEFAULT 'Decimal' COMMENT '返回值类型',
    `sort_value`  INT         NOT NULL DEFAULT 0 COMMENT '默认显示排序(仅用于字典列表展示)',
    `status`      TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 (1:启用,0:停用)',
    `depends_on`  VARCHAR(255)         DEFAULT NULL COMMENT '依赖变量',
    `param_json`  JSON                 DEFAULT NULL COMMENT '参数配置',
    `stage`       INT         NOT NULL DEFAULT 1 COMMENT '所属阶段',
    `remark`              VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '财务备注',
    `delete_flag` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
    `create_by`   VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`   VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
    `default_flag`    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否默认流程',
    `status`        TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态(1启用 0停用)',
    `remark`              VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '财务备注',
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
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `pipeline_code` VARCHAR(64) NOT NULL COMMENT '所属管道编码',
    `pipeline_version` INT NOT NULL DEFAULT 1 COMMENT '管道版本',
    `rule_code`     VARCHAR(64) NOT NULL COMMENT '规则编码',
    `rule_name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '规则名称快照',
    `rule_type` TINYINT(1) DEFAULT NULL COMMENT '规则类型快照',
    `condition_script` TEXT COMMENT '执行条件表达式',
    `stage`         TINYINT NOT NULL COMMENT '阶段(1基础 2补贴 3扣款 4税 5汇总)',
    `sort_order`    INT     NOT NULL COMMENT '执行顺序',
    `block_flag`      TINYINT(1) NOT NULL DEFAULT 1 COMMENT '失败是否阻断',
    `skip_if_null`  TINYINT(1) NOT NULL DEFAULT 0 COMMENT '结果为空是否跳过',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `delete_flag`   BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
    `create_by`     VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`     VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_pipeline_step` (`pipeline_code`, `pipeline_version`, `rule_code`, `delete_flag`),
    KEY `idx_pipeline_exec` (`pipeline_code`, `pipeline_version`, `status`, `stage`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资计算管道步骤-明细表';
-- ==========================================================
-- 16. 薪资计算上下文快照表
-- ==========================================================
CREATE TABLE `salary_calc_context`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `employee_id`   BIGINT UNSIGNED NOT NULL COMMENT '员工ID',
    `period_id`     BIGINT UNSIGNED NOT NULL COMMENT '薪资周期ID',
    `archive_id`    BIGINT UNSIGNED NOT NULL COMMENT '来源档案ID',
    `pipeline_version` INT NOT NULL DEFAULT 1 COMMENT '使用的流程管道版本号',
    `env_json`  JSON        NOT NULL COMMENT '上下文变量JSON（env）',
    `pipeline_code` VARCHAR(64) NOT NULL COMMENT '使用的流程编码',
    `version`       INT         NOT NULL DEFAULT 1 COMMENT '版本号',
    `remark`              VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '财务备注',
    `delete_flag`   BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`     VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`     VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
    `month_days`           DECIMAL(6, 2) NOT NULL DEFAULT '0.00' COMMENT '月天数',
    `attendance_days`      DECIMAL(6, 2) NOT NULL DEFAULT '0.00' COMMENT '出勤天数',
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
    `calc_version`      INT            NOT NULL DEFAULT 1 COMMENT '计算版本号(用于重算/历史追溯)',
    `calc_status`       TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT ' 计算状态:0-未计算 1-成功 2-失败',
    `payment_status`    TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '发放状态：0-未支付 1-已支付 2-支付失败',
    `lock_flag`         TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否锁定(1:锁定, 0:未锁定, 发放后锁定不可重算)',
    `detail_json`       JSON                    DEFAULT NULL COMMENT '汇总快照(用于展示工资单):{"income": [...],"deduction": [...],"tax": [...]}',
    `remark`              VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '财务备注',
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
    `settlement_month` CHAR(6)         NOT NULL COMMENT '考核/结算月份 (格式: YYYYMM，方便按月快速检索)',
    `kpi_grade`        VARCHAR(16)     NOT NULL DEFAULT '' COMMENT '最终绩效评级 (例如: S, A, B, C, D 等)',
    `kpi_score`        DECIMAL(6, 2)   NOT NULL DEFAULT 0.00 COMMENT '最终考核打分 (例如: 95.50，用于精细化计算)',
    `kpi_coefficient`  DECIMAL(6, 4)   NOT NULL DEFAULT 1.0000 COMMENT '绩效发放系数 (核心参数：例如 1.2000，算薪引擎直接乘以绩效基数)',
    `evaluate_by`      VARCHAR(64)     NOT NULL DEFAULT '' COMMENT '考核人 (通常记录直属主管或HR的账号/工号)',
    `evaluate_remark`  VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '考核评语/说明 (用于申诉或审计备查)',
    `audit_status`     TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '审核流转状态 (0:打分中/草稿, 1:已确认/审核通过, 2:被驳回/申诉中。注：引擎只抓取=1的数据)',
    `effective_flag`   TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '版本生效标识 (1:当前生效版本, 0:历史作废版本。用于处理重新打分时的历史数据保留)',
    `delete_flag`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标识 (0:未删除, >0:已删除)',
    `create_by`        VARCHAR(64)     NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        VARCHAR(64)     NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_emp_period_del` (`employee_id`, `period_id`, `delete_flag`),
    KEY `idx_month_status` (`settlement_month`, `audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工月度绩效考核记录表';

