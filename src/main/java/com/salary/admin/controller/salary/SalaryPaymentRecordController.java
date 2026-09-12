package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.paymentrecord.PaymentRecordQueryReqDTO;
import com.salary.admin.model.vo.salary.paymentrecord.SalaryPaymentRecordVO;
import com.salary.admin.service.salary.ISalaryPaymentRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 薪资结算明细/流水记录 前端控制器
 * </p>
 *
 * 提供：工资单流水分页查询、单条详情（含快照解析）、财务手工修正实发金额。
 * 数据源：salary_summary 聚合视图（穿透发薪台查看每位员工的核算流水）。
 *
 * @author system
 * @since 2026-03-27
 */
@RestController
@RequestMapping("/api/salary/payment-record")
@Tag(name = "薪资流水记录", description = "工资单流水底稿查询与财务手工修正")
@Slf4j
@RequiredArgsConstructor
public class SalaryPaymentRecordController {

    private final ISalaryPaymentRecordService paymentRecordService;

    @PostMapping("/page")
    @Operation(summary = "分页查询工资单流水", description = "支持按汇总批次、关键字、月份、手工调整筛选")
    public ApiResult<PageResult<SalaryPaymentRecordVO>> getPage(@RequestBody PaymentRecordQueryReqDTO reqDTO) {
        return ApiResult.successResult(paymentRecordService.pageQuery(reqDTO));
    }

    @GetMapping("/getById/{id}")
    @Operation(summary = "获取单条流水详情", description = "包含解析后的明细快照")
    public ApiResult<SalaryPaymentRecordVO> getById(@PathVariable Long id) {
        return ApiResult.successResult(paymentRecordService.getRecordDetail(id));
    }

    @PutMapping("/update")
    @Operation(summary = "财务手工修正实发金额", description = "差额自动写入手工调整字段并追加审计备注")
    @Loggable(title = "薪资流水-手工修正", logRequest = true)
    public ApiResult<Boolean> update(@RequestBody Map<String, Object> body) {
        Long id = body.get("id") != null ? Long.valueOf(String.valueOf(body.get("id"))) : null;
        BigDecimal finalSalary = body.get("finalSalary") != null
                ? new BigDecimal(String.valueOf(body.get("finalSalary")))
                : null;
        String remark = body.get("remark") != null ? String.valueOf(body.get("remark")) : null;
        return ApiResult.successResult(paymentRecordService.updateFinalSalary(id, finalSalary, remark));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除流水记录", description = "逻辑删除对应工资单")
    @Loggable(title = "薪资流水-删除")
    public ApiResult<Boolean> delete(@PathVariable Long id) {
        return ApiResult.successResult(paymentRecordService.deleteRecord(id));
    }
}
