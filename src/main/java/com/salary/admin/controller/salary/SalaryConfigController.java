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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 薪资系统全局配置表 前端控制器
 * </p>
 *
 * 提供：分页查询、新增、修改、全量启用列表、按 Key 取值
 * 应用场景：本位币、汇率基准、个税起征点等全局业务参数的集中管理
 *
 * @author system
 * @since 2026-03-27
 */
@RestController
@RequestMapping("/api/salary/config")
@Tag(name = "薪资全局配置", description = "薪资系统全局业务参数集中管理")
@Slf4j
@RequiredArgsConstructor
public class SalaryConfigController {

    private final ISalaryConfigService configService;

    @PostMapping("/page")
    @Operation(summary = "分页查询薪资全局配置", description = "支持按配置名/配置键模糊、状态筛选")
    public ApiResult<PageResult<SalaryConfigVO>> getPage(@RequestBody SalaryConfigQueryReqDTO reqDTO) {
        return ApiResult.successResult(configService.pageQuery(reqDTO));
    }

    @PostMapping("/add")
    @Operation(summary = "新增全局配置项", description = "配置键全局唯一")
    @Loggable(title = "薪资全局配置-新增", logRequest = true)
    public ApiResult<Long> add(@Validated @RequestBody SalaryConfigAddReqDTO reqDTO) {
        return ApiResult.successResult(configService.addConfig(reqDTO));
    }

    @PutMapping("/edit")
    @Operation(summary = "修改全局配置项", description = "配置键只读，仅允许修改名称/值/状态/备注")
    @Loggable(title = "薪资全局配置-修改", logRequest = true)
    public ApiResult<Boolean> edit(@Validated @RequestBody SalaryConfigEditReqDTO reqDTO) {
        return ApiResult.successResult(configService.editConfig(reqDTO));
    }

    @GetMapping("/listAll")
    @Operation(summary = "查询全部启用配置", description = "供其他模块初始化加载")
    public ApiResult<List<SalaryConfigVO>> listAll() {
        return ApiResult.successResult(configService.listAllActive());
    }

    @GetMapping("/getVal")
    @Operation(summary = "按配置键获取配置值", description = "未命中或停用返回 null")
    public ApiResult<String> getVal(@RequestParam String configKey) {
        return ApiResult.successResult(configService.getValueByKey(configKey));
    }
}
