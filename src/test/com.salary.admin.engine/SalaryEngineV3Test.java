//package com.salary.admin.engine;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONWriter;
//import com.salary.admin.model.entity.salary.SalaryPeriod;
//import com.salary.admin.model.entity.salary.SalarySummary;
//import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
//import com.salary.admin.service.salary.ISalaryCoreEngine;
//import com.salary.admin.service.salary.ISalarySummaryService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.math.BigDecimal;
//
//@Slf4j
//@SpringBootTest
//public class SalaryEngineV3Test {
//
//    @Autowired
//    private ISalaryCoreEngine salaryCoreEngine;
//
//    @Autowired
//    private ISalarySummaryService salarySummaryService;
//
//    @Test
//    public void testPipelineCalculate() {
//        log.info("====== 🚀 开始测试 V3.0 管道规则引擎 ======");
//
//        // 1. 模拟生成一条空的汇总单记录 (确保数据库能 update)
//        SalarySummary mockSummary = new SalarySummary();
//        mockSummary.setEmployeeId(888L);
//        mockSummary.setPeriodId(999L);
//        mockSummary.setSettlementMonth("202603");
//        mockSummary.setEmployeeName("架构测试专家");
//        mockSummary.setEmployeeCode("TEST-001");
//        salarySummaryService.save(mockSummary);
//        Long summaryId = mockSummary.getId();
//
//        // 2. 模拟员工薪资档案快照
//        SalaryArchiveVO mockArchive = new SalaryArchiveVO();
//        mockArchive.setId(100L);
//        mockArchive.setEmployeeId(888L);
//        mockArchive.setCurrency("CNY");
//        // 底薪在 Engine 里已经被我们强行 mock 成了 5840，这里配不配无所谓
//
//        // 3. 模拟当月薪资周期/考勤数据
//        SalaryPeriod mockPeriod = new SalaryPeriod();
//        mockPeriod.setId(999L);
//        mockPeriod.setEmployeeId(888L);
//        mockPeriod.setSettlementMonth("202603");
//        mockPeriod.setCurrency("CNY");
//
//        try {
//            // 🚀 4. 轰鸣吧引擎！执行核心计算逻辑
//            salaryCoreEngine.createRecordByCalculation(summaryId, mockArchive, mockPeriod);
//
//            // 5. 从数据库查出刚刚算好的汇总单，验证结果
//            SalarySummary resultSummary = salarySummaryService.getById(summaryId);
//
//            log.info("====== 🎯 计算完成！以下是引擎生成的最终账单大盘 ======");
//            log.info("应发总计 (Gross): {}", resultSummary.getGrossSalary());
//            log.info("实发总计 (Net): {}", resultSummary.getNetSalary());
//
//            log.info("====== 📦 封存的 JSON 快照 (准备发往前端工资条) ======");
//            // 打印出美化后的 JSON 字符串
//            String prettyJson = JSON.toJSONString(resultSummary.getDetailJson(), JSONWriter.Feature.PrettyFormat);
//            System.out.println(prettyJson);
//
//        } finally {
//            // 清理测试脏数据
//            salarySummaryService.removeById(summaryId);
//        }
//    }
//}