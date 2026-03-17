package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.paymentrecord.PaymentRecordQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryPaymentRecord;
import com.salary.admin.model.vo.salary.paymentrecord.SalaryPaymentRecordVO;
import com.salary.admin.service.salary.ISalaryPaymentRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 薪资结算明细记录管理
 * 提供核算后的员工薪资明细查询、手动调整及异常记录删除功能
 *
 * @author system
 * @since 2026-03-15
 */
@Tag(name = "薪资明细记录")
@RestController
@RequestMapping("/api/salary/payment-record")
public class SalaryPaymentRecordController {

    @Resource
    private ISalaryPaymentRecordService paymentRecordService;

    @Operation(summary = "分页查询薪资结算明细", description = "支持按汇总批次过滤，联表返回员工基本信息及计算详情JSON")
    @PostMapping("/page")
    @Loggable(title = "薪资明细记录-分页查询薪资结算明细")
    public ApiResult<PageResult<SalaryPaymentRecordVO>> page(@RequestBody PaymentRecordQueryReqDTO queryReq) {
        return ApiResult.successResult(paymentRecordService.selectRecordPage(queryReq));
    }

    @Operation(summary = "获取单条结算记录详情")
    @GetMapping("/{id}")
    @Loggable(title = "薪资明细记录-获取单条结算记录详情")
    public ApiResult<SalaryPaymentRecord> getById(@PathVariable Long id) {
        return ApiResult.successResult(paymentRecordService.getById(id));
    }

    @Operation(summary = "手动调整结算记录", description = "用于核算后对个别员工金额进行微调，修改后会自动触发汇总表金额重算")
    @PutMapping("/update")
    @Loggable(title = "薪资明细记录-手动调整结算记录")
    public ApiResult<Boolean> update(@RequestBody SalaryPaymentRecord record) {
        return ApiResult.successResult(paymentRecordService.updateRecord(record));
    }

    @Operation(summary = "删除单条结算记录", description = "删除后关联的汇总表(Summary)金额会自动递减")
    @DeleteMapping("/{id}")
    @Loggable(title = "薪资明细记录-删除单条结算记录")
    public ApiResult<Boolean> delete(@PathVariable Long id) {
        return ApiResult.successResult(paymentRecordService.removeRecord(id));
    }

    @Operation(summary = "批量删除结算记录")
    @DeleteMapping("/batch")
    @Loggable(title = "薪资明细记录-批量删除结算记录")
    public ApiResult<Boolean> batchDelete(@RequestBody List<Long> ids) {
        return ApiResult.successResult(paymentRecordService.batchRemoveRecords(ids));
    }
}
