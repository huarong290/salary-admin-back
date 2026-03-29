package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.itemconfig.ItemConfigAddReqDTO;
import com.salary.admin.model.dto.salary.itemconfig.ItemConfigEditReqDTO;
import com.salary.admin.model.dto.salary.itemconfig.ItemConfigQueryReqDTO;
import com.salary.admin.model.vo.salary.itemconfig.ItemConfigOptionVO;
import com.salary.admin.model.vo.salary.itemconfig.SalaryItemConfigVO;
import com.salary.admin.service.salary.ISalaryItemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 薪资项目统一配置 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@RestController
@RequestMapping("/api/salary/item-config")
@Tag(name = "薪资项目配置", description = "维护发薪项元数据、计算优先级及脚本变量映射")
@Slf4j
@RequiredArgsConstructor // 架构师推荐：使用构造器注入代替 @Autowired
public class SalaryItemConfigController {

    private final ISalaryItemConfigService itemConfigService;

    @PostMapping("/page")
    @Operation(summary = "分页查询薪资项目列表", description = "支持按名称、编码、分类多条件搜索，并关联字典名称")
    public ApiResult<PageResult<SalaryItemConfigVO>> getPage(@RequestBody ItemConfigQueryReqDTO reqDTO) {
        return ApiResult.successResult(itemConfigService.selectItemConfigPage(reqDTO));
    }

    @PostMapping("/add")
    @Operation(summary = "新增薪资项目")
    @Loggable(title = "薪资配置-新增项目", logRequest = true)
    public ApiResult<Long> addConfig(@Validated @RequestBody ItemConfigAddReqDTO reqDTO) {
        return ApiResult.successResult(itemConfigService.addItemConfig(reqDTO));
    }

    @PutMapping("/edit")
    @Operation(summary = "修改薪资项目", description = "系统固定项禁止修改关键变量名")
    @Loggable(title = "薪资配置-修改项目", logRequest = true)
    public ApiResult<Boolean> editConfig(@Validated @RequestBody ItemConfigEditReqDTO reqDTO) {
        return ApiResult.successResult(itemConfigService.editItemConfig(reqDTO));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除薪资项目", description = "默认执行逻辑删除，防止历史工资数据引用断裂")
    @Loggable(title = "薪资配置-删除项目")
    public ApiResult<Boolean> deleteConfig(
            @Parameter(description = "配置项ID", example = "1") @PathVariable Long id,
            @Parameter(description = "是否彻底物理删除", example = "false") @RequestParam(defaultValue = "true") boolean logicalDelete) {
        return ApiResult.successResult(itemConfigService.deleteItemConfig(id, logicalDelete));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "获取项目详细配置")
    public ApiResult<SalaryItemConfigVO> getDetail(@PathVariable Long id) {
        // 直接使用 MyBatis-Plus 默认提供的 getById，再手动转为 VO，或在 Service 补全逻辑
        return ApiResult.successResult(itemConfigService.getItemConfigDetail(id));
    }

    @GetMapping("/listOptions")
    @Operation(summary = "获取薪资项目下拉列表", description = "用于前端业务页面的动态级联选择")
    public ApiResult<List<ItemConfigOptionVO>> listOptions() {
        return ApiResult.successResult(itemConfigService.listOptions());
    }
    @PostMapping("/refresh-cache")
    @Operation(summary = "手动清理计算引擎缓存", description = "当手动调整数据库脚本后使用")
    @Loggable(title = "薪资配置-刷新缓存")
    public ApiResult<Boolean> refreshCache() {
        itemConfigService.clearCache();
        return ApiResult.successResult(true);
    }
}
