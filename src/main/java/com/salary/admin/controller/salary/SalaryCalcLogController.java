package com.salary.admin.controller.salary;

import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.calclog.CalcLogQueryReqDTO;
import com.salary.admin.model.vo.calclog.CalcLogVO;
import com.salary.admin.service.salary.ISalaryCalcLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 薪资计算日志表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Tag(name = "薪资核算日志", description = "查看引擎计算过程流水")
@RestController
@RequestMapping("/api/salary/calc-log")
@RequiredArgsConstructor
public class SalaryCalcLogController {

    private final ISalaryCalcLogService logService;

    @Operation(summary = "分页查询计算流水")
    @PostMapping("/page")
    public ApiResult<PageResult<CalcLogVO>> getPage(@RequestBody CalcLogQueryReqDTO reqDTO) {
        return ApiResult.successResult(logService.selectLogPage(reqDTO));
    }

    @Operation(summary = "根据员工和周期获取完整计算链", description = "用于工资单详情页展示计算逻辑")
    @GetMapping("/trace")
    public ApiResult<List<CalcLogVO>> trace(@RequestParam Long employeeId, @RequestParam Long periodId) {
        return ApiResult.successResult(logService.listLogsByEmployee(employeeId, periodId));
    }
}