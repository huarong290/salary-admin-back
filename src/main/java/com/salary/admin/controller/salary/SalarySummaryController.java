package com.salary.admin.controller.salary;

import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.summary.SalarySummaryOperateDTO;
import com.salary.admin.model.dto.salary.summary.SummaryQueryReqDTO;
import com.salary.admin.model.vo.salary.summary.SalarySummaryVO;
import com.salary.admin.service.salary.ISalarySummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 薪资汇总与结算表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Tag(name = "薪资汇总管理")
@RestController
@RequestMapping("/api/salary/summary")
@Slf4j
@RequiredArgsConstructor
public class SalarySummaryController {

    private final ISalarySummaryService summaryService;

    @PostMapping("/page")
    @Operation(summary = "分页查询薪资汇总数据", description = "支持按月份、姓名、工号、计算/支付状态筛选")
    public ApiResult<PageResult<SalarySummaryVO>> getPage(@RequestBody SummaryQueryReqDTO reqDTO) {
        return ApiResult.successResult(summaryService.getSummaryPage(reqDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取薪资单详情", description = "返回包含 detail_json 解析后的结构化明细数据")
    public ApiResult<SalarySummaryVO> getById(@PathVariable Long id) {
        return ApiResult.successResult(summaryService.getSummaryDetail(id));
    }

    /**
     * 统一锁定状态变更接口
     * 替代了原有的 toggleLock, batchLock, batchUnlock
     */
    @PostMapping("/batchLockStatus")
    @Operation(summary = "批量变更锁定状态", description = "统一处理锁定(1)与解锁(0)逻辑，支持单条或多条操作")
    public ApiResult<Boolean> changeLockStatus(@Validated @RequestBody SalarySummaryOperateDTO operateDTO) {
        return ApiResult.successResult(summaryService.updateLockStatus(operateDTO));
    }
}
