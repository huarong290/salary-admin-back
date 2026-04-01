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


yelive
假设当前计算的只有下面这些
每月底薪  5840
KPI      A继续 50%  B绩效20%  C0%
房补     300
全勤奖    70