package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.config.SalaryConfigAddReqDTO;
import com.salary.admin.model.dto.salary.config.SalaryConfigEditReqDTO;
import com.salary.admin.model.dto.salary.config.SalaryConfigQueryReqDTO;
import com.salary.admin.model.vo.salary.config.SalaryConfigVO;
import com.salary.admin.service.salary.ISalaryConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 薪资系统全局配置表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-20
 */
@RestController
@RequestMapping("/api/salary/config")
@Tag(name = "薪资系统配置", description = "全局结算币种、计薪精度等核心参数控制")
@Slf4j
public class SalaryConfigController {

    @Autowired
    private ISalaryConfigService salaryConfigService;

    @PostMapping("/add")
    @Operation(summary = "新增配置项", description = "用于扩展新的全局系统参数")
    @Loggable(title = "薪资配置-新增项", logRequest = true)
    public ApiResult<Long> addConfig(@Validated @RequestBody SalaryConfigAddReqDTO reqDTO) { // 🌟 修正 DTO
        return ApiResult.successResult(salaryConfigService.addConfig(reqDTO));
    }

    @GetMapping("/listAll")
    @Operation(summary = "获取全量薪资配置列表")
    public ApiResult<List<SalaryConfigVO>> listAllConfigs() {
        return ApiResult.successResult(salaryConfigService.selectAllConfigs());
    }

    @GetMapping("/getVal")
    @Operation(summary = "根据Key获取配置值", description = "例如：SETTLEMENT_CURRENCY")
    public ApiResult<String> getConfigValue(@Parameter(description = "配置键") @RequestParam String configKey) {
        return ApiResult.successResult(salaryConfigService.getConfigValue(configKey));
    }

    @PutMapping("/edit")
    @Operation(summary = "更新配置项", description = "更新配置后会自动刷新 Redis 缓存")
    @Loggable(title = "薪资配置-更新项", logRequest = true)
    public ApiResult<Boolean> updateConfig(@Validated @RequestBody SalaryConfigEditReqDTO reqDTO) { // 🌟 修正 DTO
        return ApiResult.successResult(salaryConfigService.updateConfig(reqDTO));
    }

    @GetMapping("/currentCurrency")
    @Operation(summary = "获取当前系统结算币种", description = "快捷接口，返回如: USDT")
    @Loggable(title = "薪资配置-获取当前系统结算币种")
    public ApiResult<String> getCurrentCurrency() {
        return ApiResult.successResult(salaryConfigService.getCurrentSettlementCurrency());
    }


    @PostMapping("/page")
    @Operation(summary = "分页查询配置列表", description = "支持按名称、Key、状态进行组合筛选")
    @Loggable(title = "薪资配置-分页查询")
    public ApiResult<PageResult<SalaryConfigVO>> getConfigPage(@RequestBody SalaryConfigQueryReqDTO reqDTO) {
        return ApiResult.successResult(salaryConfigService.selectConfigByPage(reqDTO));
    }

}