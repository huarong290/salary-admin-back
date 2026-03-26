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
    `salt`            VARCHAR(50)  NOT NULL COMMENT '密码盐值',
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
    KEY `idx_parent_id` (`menu_parent_id`),
    KEY `idx_permission` (`menu_permission`)
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
    -- ⚡ 调整/移除外键 fk_user_role_user / fk_user_role_role
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
    -- ⚡ 调整/移除外键 fk_role_menu_role / fk_role_menu_menu
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与菜单关联表';

-- ==========================================================
-- 6. 系统字典类型表 sys_dict_type
-- ==========================================================
CREATE TABLE `sys_dict_type`
(
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_type_code` VARCHAR(50)  NOT NULL COMMENT '字典类型编码',
    `dict_type_name` VARCHAR(100) NOT NULL COMMENT '字典类型名称',
    `dict_category`  VARCHAR(50)  NOT NULL COMMENT '类别：income/deduction/other',
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
-- 7. 系统字典明细表 sys_dict_item
-- ==========================================================
CREATE TABLE `sys_dict_item`
(
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_type_code` VARCHAR(50)  NOT NULL COMMENT '字典类型编码',
    `dict_item_name` VARCHAR(100) NOT NULL COMMENT '字典项名称',
    `dict_item_value` VARCHAR(100) NOT NULL COMMENT '字典项值',
    `dict_item_sort` INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`         TINYINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '状态 (1:启用, 0:禁用)',
    `delete_flag`    BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`      VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_item_code_del` (`dict_type_code`, `dict_item_value`, `delete_flag`)
    -- ⚡ 调整/移除外键 fk_dict_item_type
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典明细表';

-- ==========================================================
-- 模块二：薪资核心业务模块 (Salary Base)
-- ==========================================================

-- ==========================================================
-- 8. 员工基本信息表 salary_employee
-- ==========================================================
CREATE TABLE `salary_employee`
(
    `id`                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '员工ID',
    `employee_code`        VARCHAR(32) NOT NULL COMMENT '员工编号',
    `employee_name`        VARCHAR(64) NOT NULL COMMENT '姓名',
    `company_name`         VARCHAR(128) DEFAULT NULL COMMENT '所属公司',
    `department`           VARCHAR(128) DEFAULT NULL COMMENT '部门',
    `employment_status`    TINYINT(1) NOT NULL DEFAULT 1 COMMENT '在职状态: 1-在职, 0-离职',
    `transfer_flag`        TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否转岗: 1-是, 0-否',
    `accommodation_status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '住宿情况: 0-不住宿, 1-公司宿舍, 2-外宿补贴',
    `entry_date`           DATE DEFAULT NULL COMMENT '入职日期',
    `delete_flag`          BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`            VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`            VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_code_del` (`employee_code`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工基本信息表';


-- ==========================================================
-- 9. 薪资周期信息表 salary_period
-- ==========================================================
CREATE TABLE `salary_period`
(
    `id`                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '周期ID',
    `employee_id`          BIGINT NOT NULL COMMENT '员工ID',
    `work_month`           CHAR(6) NOT NULL COMMENT '在岗月份 (YYYYMM)',
    `settlement_month`     CHAR(6) NOT NULL COMMENT '结算月份 (YYYYMM)',
    `start_date`           DATE DEFAULT NULL COMMENT '开始日期',
    `end_date`             DATE DEFAULT NULL COMMENT '结束日期',
    `month_days`           DECIMAL(6, 2) NOT NULL DEFAULT '0.00' COMMENT '月天数',
    `attendance_days`      DECIMAL(6, 2) NOT NULL DEFAULT '0.00' COMMENT '出勤天数',
    `full_attendance_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否满勤 (1:是, 0:否)',
    `delete_flag`          BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`            VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`            VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_emp_month_del` (`employee_id`, `settlement_month`, `delete_flag`),
    KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资周期信息表';


-- ==========================================================
-- 10. 薪资汇总与结算表 salary_summary
-- ==========================================================
CREATE TABLE `salary_summary`
(
    `id`                     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '汇总ID',
    `employee_id`            BIGINT NOT NULL COMMENT '员工ID',
    `employee_code`          VARCHAR(32) NOT NULL COMMENT '员工编号快照',
    `employee_name`          VARCHAR(100) NOT NULL COMMENT '员工姓名快照',
    `period_id`              BIGINT NOT NULL COMMENT '薪资周期ID',
    `settlement_month`       CHAR(6) NOT NULL COMMENT '结算月份 (YYYYMM)',
    `period_start_date`      DATE DEFAULT NULL COMMENT '周期开始快照',
    `period_end_date`        DATE DEFAULT NULL COMMENT '周期结束快照',
    `currency`               VARCHAR(16) NOT NULL DEFAULT 'CNY' COMMENT '结算币种',
    `exchange_rate`          DECIMAL(18, 8) NOT NULL DEFAULT '1.00000000' COMMENT '汇率',
    `salary_subtotal`        DECIMAL(18, 2) NOT NULL DEFAULT '0.00' COMMENT '应发小计',
    `salary_deduction_total` DECIMAL(18, 2) NOT NULL DEFAULT '0.00' COMMENT '扣款小计',
    `salary_total`           DECIMAL(18, 2) NOT NULL DEFAULT '0.00' COMMENT '最终结算薪资',
    `salary_converted`       DECIMAL(18, 2) NOT NULL DEFAULT '0.00' COMMENT '实发金额(目标币)',
    `salary_rmb`             DECIMAL(18, 2) NOT NULL DEFAULT '0.00' COMMENT '折合人民币',
    `salary_usdt`            DECIMAL(18, 2) NOT NULL DEFAULT '0.00' COMMENT '折合USDT',
    `target_account`         VARCHAR(255) DEFAULT NULL COMMENT '发放账号/钱包地址快照',
    `payment_status`         TINYINT(1) NOT NULL DEFAULT '0' COMMENT '支付状态 (0未支付,1已支付,2失败,3锁定)',
    `pay_time`               DATETIME DEFAULT NULL COMMENT '实际发放时间',
    `remark`                 TEXT DEFAULT NULL COMMENT '备注',
    `delete_flag`            BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`              VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`              VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_emp_period_del` (`employee_id`, `period_id`, `delete_flag`),
    KEY `idx_summary_month_emp` (`settlement_month`, `employee_id`),
    KEY `idx_payment_status_month` (`payment_status`, `settlement_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资汇总与结算表';


-- ==========================================================
-- 11. 员工收入明细表 salary_income_detail
-- ==========================================================
CREATE TABLE `salary_income_detail`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `period_id`           BIGINT NOT NULL COMMENT '薪资周期ID',
    `employee_id`         BIGINT NOT NULL COMMENT '员工ID',
    `income_type_id`      BIGINT NOT NULL COMMENT '收入类型ID',
    `income_type_name`    VARCHAR(64) NOT NULL COMMENT '收入项目名称快照',
    `category_dict_value` VARCHAR(64) NOT NULL COMMENT '分类字典值快照',
    `currency`            VARCHAR(16) NOT NULL DEFAULT 'CNY' COMMENT '录入原币种',
    `original_amount`     DECIMAL(18, 2) NOT NULL DEFAULT '0.00' COMMENT '录入原币金额',
    `exchange_rate`       DECIMAL(18, 8) NOT NULL DEFAULT '1.00000000' COMMENT '汇率',
    `amount`              DECIMAL(18, 2) NOT NULL DEFAULT '0.00' COMMENT '折合系统本位币金额',
    `settlement_currency` VARCHAR(16) NOT NULL DEFAULT 'CNY' COMMENT '结算币种',
    `remark`              VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
    `delete_flag`         BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_period_emp` (`period_id`, `employee_id`),
    KEY `idx_income_type_emp` (`income_type_id`, `employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工收入明细表';

-- ==========================================================
-- 12. 收入类型字典表 salary_income_type
-- ==========================================================
CREATE TABLE `salary_income_type`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收入类型ID',
    `type_code`           VARCHAR(64) NOT NULL COMMENT '收入类型编码',
    `env_var_name`        VARCHAR(64) NOT NULL COMMENT '引擎上下文变量名',
    `default_rule_script` TEXT COMMENT '默认表达式脚本模板',
    `type_name`           VARCHAR(64) NOT NULL COMMENT '收入类型名称',
    `pinyin_code`         VARCHAR(64) NOT NULL DEFAULT '' COMMENT '拼音缩写',
    `category_dict_value` VARCHAR(64) NOT NULL COMMENT '收入分类字典值快照',
    `taxable_flag`        TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否纳入个税计税基数 (1:是, 0:否)',
    `description`         VARCHAR(255) NOT NULL DEFAULT '' COMMENT '收入项说明',
    `sort_value`          INT NOT NULL DEFAULT '0' COMMENT '排序值 (数值越小越靠前)',
    `delete_flag`         BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    `create_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_code_del` (`type_code`, `delete_flag`),
    KEY `idx_category_dict` (`category_dict_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收入类型业务元数据表';

-- ==========================================================
-- 13. 员工扣款明细表 salary_deduction_detail (字典规范版)
-- ==========================================================
CREATE TABLE `salary_deduction_detail`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `period_id`           BIGINT NOT NULL COMMENT '周期ID',
    `employee_id`         BIGINT NOT NULL COMMENT '员工ID',
    `deduction_type_id`   BIGINT NOT NULL COMMENT '扣款类型ID',
    `deduction_type_name` VARCHAR(64) NOT NULL COMMENT '扣款项目名称快照',
    `category_dict_value` VARCHAR(64) NOT NULL COMMENT '分类字典值快照',
    `currency`            VARCHAR(16) NOT NULL DEFAULT 'CNY' COMMENT '原币种',
    `original_amount`     DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '原币金额',
    `exchange_rate`       DECIMAL(18,8) NOT NULL DEFAULT '1.00000000' COMMENT '汇率',
    `amount`              DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '折合金额',
    `settlement_currency` VARCHAR(16) NOT NULL DEFAULT 'CNY' COMMENT '结算币种',
    `remark`              VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
    `delete_flag`         BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_period_emp` (`period_id`, `employee_id`),
    KEY `idx_deduction_type_emp` (`deduction_type_id`, `employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工扣款明细表';


-- ==========================================================
-- 14. 扣款类型字典表 salary_deduction_type (🚀 表达式 + 字典桥接版)
-- ==========================================================
CREATE TABLE `salary_deduction_type`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '扣款类型ID',
    `type_code`           VARCHAR(64) NOT NULL COMMENT '扣款类型编码',
    `env_var_name`        VARCHAR(64) NOT NULL COMMENT '引擎上下文变量名',
    `default_rule_script` TEXT COMMENT '默认表达式脚本模板',
    `type_name`           VARCHAR(64) NOT NULL COMMENT '扣款类型名称',
    `pinyin_code`         VARCHAR(64) NOT NULL DEFAULT '' COMMENT '拼音缩写',
    `category_dict_value` VARCHAR(64) NOT NULL COMMENT '扣款分类字典值快照',
    `description`         VARCHAR(255) NOT NULL DEFAULT '' COMMENT '扣款项说明',
    `sort_value`          INT NOT NULL DEFAULT '0' COMMENT '排序值',
    `fixed_flag`          TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否固定扣款 (1:是,0:否)',
    `tax_deductible_flag` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否税前扣除 (1:是,0:否)',
    `delete_flag`         BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_code_del` (`type_code`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扣款类型业务元数据表';


-- ==========================================================
-- 15. 员工薪资标准配置表 (档案主表)
-- ==========================================================
CREATE TABLE `salary_archive`
(
    `id`                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `employee_id`           BIGINT NOT NULL COMMENT '员工ID',
    `version`               INT NOT NULL DEFAULT 1 COMMENT '版本号',
    `latest_flag`           TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否最新版本 (1:是,0:否)',
    `effective_date`        DATE NOT NULL COMMENT '生效日期',
    `expiry_date`           DATE NOT NULL DEFAULT '9999-12-31' COMMENT '失效日期',
    `audit_status`          TINYINT(1) NOT NULL DEFAULT 0 COMMENT '审核状态 (0:草稿,1:已生效,2:驳回)',
    `base_salary`           DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '基本工资',
    `full_attendance_bonus` DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '全勤奖标准',
    `probation_base_salary` DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '试用期底薪',
    `currency`              VARCHAR(16) NOT NULL DEFAULT 'CNY' COMMENT '结算币种',
    `change_reason`         VARCHAR(255) NOT NULL DEFAULT '' COMMENT '调薪原因',
    `tax_rule_code`         VARCHAR(64) NOT NULL DEFAULT 'TAX_RESIDENT_CN' COMMENT '个税规则Code',
    `remark`                VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
    `delete_flag`           BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`             VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`             VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_emp_version_del` (`employee_id`, `version`, `delete_flag`),
    KEY `uk_emp_latest_del` (`employee_id`, `latest_flag`, `audit_status`),
    KEY `idx_emp_time_slice` (`employee_id`, `effective_date`, `expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工薪资标准配置表(含版本历史)';
-- ==========================================================
-- 16. 薪资档案固定项明细表 (档案从表) (🚀 表达式 + 字典规范版)
-- ==========================================================
CREATE TABLE `salary_archive_item`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `archive_id`          BIGINT NOT NULL COMMENT '档案ID',
    `item_type`           TINYINT(1) NOT NULL COMMENT '项目类型: 1-收入项, 2-扣款项',
    `type_id`             BIGINT NOT NULL COMMENT '收入/扣款类型ID',
    `type_name`           VARCHAR(64) NOT NULL COMMENT '收入/扣款名称快照',
    `category_dict_value` VARCHAR(64) NOT NULL COMMENT '分类字典值快照',
    `rule_script`         TEXT COMMENT '表达式脚本',
    `amount`              DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '固定金额',
    `delete_flag`         BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_archive_item_del` (`archive_id`, `item_type`, `type_id`, `delete_flag`),
    KEY `idx_archive_item_type` (`archive_id`, `item_type`, `type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资档案固定项明细表';

-- ==========================================================
-- 17. 薪资计算结果快照表
-- ==========================================================
CREATE TABLE `salary_payment_record`
(
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `summary_id`          BIGINT NOT NULL COMMENT '汇总ID',
    `employee_id`         BIGINT NOT NULL COMMENT '员工ID',
    `archive_id`          BIGINT NOT NULL COMMENT '薪资档案版本ID',
    `settlement_month`    CHAR(6) NOT NULL COMMENT '结算月份 (YYYYMM)',
    `base_salary`         DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '基本工资快照',
    `income_total`        DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '收入合计',
    `deduction_total`     DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '扣款合计',
    `final_salary`        DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '最终总计',
    `is_manual`           TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否手动录入 (1:是,0:否)',
    `settlement_currency` VARCHAR(16) NOT NULL DEFAULT 'CNY' COMMENT '结算币种',
    `exchange_rate`       DECIMAL(18,8) NOT NULL DEFAULT '1.00000000' COMMENT '汇率',
    `base_final_salary`   DECIMAL(18, 8) NOT NULL DEFAULT '0.00' COMMENT '折合本位币金额',
    `payment_method`      VARCHAR(64) NOT NULL DEFAULT '' COMMENT '支付方式',
    `detail_json`         JSON DEFAULT NULL COMMENT '计算详情快照',
    `remark`              TEXT DEFAULT NULL COMMENT '备注',
    `delete_flag`         BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`           VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_summary_emp_del` (`summary_id`, `employee_id`, `delete_flag`),
    KEY `idx_summary_emp_month` (`summary_id`, `employee_id`, `settlement_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资结算明细记录表';


-- ==========================================================
-- 18. 薪资系统全局配置表
-- ==========================================================
CREATE TABLE `salary_config`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `config_key`   VARCHAR(64) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(255) NOT NULL COMMENT '配置值',
    `config_name`  VARCHAR(128) NOT NULL COMMENT '配置名称',
    `active_flag`  TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活 (1:是,0:否)',
    `remark`       VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `delete_flag`  BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`    VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`    VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_config_key_del` (`config_key`, `delete_flag`),
    KEY `idx_active_flag` (`active_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资系统全局配置表';


-- ==========================================================
-- 19. 薪资全局计算规则库表 (🌟 全新引擎核心表)
-- ==========================================================
CREATE TABLE `salary_calc_rule`
(
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `rule_code`      VARCHAR(64) NOT NULL COMMENT '规则编码',
    `rule_name`      VARCHAR(64) NOT NULL COMMENT '规则名称',
    `rule_type`      TINYINT(1) NOT NULL DEFAULT 1 COMMENT '规则类型 (1:公式,2:函数)',
    `rule_script`    TEXT NOT NULL COMMENT '表达式脚本',
    `return_type`    VARCHAR(32) NOT NULL DEFAULT 'Decimal' COMMENT '返回值类型',
    `sort_value`     INT NOT NULL DEFAULT 0 COMMENT '执行优先级',
    `status`         TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 (1:启用,0:停用)',
    `remark`         VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `delete_flag`    BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `create_by`      VARCHAR(64) NOT NULL DEFAULT 'admin',
    `create_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`      VARCHAR(64) NOT NULL DEFAULT 'admin',
    `update_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_rule_code_del` (`rule_code`, `delete_flag`),
    KEY `idx_rule_type_status` (`rule_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资计算规则库表';




