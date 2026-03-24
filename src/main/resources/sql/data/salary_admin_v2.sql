SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================================
-- 模块一：系统基础权限与字典 (System Base)
-- ==========================================================
-- ==========================================================
-- 1. 用户表 sys_user
-- ==========================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`        VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '用户名',
    `password`        VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '加密密码 (扩容至255，适应未来更复杂的加密算法)',
    `nickname`        VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '用户昵称',
    `email`           VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '邮箱',
    `phone`           VARCHAR(20)     NOT NULL DEFAULT '' COMMENT '手机号',
    `sex`             TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '用户性别 (0:未知, 1:男, 2:女)',
    `avatar`          VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '头像地址URL',
    `status`          TINYINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '状态 (0:禁用, 1:正常)',
    `last_login_time` DATETIME        NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '最后登录时间',
    `delete_flag`     BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`       VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username_del` (`username`, `delete_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
-- ==========================================================
-- 2. 角色表 sys_role
-- ==========================================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   VARCHAR(50)     NOT NULL COMMENT '角色名称',
    `role_code`   VARCHAR(50)     NOT NULL COMMENT '角色编码 (如: ADMIN, MGR)',
    `role_sort`   INT             NOT NULL DEFAULT '0' COMMENT '显示顺序',
    `role_status` TINYINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '状态',
    `role_desc`   VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '角色描述',
    `remark`      VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '备注',
    `delete_flag` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`   VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rolecode_del` (`role_code`, `delete_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ==========================================================
-- 3. 菜单表 sys_menu
-- ==========================================================
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name`       VARCHAR(50)     NOT NULL COMMENT '菜单名称 (如：系统管理、用户新增)',
    `menu_code`       VARCHAR(100)    NOT NULL COMMENT '菜单编码 (唯一标识)',
    `menu_path`       VARCHAR(200)    NOT NULL DEFAULT '' COMMENT '前端路由地址(如:/system/user，按钮无需路由)',
    `menu_component`  VARCHAR(200)    NOT NULL DEFAULT '' COMMENT '前端组件路径 (如: system/user/index)',
    `menu_redirect`   VARCHAR(200)    NOT NULL DEFAULT '' COMMENT '重定向地址',
    `menu_icon`       VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '菜单图标，用于前端显示',
    `menu_permission` VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '后端权限标识 (如 sys:user:add)',
    `menu_type`       TINYINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '菜单类型 (1:目录, 2:菜单, 3:按钮)',
    `menu_parent_id`  BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '父级ID',
    `menu_sort`       INT             NOT NULL DEFAULT '0' COMMENT '排序',
    `menu_visible`    TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '菜单是否可见：0可见 1隐藏，前端渲染控制',
    `menu_status`     TINYINT UNSIGNED NOT NULL DEFAULT '1' COMMENT '菜单业务状态 (1:正常 0:停用)',
    `delete_flag`     BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`       VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_menucode_del` (`menu_code`, `delete_flag`) USING BTREE,
    KEY               `idx_parent_id` (`menu_parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限菜单表';

-- ==========================================================
-- 4. 用户-角色关联表 sys_user_role
-- ==========================================================
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `role_id`     BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    `delete_flag` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`   VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role_del` (`user_id`, `role_id`, `delete_flag`),
    KEY           `idx_user_id` (`user_id`),
    KEY           `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与角色关联表';

-- ==========================================================
-- 5. 角色-菜单关联表 sys_role_menu
-- ==========================================================
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id`     BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    `menu_id`     BIGINT UNSIGNED NOT NULL COMMENT '菜单ID',
    `delete_flag` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`   VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)     NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu_del` (`role_id`, `menu_id`, `delete_flag`),
    KEY           `idx_role_id` (`role_id`),
    KEY           `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与菜单关联表';

-- ==========================================================
-- 6. 系统全局字典表
-- ==========================================================
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_type_code`  VARCHAR(100) NOT NULL DEFAULT '' COMMENT '所属字典类型code',
    `dict_item_label` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '字典项标签（如 男、女）',
    `dict_item_value` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '字典项值（如 1、0）',
    `sort`            INT          NOT NULL DEFAULT '0' COMMENT '排序值，越小越靠前',
    `status`          TINYINT(1)   NOT NULL DEFAULT '1' COMMENT '是否启用，0表示启用',
    `remark`          VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注说明',
    `delete_flag`     BIGINT(1)    NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`       VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type_value` (`dict_type_code`, `dict_item_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典项表';

-- ==========================================================
-- 7. 系统全局字典项表
-- ==========================================================
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_type_code` VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '字典类型编码（如 gender、status）',
    `dict_type_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '字典类型名称（如 性别、状态）',
    `status`         TINYINT(1)   NOT NULL DEFAULT '0' COMMENT '是否启用,0 表示启用',
    `remark`         VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注说明',
    `delete_flag`    BIGINT(1)    NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`      VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code` (`dict_type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统字典类型表';


-- ==========================================================
-- 模块二：薪资基础与周期字典 (Salary Base & Dictionaries)
-- ==========================================================
-- ==========================================================
-- 8. 员工基本信息表 salary_employee
-- ==========================================================
DROP TABLE IF EXISTS `salary_employee`;
CREATE TABLE `salary_employee`
(
    `id`                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '员工ID',
    `employee_code`        VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '员工编号',
    `employee_name`        VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '姓名',
    `company_name`         VARCHAR(128) DEFAULT NULL COMMENT '所属公司',
    `department`           VARCHAR(128) DEFAULT NULL COMMENT '部门',
    `employment_status`    TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '在职状态: 0-离职, 1-在职',
    `transfer_flag`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否转岗: 0-否, 1-是',
    `accommodation_status` TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '住宿情况: 0-不住宿, 1-公司宿舍, 2-外宿补贴',
    `entry_date`           DATE         DEFAULT NULL COMMENT '入职日期',
    `delete_flag`          BIGINT(1)    NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`            VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_employee_code` (`employee_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工基本信息表';

-- ==========================================================
-- 9. 收入类型字典表 salary_income_type
-- ==========================================================
DROP TABLE IF EXISTS `salary_income_type`;
CREATE TABLE `salary_income_type`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '收入类型ID',
    `type_code`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '收入类型缩写简称',
    `type_name`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '收入类型名称',
    `pinyin_code`   VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '拼音缩写',
    `category_id`           BIGINT       NOT NULL COMMENT '关联分类ID',
--     `category_name` VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '收入分类',
    `taxable_flag`  TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否纳入个税计税基数: 0-否, 1-是',
    `social_base_flag`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否计入社保基数',
    `bonus_flag`              TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否奖金类',
    `attendance_related_flag` TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否考勤相关',
    `description`   VARCHAR(255) NOT NULL DEFAULT '' COMMENT '收入项说明',
    `sort_value`    INT          NOT NULL DEFAULT '0' COMMENT '排序值 (数值越小越靠前)',
    `delete_flag`   BIGINT(1)    NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`     VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收入类型字典表';

-- ==========================================================
-- 10. 扣款类型字典表 salary_deduction_type
-- ==========================================================
DROP TABLE IF EXISTS `salary_deduction_type`;
CREATE TABLE `salary_deduction_type`
(
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '扣款类型ID',
    `type_code`           VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '扣款类型缩写简称',
    `type_name`           VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '扣款类型名称',
    `pinyin_code`         VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '拼音缩写',
    `category_id`           BIGINT       NOT NULL COMMENT '分类ID',
--     `category_name`       VARCHAR(64)  DEFAULT NULL COMMENT '扣款分类',
    `description`         VARCHAR(255) DEFAULT NULL COMMENT '扣款项说明',
    `sort_value`          INT          NOT NULL DEFAULT '0' COMMENT '排序值 (数值越小越靠前)',
    `tax_deductible_flag`  TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否纳入个税计税基数: 0-否, 1-是',
    `social_base_flag`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否计入社保基数',
    `bonus_flag`              TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否奖金类',
    `attendance_related_flag` TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否考勤相关',
    `delete_flag`         BIGINT(1)    NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`           VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`           VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扣款类型字典表';

-- ==========================================================
-- 11. 薪资系统全局配置表
-- ==========================================================
DROP TABLE IF EXISTS `salary_config`;
CREATE TABLE `salary_config`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `config_key`   VARCHAR(64)  NOT NULL COMMENT '配置键:如 SETTLEMENT_CURRENCY',
    `config_value` VARCHAR(255) NOT NULL COMMENT '配置值:如 USDT',
    `config_name`  VARCHAR(128) NOT NULL COMMENT '配置名称:如 默认结算币种',
    `active_flag`  TINYINT(1)   DEFAULT 1 COMMENT '是否激活: 1-是, 0-否',
    `remark`       VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `delete_flag`  BIGINT(1)    NOT NULL DEFAULT '0',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`    VARCHAR(64)  NOT NULL DEFAULT 'admin',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`    VARCHAR(64)  NOT NULL DEFAULT 'admin',
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资系统全局配置表';


-- ==========================================================
-- 模块三：薪资档案状态机 (Salary Archive - 拉链表)
-- ==========================================================
-- ==========================================================
-- 12. 员工薪资标准配置表 (档案主表)
-- ==========================================================
DROP TABLE IF EXISTS `salary_archive`;
CREATE TABLE `salary_archive`
(
    `id`                    BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `employee_id`           BIGINT         NOT NULL COMMENT '员工ID',
    `salary_group_id`       BIGINT         NOT NULL DEFAULT 0 COMMENT '🌟绑定的薪资套账ID (为V2.0规则引擎铺垫)',
    `version`               INT            NOT NULL DEFAULT 1 COMMENT '版本号 (每次调薪递增)',
    `is_latest`             TINYINT(1)     NOT NULL DEFAULT 1 COMMENT '是否当前最新版本: 0-历史, 1-最新',
    `effective_date`        DATE           NOT NULL COMMENT '生效起始日期',
    `expiry_date`           DATE           DEFAULT '9999-12-31' COMMENT '失效日期',
    `audit_status`          TINYINT(4)     NOT NULL DEFAULT 0 COMMENT '审核状态: 0-草稿/待审, 1-已生效, 2-驳回',
    `base_salary`           DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '基本工资/转正底薪',
    `full_attendance_bonus` DECIMAL(18, 8) DEFAULT '0.00000000' COMMENT '全勤奖标准',
    `probation_base_salary` DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '试用期底薪(选填)',
    `currency`              VARCHAR(16)    NOT NULL DEFAULT 'CNY' COMMENT '默认结算币种',
    `change_reason`         VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '调薪原因 (如: 年度普调、晋升)',
    `tax_scheme`            TINYINT(4)     NOT NULL DEFAULT 1 COMMENT '计税方案: 0-不计税, 1-居民个人所得税, 2-劳务报酬税',
    `remark`                VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '档案备注',
    `delete_flag`           BIGINT(1)      NOT NULL DEFAULT '0',
    `create_by`             VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `create_time`           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`             VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `update_time`           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_emp_version` (`employee_id`, `version`),
    KEY `idx_emp_latest` (`employee_id`, `is_latest`, `audit_status`),
    KEY `idx_effective_date` (`effective_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工薪资标准配置表(含版本历史)';
-- ==========================================================
-- 13. 薪资档案固定项明细表 (档案从表)
-- ==========================================================
DROP TABLE IF EXISTS `salary_archive_item`;
CREATE TABLE `salary_archive_item`
(
    `id`            BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `archive_id`    BIGINT         NOT NULL DEFAULT 0 COMMENT '关联具体的某一个版本的档案ID',
    `item_type`     TINYINT(1)     NOT NULL DEFAULT 0 COMMENT '项目类型: 1-收入项, 2-扣款项',
    `type_id`       BIGINT         NOT NULL DEFAULT 0 COMMENT '对应的收入/扣款类型ID',
    `type_name`     VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '对应的收入/扣款类型ID',
    `category_name` VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '分类名称快照',
    `calc_type`     TINYINT(1)     NOT NULL DEFAULT 1 COMMENT '计算方式: 1-固定金额, 2-按基数比例 3-按出勤折算额度',
    `base_amount`   DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '计算基数 (为空则默认取主表base_salary)',
    `amount`        DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '固定金额 (若为比例计算，此字段可作为计算结果缓存)',
    `ratio`         DECIMAL(8, 4)  NOT NULL DEFAULT '0.0000' COMMENT '计算比例 (如 0.0800 代表 8%)',
    `delete_flag`   BIGINT(1)      NOT NULL DEFAULT '0',
    `create_by`     VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `create_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`     VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `update_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_archive_id` (`archive_id`),
    UNIQUE KEY `uk_archive_item` (`archive_id`, `item_type`, `type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资档案固定项明细表';


-- ==========================================================
-- 模块四：月度核算流转表 (Salary Processing)
-- ==========================================================
-- ==========================================================
-- 14. 薪资周期信息表 salary_period
-- ==========================================================
DROP TABLE IF EXISTS `salary_period`;
CREATE TABLE `salary_period`
(
    `id`                   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '周期ID',
    `employee_id`          BIGINT        NOT NULL DEFAULT '0' COMMENT '员工ID',
    `work_month`           VARCHAR(255)  NOT NULL DEFAULT '' COMMENT '在岗月份',
    `settlement_month`     VARCHAR(255)  NOT NULL DEFAULT '' COMMENT '结算月份（格式：YYYYMM）',
    `start_date`           DATE          DEFAULT NULL COMMENT '开始日期',
    `end_date`             DATE          DEFAULT NULL COMMENT '结束日期',
    `month_days`           DECIMAL(6, 3) NOT NULL DEFAULT '0' COMMENT '月天数',
    `attendance_days`      DECIMAL(6, 3) NOT NULL DEFAULT '0' COMMENT '出勤天数',
    `full_attendance_flag` TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '是否满勤 (1:是, 0:否)',
    `delete_flag`          BIGINT(1)     NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`            VARCHAR(64)   NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            VARCHAR(64)   NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `employee_id` (`employee_id`) USING BTREE,
    UNIQUE KEY `uk_emp_month` (`employee_id`, `settlement_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资周期信息表';

-- ==========================================================
-- 15. 薪资汇总与结算表 salary_summary
-- ==========================================================
DROP TABLE IF EXISTS `salary_summary`;
CREATE TABLE `salary_summary`
(
    `id`                     BIGINT         NOT NULL AUTO_INCREMENT COMMENT '汇总ID',
    `period_id`              BIGINT         NOT NULL DEFAULT 0 COMMENT '薪资周期ID',
    `currency`               VARCHAR(16)    NOT NULL DEFAULT 'CNY' COMMENT '结算币种(CNY/PHP/USDT)',
    `exchange_rate`          DECIMAL(18, 8) DEFAULT '1.00000000' COMMENT '汇率(1本币兑X目标币快照)',
    `salary_subtotal`        DECIMAL(18, 8) DEFAULT '0.00000000' COMMENT '应发小计(本币)',
    `salary_deduction_total` DECIMAL(18, 8) DEFAULT '0.00000000' COMMENT '扣款小计(本币)',
    `salary_total`           DECIMAL(18, 8) DEFAULT '0.00000000' COMMENT '最终结算薪资(本币)',
    `salary_converted`       DECIMAL(18, 8) DEFAULT '0.00000000' COMMENT '实发金额(目标币)',
    `salary_rmb`             DECIMAL(18, 8) DEFAULT '0.00000000' COMMENT '折合人民币(存档)',
    `salary_usdt`            DECIMAL(18, 8) DEFAULT '0.00000000' COMMENT '折合USDT(存档)',
    `target_account`         VARCHAR(255)   DEFAULT NULL COMMENT '发放账号/钱包地址(快照)',
    `payment_status`         TINYINT(1)     DEFAULT '0' COMMENT '支付状态(0未支付 1已支付 2失败 3锁定)',
    `pay_time`               DATETIME       DEFAULT NULL COMMENT '实际发放/确认时间',
    `remark`                 TEXT           DEFAULT NULL COMMENT '备注',
    `delete_flag`            BIGINT(1)      NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`              VARCHAR(64)    NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`            DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`              VARCHAR(64)    NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`            DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_period_delete` (`period_id`, `delete_flag`) USING BTREE,
    KEY `idx_currency` (`currency`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资汇总与结算表';
-- ==========================================================
-- 16. 员工收入明细表 salary_income_detail
-- ==========================================================
DROP TABLE IF EXISTS `salary_income_detail`;
CREATE TABLE `salary_income_detail`
(
    `id`                  BIGINT         NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `period_id`           BIGINT         NOT NULL COMMENT '薪资周期ID',
    `employee_id`         BIGINT         NOT NULL COMMENT '员工ID (冗余方便查询)',
    `income_type_id`      BIGINT         NOT NULL COMMENT '收入类型ID',
    `income_type_name`    VARCHAR(64)    NOT NULL COMMENT '收入项目名称快照 (如: 季度奖金)',
    `category_name`       VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '收入分类快照 (如: 奖金)',
    `currency`            VARCHAR(16)    NOT NULL DEFAULT 'CNY' COMMENT '录入原币种(如CNY, PHP, USDT)',
    `original_amount`     DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '录入原币金额',
    `exchange_rate`       DECIMAL(18, 8) NOT NULL DEFAULT '1.00000000' COMMENT '录入时汇率(原币兑本币)',
    `amount`              DECIMAL(18, 2) NOT NULL DEFAULT '0.00' COMMENT '折合系统本位币后的核算金额',
    `settlement_currency` VARCHAR(16)    NOT NULL DEFAULT 'CNY' COMMENT '结算币种(如CNY, PHP, USDT)',
    `remark`              VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '备注',
    `delete_flag`         TINYINT(1)     NOT NULL DEFAULT '0',
    `create_by`           VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `create_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`           VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `update_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_period_emp` (`period_id`, `employee_id`),
    KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工变动收入明细表(阅后即焚)';

-- ==========================================================
-- 17. 员工扣款明细表 salary_deduction_detail
-- ==========================================================
DROP TABLE IF EXISTS `salary_deduction_detail`;
CREATE TABLE `salary_deduction_detail`
(
    `id`                  BIGINT         NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `period_id`           BIGINT         NOT NULL COMMENT '周期ID',
    `employee_id`         BIGINT         NOT NULL COMMENT '员工ID',
    `deduction_type_id`   BIGINT         NOT NULL COMMENT '扣款类型ID',
    `deduction_type_name` VARCHAR(64)    NOT NULL COMMENT '扣款项目名称快照 (如: 迟到扣款)',
    `category_name`       VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '扣款分类快照 (如: 考勤扣项)',
    `currency`            VARCHAR(16)    NOT NULL DEFAULT 'CNY' COMMENT '原币种(如CNY, PHP, USDT)',
    `original_amount`     DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '原币金额',
    `exchange_rate`       DECIMAL(18, 8) NOT NULL DEFAULT '1.00000000' COMMENT '录入时汇率(原币兑本币)',
    `amount`              DECIMAL(18, 2) NOT NULL DEFAULT '0.00' COMMENT '金额',
    `settlement_currency` VARCHAR(16)    NOT NULL DEFAULT 'CNY' COMMENT '结算币种(如CNY, PHP, USDT)',
    `remark`              VARCHAR(255)   NOT NULL DEFAULT '' COMMENT '备注',
    `delete_flag`         BIGINT(1)      NOT NULL DEFAULT '0',
    `create_by`           VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `create_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by`           VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `update_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_period_emp` (`period_id`, `employee_id`),
    KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工变动扣款明细表(阅后即焚)';


-- ==========================================================
-- 模块五：结果快照与拆分支付表 (Payment & Snapshot)
-- ==========================================================
-- ==========================================================
-- 18：薪资计算结果快照表 (用于存储计算出的明细或直接录入的总额)
-- ==========================================================
DROP TABLE IF EXISTS `salary_payment_record`;
CREATE TABLE `salary_payment_record`
(
    `id`                  BIGINT         NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `summary_id`          BIGINT         NOT NULL COMMENT '关联汇总ID',
    `employee_id`         BIGINT         NOT NULL COMMENT '员工ID',
    `archive_id`          BIGINT         NOT NULL DEFAULT 0 COMMENT '关联薪资档案版本ID(系统计算必填)',

    -- 核心金额字段 (底层引擎结果)
    `base_salary`         DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '基本工资(系统计算快照)',
    `income_total`        DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '收入合计',
    `deduction_total`     DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '扣款合计',
    `final_salary`        DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '最终总计(无论是计算还是手动录入)',
    `is_manual`           TINYINT(1)     NOT NULL DEFAULT '0' COMMENT '是否手动录入总额(0系统计算 1手动录入)',

    -- 🌟 结算币种与拆分发放模块 (企业级多币种核心)
    `settlement_currency` VARCHAR(10)    NOT NULL DEFAULT 'CNY' COMMENT '结算基准币种(CNY/USD/PHP等)',
    `exchange_rate`       DECIMAL(18, 6) NOT NULL DEFAULT '1.000000' COMMENT '核算汇率(相对于系统本位币)',
    `base_final_salary`   DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '折合本位币实发金额(用于汇总报表)',
    `payment_method`      VARCHAR(32)    NOT NULL DEFAULT '' COMMENT '默认发放方式(银行卡/USDT地址/现金)',

    `split_cny_amount`    DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '拆分发放：法币(CNY/PHP)部分金额',
    `split_usdt_amount`   DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '拆分发放：加密货币(USDT)部分金额',
    `usdt_exchange_rate`  DECIMAL(18, 6) NOT NULL DEFAULT '0.000000' COMMENT '当期 USDT 兑法币的结算汇率',
    `cny_pay_status`      TINYINT(1)     NOT NULL DEFAULT '0' COMMENT '法币部分发放状态(0未发 1已发)',
    `usdt_pay_status`     TINYINT(1)     NOT NULL DEFAULT '0' COMMENT 'USDT部分发放状态(0未发 1已发)',

    -- 审计快照
    -- 审计增强
    `version`             INT            NOT NULL DEFAULT 1 COMMENT '计算版本',
    `valid_flag`            TINYINT(1)     NOT NULL DEFAULT 1 COMMENT '是否有效',
    `detail_json`         JSON           DEFAULT NULL COMMENT '计算详情快照(存储当时所有公式与环境变量的JSON)',
    `remark`              TEXT           DEFAULT NULL COMMENT '备注',
    `delete_flag`         BIGINT(1)      NOT NULL DEFAULT '0',
    `create_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`           VARCHAR(64)    NOT NULL DEFAULT 'admin',
    `update_time`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`           VARCHAR(64)    NOT NULL DEFAULT 'admin',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_summary_id` (`summary_id`),
    KEY `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资结算明细记录与快照表';


-- ==========================================================
-- 模块六：🚀 未来架构 V2.0 规则引擎储备表 (Rule Engine Readiness)
-- ==========================================================
-- ==========================================================
-- 19：薪资套账(规则组)表
-- ==========================================================
DROP TABLE IF EXISTS `salary_group`;
CREATE TABLE `salary_group` (
                                `id`          BIGINT       NOT NULL AUTO_INCREMENT,
                                `group_code`    VARCHAR(64) NOT NULL COMMENT '编码',
                                `group_name`  VARCHAR(128) NOT NULL COMMENT '套账名称 (如: 高管套账, 销售组套账, 菲律宾外籍套账)',
                                `currency`    VARCHAR(16)  NOT NULL DEFAULT 'CNY' COMMENT '该套账的默认发薪币种',
                                `tax_scheme`  TINYINT      NOT NULL DEFAULT 1 COMMENT '绑定的计税方案',
                                `delete_flag` BIGINT(1)    NOT NULL DEFAULT '0',
                                `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `create_by`   VARCHAR(64)  NOT NULL DEFAULT 'admin',
                                `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                `update_by`   VARCHAR(64)  NOT NULL DEFAULT 'admin',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【V2.0 预留】薪资套账(规则组)表';
-- ==========================================================
-- 20：薪资算薪因子(变量)表
-- ==========================================================
DROP TABLE IF EXISTS `salary_factor`;
CREATE TABLE `salary_factor` (
                                 `id`            BIGINT       NOT NULL AUTO_INCREMENT,
                                 `factor_code`   VARCHAR(64)  NOT NULL COMMENT '因子编码 (如: base_salary, attendance_days)',
                                 `factor_name`   VARCHAR(64)  NOT NULL COMMENT '因子名称 (如: 基本工资, 出勤天数)',
                                 `factor_type`   TINYINT(1)   NOT NULL COMMENT '因子来源: 1-系统内置, 2-档案提取, 3-当月导入',
                                 `scope_type`    VARCHAR(32)  NOT NULL COMMENT 'GLOBAL / ARCHIVE / PERIOD',
                                 `data_type`     VARCHAR(16)  NOT NULL DEFAULT 'NUMBER' COMMENT '数据类型: NUMBER, STRING, BOOLEAN',
                                 `default_value` VARCHAR(64)  DEFAULT '0' COMMENT '兜底默认值',
                                 `delete_flag`   BIGINT(1)    NOT NULL DEFAULT '0',
                                 `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `create_by`     VARCHAR(64)  NOT NULL DEFAULT 'admin',
                                 `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 `update_by`     VARCHAR(64)  NOT NULL DEFAULT 'admin',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_factor_code` (`factor_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【V2.0 预留】薪资算薪因子(变量)表';
-- ==========================================================
-- 20：薪资动态计算规则(公式)表
-- ==========================================================
DROP TABLE IF EXISTS `salary_rule`;
CREATE TABLE `salary_rule` (
                               `id`                   BIGINT       NOT NULL AUTO_INCREMENT,
                               `salary_group_id`      BIGINT       NOT NULL COMMENT '绑定的套账ID',
                               `item_type`            TINYINT(1)   NOT NULL COMMENT '1-收入项 2-扣款项',
                               `type_id`              BIGINT       NOT NULL COMMENT '关联字典的 收入/扣款项ID',
                               `rule_name`            VARCHAR(128) NOT NULL COMMENT '规则名称 (如: 研发部全勤奖规则)',
                               `formula_expression`   TEXT         NOT NULL COMMENT '表达式引擎公式 (如: (base_salary/month_days)*attendance_days)',
                               `condition_expression` TEXT         DEFAULT NULL COMMENT '前置触发条件 (如: employment_status == 1)',
                               `priority`             INT          NOT NULL DEFAULT 0 COMMENT '执行顺序',
                               `result_key`           VARCHAR(64)  NOT NULL COMMENT '结果变量名',
                               `accumulative_flag`    TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否叠加',
                               `status`               TINYINT(1)   NOT NULL DEFAULT 1,
                               `delete_flag`          BIGINT(1)    NOT NULL DEFAULT '0',
                               `create_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               `create_by`            VARCHAR(64)  NOT NULL DEFAULT 'admin',
                               `update_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               `update_by`            VARCHAR(64)  NOT NULL DEFAULT 'admin',
                               PRIMARY KEY (`id`),
                               KEY `idx_salary_group` (`salary_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='【V2.0 预留】薪资动态计算规则(公式)表';


-- ==========================================================
-- 21. 薪资分类表（统一收入/扣款分类体系）
-- ==========================================================
DROP TABLE IF EXISTS `salary_category`;
CREATE TABLE `salary_category` (
                                   `id`              BIGINT       NOT NULL AUTO_INCREMENT,
                                   `category_code`   VARCHAR(64)  NOT NULL COMMENT '分类编码（BONUS / BASIC / LIFE / ATTENDANCE）',
                                   `category_name`   VARCHAR(64)  NOT NULL COMMENT '分类名称',
                                   `category_type`   TINYINT(1)   NOT NULL COMMENT '分类类型: 1收入分类 2扣款分类 3通用分类',
                                   `parent_id`       BIGINT       NOT NULL DEFAULT 0 COMMENT '父级分类ID（用于二级分类）',
                                   `sort_value`      INT          NOT NULL DEFAULT 0,
                                   `status`          TINYINT(1)   NOT NULL DEFAULT 1,
                                   `delete_flag`          BIGINT(1)    NOT NULL DEFAULT '0',
                                   `create_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `create_by`            VARCHAR(64)  NOT NULL DEFAULT 'admin',
                                   `update_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   `update_by`            VARCHAR(64)  NOT NULL DEFAULT 'admin',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_category_code` (`category_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资统一分类表';
