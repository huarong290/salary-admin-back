# salary-admin-back

企业级薪资管理后台系统（后端服务），提供 RBAC 权限体系与可配置化的薪资核算引擎。

## 技术栈

- **Spring Boot 3.5.8 + JDK 21**（虚拟线程）
- **MyBatis-Plus 3.5.16**（逻辑删除、分页、代码生成）
- **Spring Security + JWT**（双 Token、令牌轮转、设备绑定、复用检测）
- **Redis (Lettuce)**（验证码、权限缓存、会话管理）
- **AviatorScript 5.4.3**（薪资公式动态计算引擎，DECIMAL128 高精度）
- **MapStruct / Hutool / Fastjson2 / springdoc-openapi**

## 项目结构

```
com.salary.admin
├── controller/           # 接口层 (系统模块 + salary 薪资模块)
├── service/              # 业务层 (接口 + impl)
│   └── impl/salary/engine/  # 薪资核算引擎组件
│       ├── AbstractSalaryProcessor   # 核算模板方法 (管道驱动主干)
│       ├── PipelineStepExecutor      # 节点执行器 (条件阻断/脚本执行/审计埋点)
│       ├── SalaryPersistProcessor    # 真实核算 (事务落库)
│       ├── SalaryPreviewProcessor    # 预览核算 (纯内存试算)
│       ├── SalaryCalcAggregator      # 内存聚合器 (快照构建)
│       └── SalaryTaxCalculator       # 个税累计预扣计算器
├── engine/SalaryRuleEngine # Aviator 公式执行引擎
├── mapper/                # auto (生成) + ext (手写扩展 SQL)
├── model/                 # entity / dto / vo
├── convert/               # MapStruct 转换器
├── config/                # 安全、Redis、MyBatis-Plus 等配置
└── doc/sql/v9/            # 数据库初始化脚本 (建表 + 种子数据)
```

## 薪资核算引擎 (核心)

引擎采用 **管道编排 + 公式脚本** 的动态核算架构：

1. `salary_calc_pipeline_info` 定义核算管道（如 `OFFICIAL_STAFF_2026`）
2. `salary_calc_pipeline_step` 按 阶段(stage) + 顺序(sort_order) 编排执行步骤
3. `salary_calc_rule` 存放 Aviator 公式脚本（59+ 项规则，如底薪折算、全勤奖、KPI 绩效）
4. `SalaryCalcContextServiceImpl.buildEmployeeContext()` 组装核算上下文 env（考勤、档案时间切片、手工调账、KPI、社保公积金）
5. `PipelineStepExecutor` 逐节点执行：条件阻断 → 脚本计算 → 空值跳过 → 强阻断熔断 → 异步审计埋点
6. **个税节点 `AUTO_TAX_CALC` 走内置 Java 累计预扣计算器**（`SalaryTaxCalculator`），不依赖脚本

支持 单人核算 / 单人预览（档案版本时间旅行）/ 批量核算（异常隔离、锁定单据跳过）。

## 快速开始

### 1. 初始化数据库

```bash
# 创建数据库 (v9)
mysql -uroot -p -e "CREATE DATABASE salary_admin_v9 DEFAULT CHARACTER SET utf8mb4;"
# 导入初始化脚本 (建表 + 种子数据: 账号/角色/菜单/字典/规则/管道/配置)
mysql -uroot -p salary_admin_v9 < doc/sql/v9/salary_admin_v9.sql
```

### 2. 启动服务

```bash
# Mac 本地 (默认 profile: mac-mini, 连接 127.0.0.1:6446 MySQL Router + Redis 哨兵)
mvn spring-boot:run

# 或指定环境
mvn spring-boot:run -Dspring-boot.run.profiles=windows   # Windows 单机 3306
mvn spring-boot:run -Dspring-boot.run.profiles=mac       # Mac 单机 33061
```

默认账号：`admin / 123456`

### 3. 联调前端

前端项目 `salary-admin-web` 开发代理指向 `http://localhost:28080`（见 `.env.development`）。

## 默认数据

- 账号：`system` / `admin` / `user` / `test`（密码均为 `123456`）
- 角色：`SUPER_ADMIN`（全菜单）/ `ADMIN` / `USER` / `TEST`
- 管道：`OFFICIAL_STAFF_2026`（正式员工核算流，含 50+ 步骤）
- 全局配置：本位币、个税开关、加班费率等（`salary_config` 表，可通过"薪资全局配置"页面维护）

## 多币种结算 (v9 增强)

系统支持不同国家/地区员工按各自币种结算工资：

- **币种维度 = 员工档案**：定薪/调薪时在档案上指定 `currency`（如 CNY/PHP/USD/AED），引擎核算以档案币种为准
- **汇率表** `salary_exchange_rate`：按 `结算月份 × 币种` 维护对基准币(CNY)的汇率，财务月末维护；引擎核算时查询当月汇率，**未配置回退 1:1** 不阻塞核算
- **口径**：工资单/流水/明细均按员工币种展示金额；`original_amount / exchange_rate / settlement_amount` 三条腿落库，为后续按基准币汇总统计预留
- **全局配置** `SETTLEMENT_CURRENCY` 定位为"报表基准币种"（统一折算口径），不再决定员工实发币种

> 备注：个税累计预扣当前统一按 5000/月 减除费用口径计算；外籍员工附加减除、分国别税率规则属后续税务规则增强项。
