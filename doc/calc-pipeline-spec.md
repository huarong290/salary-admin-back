# 薪资计算管道编排规范

> 适用对象：`salary_calc_pipeline_step`（管道步骤编排表）
> 涉及代码：`AbstractSalaryProcessor` / `PipelineStepExecutor` / `SalaryTaxCalculator`
> 配套脚本：`doc/sql/v9/pipeline_reorder_official_staff_2026.sql`

---

## 1. 背景与目标

引擎按"管道 + 步骤"驱动算薪，**步骤的先后顺序直接决定个税算得对不对**：

- 原编排（`OFFICIAL_STAFF_2026` v1）是在**尚未接入个税**的阶段设计的，
  当时顺序只影响"收支分类与展示"，所以把返还/补发类统一放在了末尾；
- 接入个税后，顺序变成**计算正确性问题**：排在个税步骤之后的应税收入，**永远不会进入税基**。
- 实测发现的缺陷：`PREV_MONTH_ADJUSTMENT`（上月补发/续扣，`taxable_flag = 1`）
  排在 `stage 5 / sort 660`，而个税在 `stage 4`，导致该笔应税收入漏出税基（少扣税）。

本规范给出**阶段划分、字段规范、校验清单与迁移方式**，用于把编排从"经验排序"变成"可校验的规则"。

---

## 2. 引擎执行模型（必须先理解）

| 机制 | 说明 | 代码位置 |
|---|---|---|
| **排序规则** | `ORDER BY stage ASC, sort_order ASC`（同为升序） | `AbstractSalaryProcessor#getPipelineSteps` |
| **逐步累加** | 每执行完一步，把金额写入上下文并按项目大类累加 | `AbstractSalaryProcessor#process` |
| **税基来源** | 每步之后刷新 `env["_taxableIncome"] = aggregator.getTaxableIncomeTotal()` | 同上 |
| **个税取值** | 执行到 `AUTO_TAX_CALC` 时读取当时的 `_taxableIncome` 作为计税基数 | `PipelineStepExecutor`（`AUTO_TAX_RULE_CODE` 分支） |
| **收支分类** | 由 `salary_item_config.item_category` 决定，**与 stage 无关** | `SalaryCalcAggregator#accumulate` |
| **stage 的作用** | 仅参与排序 + 写入审计日志（`salary_calc_log.stage`），**代码中无任何 `stage == N` 的硬编码判断** | `PipelineStepExecutor` |

> 结论：**stage 只表达"业务阶段 + 执行次序"**，可以自由重排，不会影响收支分类逻辑。

---

## 3. 编排三条铁律

```
① taxable_flag = 1 的收入项         →  必须排在个税步骤之前
② tax_deductible_flag = 1 的扣除项  →  必须排在个税步骤之前
③ 税后项（罚款 / 水电 / 押金 / 返还 / 公司支出）→  必须排在个税步骤之后
```

- 违反 ① → **少扣税**（应税收入没进税基）
- 违反 ② → **多扣税**（税前扣除没生效）
- 违反 ③ → **税基被污染**（税后项目错误地冲减/增加税基）

`taxable_flag` / `tax_deductible_flag` 的取值优先级：
`salary_archive_item.taxable_flag`（档案项快照，按员工） → 为空回退 `salary_item_config.taxable_flag`（项目默认）。

---

## 4. 阶段划分（S1 – S9）

| 阶段 | 语义 | 步数 | sort 区间 | 与税基的关系 |
|---|---|---|---|---|
| **S1** | 基础工资与考勤 | 4 | 10 – 40 | 进税基（应发主体） |
| **S2** | 绩效与提成 | 3 | 100 – 120 | 进税基 |
| **S3** | 津贴与福利 | 17 | 200 – 360 | 多数进税基（`MEAL_ALLOW`/`BIRTHDAY_BONUS` 不计税） |
| **S4** | 奖金与年终 | 16 | 400 – 550 | 进税基 |
| **S5** | 补发与追溯 | 4 | 600 – 630 | ★ 应税项（`PREV_MONTH_ADJUSTMENT`）进税基 |
| **S6** | 税前扣除 | 5 | 700 – 740 | 减少税基（社保个人、缺勤/迟到扣款） |
| **S7** | **个税（分水岭）** | **1** | **900** | **`AUTO_TAX_CALC`，`block_flag = 1`** |
| **S8** | 税后扣款与返还 | 10 | 1000 – 1090 | 不影响税基 |
| **S9** | 公司成本 | 2 | 1100 – 1110 | 不影响税基与员工净额 |

