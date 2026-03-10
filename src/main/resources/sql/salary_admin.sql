-- ==========================================================
-- 1. 用户表 sys_user
-- ==========================================================
CREATE TABLE `sys_user`
(
    `id`              BIGINT UNSIGNED       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`        VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '用户名',
    `password`        VARCHAR(255) NOT NULL DEFAULT '' COMMENT '加密密码 (扩容至255，适应未来更复杂的加密算法)',
    `nickname`        VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '用户昵称',
    `email`           VARCHAR(100) NOT NULL DEFAULT '' COMMENT '邮箱',
    `phone`           VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '手机号',
    `sex`             TINYINT UNSIGNED      NOT NULL DEFAULT '0' COMMENT '用户性别 (0:未知, 1:男, 2:女)',
    `avatar`          VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像地址URL',
    `status`          TINYINT UNSIGNED      NOT NULL DEFAULT '1' COMMENT '状态 (0:禁用, 1:正常)',
    `last_login_time` DATETIME     NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '最后登录时间',
    -- 逻辑删除标志改为 BIGINT，删除时写入主键 ID，彻底解决唯一索引冲突
    `delete_flag`     BIGINT UNSIGNED       NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`       VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    -- 联合 delete_flag 建立唯一索引，防止同名用户删除后再创建报错
    UNIQUE KEY `uk_username_del` (`username`, `delete_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


-- ==========================================================
-- 2. 角色表 sys_role
-- ==========================================================
CREATE TABLE `sys_role`
(
    `id`          BIGINT UNSIGNED      NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    `role_code`   VARCHAR(50)  NOT NULL COMMENT '角色编码 (如: ADMIN, HR_MGR)',
    `role_sort`   int          NOT NULL DEFAULT '0' COMMENT '显示顺序',
    `role_status` TINYINT UNSIGNED   NOT NULL DEFAULT '1' COMMENT '状态',
    `role_desc`   VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '角色描述',
    `remark`      VARCHAR(500) NOT NULL DEFAULT '' COMMENT '备注',
    -- 逻辑删除标志改为 BIGINT
    `delete_flag` BIGINT UNSIGNED      NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`   VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    -- 联合 delete_flag 建立唯一索引
    UNIQUE KEY `uk_rolecode_del` (`role_code`, `delete_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';


-- ==========================================================
-- 3. 菜单表 sys_menu
-- ==========================================================
CREATE TABLE `sys_menu`
(
    `id`              BIGINT UNSIGNED       NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name`       VARCHAR(50)  NOT NULL COMMENT '菜单名称 (如：系统管理、用户新增)',
    `menu_code`       VARCHAR(100) NOT NULL COMMENT '菜单编码 (唯一标识)',
    `menu_path`       VARCHAR(200) NOT NULL DEFAULT '' COMMENT '前端路由地址(如:/system/user，按钮无需路由)',
    `menu_component`  VARCHAR(200) NOT NULL DEFAULT '' COMMENT '前端组件路径 (如: system/user/index)',
    `menu_redirect`   VARCHAR(200) NOT NULL DEFAULT '' COMMENT '重定向地址',
    `menu_icon`       VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '菜单图标，用于前端显示',
    `menu_permission` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '后端权限标识 (如 sys:user:add)',
    `menu_type`       TINYINT UNSIGNED    NOT NULL DEFAULT '1' COMMENT '菜单类型 (1:目录, 2:菜单, 3:按钮)',
    `menu_parent_id`  BIGINT UNSIGNED       NOT NULL DEFAULT '0' COMMENT '父级ID',
    `menu_sort`       int          NOT NULL DEFAULT '0' COMMENT '排序',
    `menu_visible`    TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '菜单是否可见：0可见 1隐藏，前端渲染控制',
    `menu_status`     TINYINT UNSIGNED    NOT NULL DEFAULT '1' COMMENT '菜单业务状态 (1:正常 0:停用)',
    -- 逻辑删除标志改为 BIGINT
    `delete_flag`     BIGINT UNSIGNED       NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`       VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       VARCHAR(64)  NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    -- 联合 delete_flag 建立唯一索引
    UNIQUE KEY `uk_menucode_del` (`menu_code`, `delete_flag`) USING BTREE,
    -- 为父级ID增加普通索引，极大提升构建菜单树时的查询性能
    KEY               `idx_parent_id` (`menu_parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限菜单表';


-- ==========================================================
-- 4. 用户-角色关联表 sys_user_role
-- ==========================================================
CREATE TABLE `sys_user_role`
(
    `id`          BIGINT UNSIGNED       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT UNSIGNED       NOT NULL COMMENT '用户ID',
    `role_id`     BIGINT UNSIGNED       NOT NULL COMMENT '角色ID',
    -- 逻辑删除标志改为 BIGINT
    `delete_flag` BIGINT UNSIGNED       NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    -- 中间表联合唯一索引同样加上 delete_flag
    UNIQUE KEY `uk_user_role_del` (`user_id`, `role_id`, `delete_flag`),
    KEY           `idx_user_id` (`user_id`),
    KEY           `idx_role_id` (`role_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4  COMMENT='用户与角色关联表';


-- ==========================================================
-- 5. 角色-菜单关联表 sys_role_menu
-- ==========================================================
CREATE TABLE `sys_role_menu`
(
    `id`          BIGINT UNSIGNED       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id`     BIGINT UNSIGNED       NOT NULL COMMENT '角色ID',
    `menu_id`     BIGINT UNSIGNED       NOT NULL COMMENT '菜单ID',
    -- 逻辑删除标志改为 BIGINT
    `delete_flag` BIGINT UNSIGNED       NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    -- 中间表联合唯一索引同样加上 delete_flag
    UNIQUE KEY `uk_role_menu_del` (`role_id`, `menu_id`, `delete_flag`),
    KEY           `idx_role_id` (`role_id`),
    KEY           `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4  COMMENT='角色与菜单关联表';

-- ==========================================================
-- 6. 员工基本信息表 hr_employee
-- ==========================================================
CREATE TABLE `hr_employee`
(
    `id`                   BIGINT      NOT NULL AUTO_INCREMENT COMMENT '员工ID',
    `employee_code`        VARCHAR(32) NOT NULL DEFAULT '' COMMENT '员工编号',
    `name`                 VARCHAR(64) NOT NULL DEFAULT '' COMMENT '姓名',
    `company_name`         VARCHAR(128)         DEFAULT NULL COMMENT '所属公司',
    `department`           VARCHAR(128)         DEFAULT NULL COMMENT '部门',
    `employment_status`    VARCHAR(32)          DEFAULT NULL COMMENT '在职状态',
    `is_transferred`       TINYINT(1) DEFAULT '0' COMMENT '是否转岗',
    `accommodation_status` VARCHAR(32)          DEFAULT NULL COMMENT '住宿情况',
    `delete_flag`          TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`            VARCHAR(64) NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            VARCHAR(64) NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_employee_code` (`employee_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工基本信息表';
-- ==========================================================
-- 7. 薪资周期信息表 hr_salary_period
-- ==========================================================
CREATE TABLE `hr_salary_period`
(
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '周期ID',
    `employee_id`      BIGINT       NOT NULL DEFAULT '0' COMMENT '员工ID',
    `work_month`       VARCHAR(255) NOT NULL DEFAULT '' COMMENT '在岗月份',
    `settlement_month` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '结算月份（格式：YYYYMM）',
    `start_date`       date                  DEFAULT NULL COMMENT '开始日期',
    `end_date`         date                  DEFAULT NULL COMMENT '结束日期',
    `month_days`       int                   DEFAULT '0' COMMENT '月天数',
    `attendance_days`  int                   DEFAULT '0' COMMENT '出勤天数',
    `delete_flag`      TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`        VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        VARCHAR(64)  NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                `employee_id` (`employee_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资周期信息表';
-- ==========================================================
-- 8. 薪资汇总与结算表 hr_salary_summary
-- ==========================================================
CREATE TABLE `hr_salary_summary`
(
    `id`               BIGINT      NOT NULL AUTO_INCREMENT COMMENT '汇总ID',
    `hr_period_id`        BIGINT      NOT NULL DEFAULT '0' COMMENT '薪资周期ID',
    `currency`         VARCHAR(16)          DEFAULT 'CNY' COMMENT '结算币种',
    `exchange_rate`    DECIMAL(18, 8)       DEFAULT '1.00000000' COMMENT '汇率',
    `salary_subtotal`  DECIMAL(18, 8)       DEFAULT '0.00000000' COMMENT '应发小计（本币）',
    `salary_total`     DECIMAL(18, 8)       DEFAULT '0.00000000' COMMENT '结算薪资（本币）',
    `salary_converted` DECIMAL(18, 8)       DEFAULT '0.00000000' COMMENT '结算薪资（目标币）',
    `salary_rmb`       DECIMAL(18, 8)       DEFAULT '0.00000000' COMMENT '人民币金额',
    `salary_usdt`      DECIMAL(18, 8)       DEFAULT '0.00000000' COMMENT 'USDT金额',
    `payment_status`   TINYINT(1) DEFAULT '0' COMMENT '支付状态(0未支付 1已支付 2失败)',
    `remark`           text COMMENT '备注',
    `delete_flag`      TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`        VARCHAR(64) NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        VARCHAR(64) NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                `idx_hr_period_id` (`hr_period_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资汇总与结算表';
-- ==========================================================
-- 9. 员工收入主表 hr_salary_income
-- ==========================================================
CREATE TABLE `hr_salary_income`
(
    `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '收入记录ID',
    `hr_period_id`    BIGINT      NOT NULL DEFAULT '0' COMMENT '薪资周期ID',
    `employee_id`  BIGINT      NOT NULL DEFAULT '0' COMMENT '员工ID',
    `total_amount` DECIMAL(18, 8)       DEFAULT '0.00000000' COMMENT '总收入金额',
    `delete_flag`  TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`    VARCHAR(64) NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    VARCHAR(64) NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY            `idx_period_id` (`hr_period_id`),
    KEY            `idx_employee_id` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工收入主表';
-- ==========================================================
-- 10. 员工收入明细表 hr_salary_income_detail
-- ==========================================================
CREATE TABLE `hr_salary_income_detail`
(
    `id`             BIGINT         NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `hr_income_id`      BIGINT         NOT NULL DEFAULT '0' COMMENT '主表ID',
    `hr_period_id`      BIGINT         NOT NULL DEFAULT '0' COMMENT '薪资周期ID',
    `income_type_id` BIGINT            NOT NULL DEFAULT '0' COMMENT '收入类型ID',
    `amount`         DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '收入金额',
    `remark`         VARCHAR(255)            DEFAULT NULL COMMENT '备注说明',
    `delete_flag`    TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`      VARCHAR(64)    NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      VARCHAR(64)    NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY              `idx_income_id` (`hr_income_id`),
    KEY              `idx_income_type_id` (`income_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工收入明细表';
-- ==========================================================
-- 11. 收入类型字典表 hr_salary_income_type
-- ==========================================================
CREATE TABLE `hr_salary_income_type`
(
    `id`          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '收入类型ID',
    `type_name`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT '收入类型名称',
    `category`    VARCHAR(64)          DEFAULT NULL COMMENT '收入分类',
    `description` VARCHAR(255)         DEFAULT NULL COMMENT '收入项说明',
    `delete_flag` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`   VARCHAR(64) NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64) NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_name` (`type_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收入类型字典表';

-- ==========================================================
-- 12. 员工扣款明细表 hr_salary_deduction_detail
-- ==========================================================
CREATE TABLE `hr_salary_deduction_detail`
(
    `id`                BIGINT         NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `hr_period_id`         BIGINT         NOT NULL DEFAULT '0' COMMENT '周期ID',
    `deduction_type_id` BIGINT            NOT NULL DEFAULT '0' COMMENT '扣款类型ID',
    `amount`            DECIMAL(18, 8) NOT NULL DEFAULT '0.00000000' COMMENT '扣款金额',
    `remark`            VARCHAR(255)            DEFAULT NULL,
    `delete_flag`       TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`         VARCHAR(64)    NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         VARCHAR(64)    NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time`       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY                 `idx_period_id` (`hr_period_id`),
    KEY                 `idx_deduction_type_id` (`deduction_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工扣款明细表';
-- ==========================================================
-- 13. 扣款类型字典表 hr_salary_deduction_type
-- ==========================================================
CREATE TABLE `hr_salary_deduction_type`
(
    `id`          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '扣款类型ID',
    `type_name`   VARCHAR(64) NOT NULL DEFAULT '' COMMENT '扣款类型名称',
    `category`    VARCHAR(64)          DEFAULT NULL COMMENT '扣款分类',
    `description` VARCHAR(255)         DEFAULT NULL COMMENT '扣款项说明',
    `is_fixed`    TINYINT(1) DEFAULT '0' COMMENT '是否固定扣款',
    `delete_flag` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
    `create_by`   VARCHAR(64) NOT NULL DEFAULT 'admin' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   VARCHAR(64) NOT NULL DEFAULT 'admin' COMMENT '修改者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_name` (`type_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扣款类型字典表';