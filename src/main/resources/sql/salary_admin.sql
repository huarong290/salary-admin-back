-- ==========================================================
-- 1. 用户表 sys_user
-- ==========================================================
CREATE TABLE `sys_user`
(
    `id`              BIGINT UNSIGNED       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`        varchar(50)  NOT NULL DEFAULT '' COMMENT '用户名',
    `password`        varchar(255) NOT NULL DEFAULT '' COMMENT '加密密码 (扩容至255，适应未来更复杂的加密算法)',
    `nickname`        varchar(50)  NOT NULL DEFAULT '' COMMENT '用户昵称',
    `email`           varchar(100) NOT NULL DEFAULT '' COMMENT '邮箱',
    `phone`           varchar(20)  NOT NULL DEFAULT '' COMMENT '手机号',
    `sex`             TINYINT UNSIGNED      NOT NULL DEFAULT '0' COMMENT '用户性别 (0:未知, 1:男, 2:女)',
    `avatar`          varchar(255) NOT NULL DEFAULT '' COMMENT '头像地址URL',
    `status`          TINYINT UNSIGNED      NOT NULL DEFAULT '1' COMMENT '状态 (0:禁用, 1:正常)',
    `last_login_time` DATETIME     NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '最后登录时间',
    -- 逻辑删除标志改为 BIGINT，删除时写入主键 ID，彻底解决唯一索引冲突
    `delete_flag`     BIGINT UNSIGNED       NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`       varchar(64)  NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       varchar(64)  NOT NULL DEFAULT 'system' COMMENT '修改者',
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
    `role_name`   varchar(50) NOT NULL COMMENT '角色名称',
    `role_code`   varchar(50) NOT NULL COMMENT '角色编码 (如: ADMIN, HR_MGR)',
    `role_sort`   int         NOT NULL DEFAULT '0' COMMENT '显示顺序',
    `role_status` TINYINT UNSIGNED   NOT NULL DEFAULT '1' COMMENT '状态',
    `role_desc`   varchar(50) NOT NULL DEFAULT '' COMMENT '角色描述',
    `remark`      varchar(500) NOT NULL DEFAULT '' COMMENT '备注',
    -- 逻辑删除标志改为 BIGINT
    `delete_flag` BIGINT UNSIGNED      NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`   varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
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
    `menu_name`       varchar(50)  NOT NULL COMMENT '菜单名称 (如：系统管理、用户新增)',
    `menu_code`       varchar(100) NOT NULL COMMENT '菜单编码 (唯一标识)',
    `menu_path`       varchar(200) NOT NULL DEFAULT '' COMMENT '前端路由地址(如:/system/user，按钮无需路由)',
    `menu_component`  varchar(200) NOT NULL DEFAULT '' COMMENT '前端组件路径 (如: system/user/index)',
    `menu_redirect`   varchar(200) NOT NULL DEFAULT '' COMMENT '重定向地址',
    `menu_icon`       VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '菜单图标，用于前端显示',
    `menu_permission` varchar(100) NOT NULL DEFAULT '' COMMENT '后端权限标识 (如 sys:user:add)',
    `menu_type`       TINYINT UNSIGNED    NOT NULL DEFAULT '1' COMMENT '菜单类型 (1:目录, 2:菜单, 3:按钮)',
    `menu_parent_id`  BIGINT UNSIGNED       NOT NULL DEFAULT '0' COMMENT '父级ID',
    `menu_sort`       int          NOT NULL DEFAULT '0' COMMENT '排序',
    `menu_visible`    TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '菜单是否可见：0可见 1隐藏，前端渲染控制',
    `menu_status`     TINYINT UNSIGNED    NOT NULL DEFAULT '1' COMMENT '菜单业务状态 (1:正常 0:停用)',
    -- 逻辑删除标志改为 BIGINT
    `delete_flag`     BIGINT UNSIGNED       NOT NULL DEFAULT '0' COMMENT '删除标识 (0:未删, 其他值:已删的主键ID)',
    `create_by`       varchar(64)  NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       varchar(64)  NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    -- 联合 delete_flag 建立唯一索引
    UNIQUE KEY `uk_menucode_del` (`menu_code`, `delete_flag`) USING BTREE,
    -- 为父级ID增加普通索引，极大提升构建菜单树时的查询性能
    KEY `idx_parent_id` (`menu_parent_id`)
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
    `create_by`   varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
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
    `create_by`   varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    -- 中间表联合唯一索引同样加上 delete_flag
    UNIQUE KEY `uk_role_menu_del` (`role_id`, `menu_id`, `delete_flag`),
    KEY           `idx_role_id` (`role_id`),
    KEY           `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4  COMMENT='角色与菜单关联表';