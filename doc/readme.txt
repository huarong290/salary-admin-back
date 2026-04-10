三层模型
① 档案层（规则层）
salary_archive
salary_archive_item

② 计算层（引擎）
rule_script + pipeline + context
③ 结果层（事实层）
salary_item_detail   ←（你新加的）
salary_summary
salary_payment_record


buildEmployeeContext()
    ↓
buildContextData()   ← 纯构建（可复用、可测试）
    ↓
fillArchive()
fillEmployee()
fillAttendance()
fillKpi()
    ↓
saveSnapshot()

yelive
假设当前计算的只有下面这些
每月底薪  5840
KPI      A继续 50%  B绩效20%  C0%
房补     300
全勤奖    70

yelive
假设当前计算的只有下面这些
每月底薪  6250
KPI      A继续 50% 3125  B绩效20%  C0%
房补     300
全勤奖    70
8205.000000
A:9675 B：7800

202504  10425.000000  360
202505  9208.230000   435
202506  8750.000000   560
202507  10212.150000  147.15
202508  8205.000000    15
202509  7165.000000
202510  10663.230000
202511  8190.000000
202512  8857.940000
202601  10990.000000
202602  17036.000000

