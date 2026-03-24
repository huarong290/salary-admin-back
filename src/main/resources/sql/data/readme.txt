核心薪资引擎架构设计白皮书 (Salary Core Engine V1.0)
1. 架构愿景与定位 (Executive Summary)
本薪资引擎定位为**“高内聚、可追溯、支持跨国双币拆分”**的企业级核心算薪中枢。系统采用领域驱动设计（DDD）与元数据驱动架构，旨在彻底解放 HR 的高频手工导表工作，确保税务合规，并为未来的大数据分析和自动化规则引擎（V2.0）提供完美的底层数据模型。

2. 核心架构设计原则 (Core Architectural Principles)
时间切片寻址 (Temporal Matching)：摒弃静态关联，采用拉链表设计。算薪引擎基于“发薪月时间切片”自动穿越历史，精准匹配当时生效的档案，杜绝跨月重算导致的数据污染。

计算与发放解耦 (Separation of Concerns)：全程采用单一基准本位币（如 CNY）进行个税与社保的精准核算，在最终结算落地时，再根据实时汇率拆分为多币种（如 CNY + USDT）进行组合发放。

零信任防篡改 (Zero-Trust Snapshotting)：所有算薪流水账单必须生成自包含的 JSON 快照（包含计算公式、当时汇率、字典 ID）。实现“物理字段供快速检索，JSON 快照供防篡改审计”。

元数据驱动 (Metadata-Driven)：底层计算逻辑不依赖硬编码（Hardcode），而是由动态定义的业务分类、字典属性（如 taxable_flag）和未来的表达式公式来驱动。

3. 领域分层模型 (Domain Layered Model)
系统划分为严密的 5 层抽象结构，强制执行**“下层不依赖上层”**的单向数据流原则：

💡 L1: 元数据层 (Metadata Layer)

核心表：sys_dict_type, sys_dict_item

职责：提供全系统无状态的 UI 级枚举翻译（如：性别、支付状态、发薪方式）。

约束：绝不参与任何薪资核心数值与逻辑的运算。

💡 L2: 结构定义层 (Structural Layer)

核心表：salary_category

职责：定义薪资大盘的“无限极骨架树”（如：基本薪酬、奖金福利、法定扣款）。

场景：为下游 Flink 实时抽取薪资明细至 TiDB 数仓时，提供向上卷钻（Roll-up）和多维聚合的结构依据。

💡 L3: 业务定义层 (Definition Layer)

核心表：salary_income_type, salary_deduction_type

职责：具象化的业务实体定义。携带影响算薪链路的布尔值特权开关（如 social_base_flag 是否计入社保基数，tax_deductible_flag 是否税前扣除）。

约束：引擎算税、算基数时的唯一准则来源。

💡 L4: 行为与规则层 (Behavioral Layer) - V2.0 预留

核心表：salary_rule, salary_factor, salary_group

职责：系统的“动态大脑”。基于公式表达式（如 (base_salary / month_days) * attendance），按设定的 priority（优先级）将静态因子转化为动态金额。

💡 L5: 事实与凭证层 (Fact & Snapshot Layer)

核心表：salary_archive, salary_payment_record

职责：业务流转最终落地的“不可篡改凭证”。将前 4 层的逻辑在特定时间切片下计算出的结果，进行扁平化和 JSON 快照固化，作为财务对账的绝对真理（Single Source of Truth）。

4. 核心引擎执行流水线 (Engine Execution Pipeline)
引擎单人核算流（createRecordByCalculation）遵循严谨的有向无环图（DAG）执行顺序，确保依赖关系绝对正确：

上下文加载 (Context Init)：

读取 salary_period 获取计薪天数、出勤天数、满勤标记。

调用 matchArchiveByTimeSlice 时光机，抓取当期生效的 salary_archive。

基准核算 (Base Calculation)：

推算底薪折算金额：标准底薪 ÷ 计薪天数 × 实际出勤天数。

基于考勤系统的零信任裁定，判断并压入全勤奖金额。

档案固定项核算 (Fixed Items)：

遍历 salary_archive_item，根据 calc_type 执行固定额、基数比例或出勤折算。

通过 type_id 探查 L3 层属性，累加社保/公积金等税前可扣除总额 (socialSecurityDeductionForTax)。

当期变动项归集 (Variable Items)：

汇总阅后即焚的 salary_income_detail 与 salary_deduction_detail，计入当期应发与应扣池。

税务核算拦截器 (Tax Calculation)：

读取档案 tax_scheme。若需计税，利用公式：(应发总计 - 税前可扣除五险一金 - 5000) × 适用税率 - 速算扣除数，生成个税扣款明细。

支付拆分与快照固化 (Split & Snapshot)：

计算最终本币实发金额（兜底防负数）。

填充多币种拆分字段（如 split_cny_amount, split_usdt_amount, usdt_exchange_rate）。

将所有公式、金额、类别组装为 SalarySnapshotDTO，序列化写入 detail_json，并落盘至 salary_payment_record。

5. 数据边界与合规要求 (Data Boundary & Compliance)
历史不可变原则：已产生 salary_payment_record 的关联汇总单，严禁物理删除。调账必须通过触发**人工强制平账（is_manual=1）或在下期生成补差变动项（Retro-Pay）**来实现。

汇率锁定原则：发薪快照一经生成，exchange_rate 永久锁定，不受后续全局配置表汇率变动的影响。

档案隔离原则：salary_archive_item 仅存长期有效规则，一次性奖惩严禁写入档案，必须由临时表（Detail）承载。

6. 未来演进路线 (V2.0 Roadmap)
动态表达式引擎接入：引入 AviatorScript，剥离硬编码运算，实现 HR 可视化拖拽生成算薪公式。

大数据生态融合：通过 Flink CDC 实时监听 salary_payment_record（解析 detail_json）与 salary_category，将异构薪资流水清洗为宽表，落入 TiDB。为管理层提供秒级的跨国人力成本实时多维大屏分析。

多节点并行核算：引入分布式任务调度框架，对万人级别的大型发薪批次进行分片（Sharding）并行核算，将分钟级算薪压缩至秒级