**设计要点**

1. **阶段号有序但留白**：S1–S9 之间为未来阶段预留（如"专项附加扣除"可插为 S6.5 → 用 sort 区间扩展）。
2. **编号不与执行顺序矛盾**：个税之后的项目编号一律 **大于 900**（旧编排中 stage 5 用 600–670、个税用 999，编号与执行顺序相反，属于埋雷）。
3. **S6 与 S8 的界限**就是"是否可税前扣除"：社保个人、缺勤/迟到扣款 → S6；罚款、水电、押金、离职结算 → S8。
4. **S9 单列公司成本**：`item_category = 4` 不进员工净额，单列便于公司成本核算。

---

## 5. 字段规范

| 字段 | 规范 | 说明 |
|---|---|---|
| `stage` | 只表达业务阶段（S1–S9） | 不承担"微调顺序"职责 |
| `sort_order` | 同阶段内 **步长 10**（10/20/30…） | 留空隙，插队不必重排全表 |
| `condition_script` | 精确控制适用性，如 `kpiCoefficient > 0`、`calcMode == 2` | 避免无谓计算与 0 金额明细 |
| `block_flag` | **仅核心节点为 1**（`BASE_SALARY`、`AUTO_TAX_CALC`） | 其余一律 0，保证单人异常不拖垮整批 |
| `skip_if_null` | 可选项设 1（金额 0 则不落明细） | 工资条更干净 |
| `status` | 用 **停用（0）** 代替删除 | 保留可追溯；版本升级时可复制 |

---

## 6. 编排校验清单

建议在【保存管道】接口与启动期体检中固化以下校验：

| # | 校验项 | 不通过的后果 |
|---|---|---|
| 1 | 必备节点齐全：`BASE_SALARY`、`AUTO_TAX_CALC` | 缺个税节点 → 税费恒为 0（静默算错） |
| 2 | 个税步骤唯一，且 `block_flag = 1` | 税被算两次 / 异常被静默吞掉 |
| 3 | **铁律①**：无 `taxable_flag=1` 的步骤排在个税之后 | 少扣税 |
| 4 | **铁律②**：无 `tax_deductible_flag=1` 的步骤排在个税之后 | 多扣税 |
| 5 | **铁律③**：税后语义项（`REBATE`/`DEPOSIT`/`FINE`/`UTILITY`/`ER_`）不得早于个税 | 税基被污染 |
| 6 | 每个 `rule_code` 都能在 `salary_item_config` 找到对应项 | `configMap` 取不到 → 按"收入"兜底，静默算错 |
| 7 | 同一 `(stage, sort_order)` 不重复；不得引用 `status = 0` 的规则 | 顺序不确定 / 运行期异常 |

> 校验 3、4 对应的 SQL 见配套脚本的"第 2.2 步验证"。

---

## 7. 迁移执行（对应 SQL）

```text
① 备份      →  salary_calc_pipeline_step_bak_20260912（脚本第 0 步）
② 重排      →  62 步 stage/sort_order 一次性更新（脚本第 1 步，幂等）
③ 验证      →  阶段分布 / 铁律校验 / 个税位置（脚本第 2 步）
④ 回滚      →  从备份表还原（脚本第 3 步，已注释，按需启用）
```

**影响面（重要）**

| 项目 | 影响 |
|---|---|
| `PREV_MONTH_ADJUSTMENT` | **唯一会改变金额的项**：由税后移到税前，有"上月补发"的员工个税将（正确地）上升 |
| 其余 61 步 | 仅语义归位；其 `taxable_flag` / `tax_deductible_flag` 均为 0，**不改变任何金额** |
| `AUTO_TAX_CALC` | `stage 4 → 7`（仅影响审计日志中的 stage 记录） |

---

## 8. 版本管理建议

- **不要直接改已用于历史工资单的版本**：推荐用 `ISalaryCalcPipelineInfoService#copyAndUpgradePipeline(sourceId)`
  复制生成新版本（version + 1、`default_flag = 0`），在**新版本**上执行重排，
  再用 `setDefaultPipeline(id)` 把默认指向新版本 —— 历史版本含义保持不变。
  （若当前仍处于开发期、无需保留历史口径，直接在 v1 上重排即可。）
- 全局默认管道必须**唯一**：`default_flag = 1` 且 `status = 1`；
  算薪时若未显式指定管道，引擎按
  `调用方指定 → 默认管道 → 唯一可用管道 → 快速失败` 的顺序解析（见 `CalcPipelineResolver`）。

---

## 9. 后续演进建议

1. **显式依赖 + 拓扑排序**：目前顺序靠 `stage + sort_order` 隐式表达，容易踩坑（本次缺陷即由此而来）。
   建议在步骤表增加 `depends_on`（声明"我依赖哪些 `rule_code` 的结果"），
   引擎做拓扑排序 + 环检测，`sort_order` 降级为"同层排序"；保存管道时拒绝循环依赖。
2. **编排可视化**：前端按 stage 分组展示，并在界面上实时提示违反铁律的步骤。
3. **试算校验**：重排后用典型员工跑"单人预览"，比对应发/税基/税额三项与预期一致。

---

## 附录 A：rule_code → 阶段对照表（62 项）

| 阶段 | rule_code |
|---|---|
| **S1 基础工资与考勤**（4） | `BASE_SALARY`、`OVERTIME_PAY_DAY`、`OVERTIME_PAY_HOUR`、`ATTENDANCE_BONUS` |
| **S2 绩效与提成**（3） | `KPI_BONUS`、`COMMISSION_SALES`、`COMMISSION_AGENT` |
| **S3 津贴与福利**（17） | `HOUSING_ALLOW`、`MEAL_ALLOW`、`SHIFT_12H_ALLOWANCE`、`QUARANTINE_ALLOWANCE`、`OTHER_ALLOWANCE`、`SAFETY_CARD_BONUS`、`ANNUAL_LEAVE_BONUS`、`REFERRAL_BONUS`、`BIRTHDAY_BONUS`、`FESTIVAL_SPRING_GIFT`、`FESTIVAL_SPRING_BONUS`、`FESTIVAL_DRAGON_BOAT_GIFT`、`FESTIVAL_DRAGON_BOAT_BONUS`、`FESTIVAL_MID_AUTUMN_GIFT`、`FESTIVAL_MID_AUTUMN_BONUS`、`EVENT_EURO_CUP`、`EVENT_WORLD_CUP` |
| **S4 奖金与年终**（16） | `ANNUAL_BONUS_13` ~ `ANNUAL_BONUS_19`（含 .5 档共 13 项）、`LOYALTY_BONUS_2Y`、`LOYALTY_BONUS_5Y`、`LOYALTY_BONUS_10Y` |
| **S5 补发与追溯**（4） | `ATTENDANCE_REISSUE`、`PREV_MONTH_ADJUSTMENT`★、`SI_REISSUE_IND`、`EXPENSE_REIMBURSE_ONBOARD` |
| **S6 税前扣除**（5） | `SI_PENSION_IND`、`SI_MED_IND`、`SI_HOUSING_IND`、`ABSENT_DEDUCTION`、`LATE_DEDUCTION` |
| **S7 个税**（1） | `AUTO_TAX_CALC` ★分水岭 |
| **S8 税后扣款与返还**（10） | `UTILITY_DEDUCTION`、`FINE_DEDUCTION`、`PASSPORT_FEE_DEDUCTION`、`DEPOSIT_DEDUCTION_CURRENT`、`OTHER_DEDUCTION`、`RESIGNATION_SETTLEMENT`、`DEPOSIT_REFUND_CURRENT`、`FINE_REBATE`、`UTILITY_REBATE`、`PASSPORT_FEE_REBATE` |
| **S9 公司成本**（2） | `ER_PENSION_COMP`、`ER_VISA_COMP` |

---

## 附录 B：常见坑

| 坑 | 现象 | 规避 |
|---|---|---|
| 应税项排在个税之后 | 少扣税 | 铁律① + 校验 3 |
| 税前扣除排在个税之后 | 多扣税 | 铁律② + 校验 4 |
| 阶段编号与执行顺序矛盾（如税后项编号 < 个税编号） | 误以为已生效，实际顺序如旧 | 编号与 stage 顺序保持一致（附录 A 已按阶段分段编号） |
| `stage` 与 `item_category` 混为一谈 | 以为改 stage 能改收支分类 | 分类只看 `item_category`，stage 只排序 |
| 直接删步骤而非停用 | 历史口径丢失、无法回溯 | 用 `status = 0` 停用；升级用 `copyAndUpgradePipeline` |
| 一个 `sort_order` 被多步复用 | 执行顺序不确定 | 校验 7 |
