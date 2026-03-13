package com.salary.admin.controller.salary;

import com.salary.admin.common.ApiResult;
import com.salary.admin.model.entity.salary.SalaryArchiveItem;
import com.salary.admin.service.salary.ISalaryArchiveItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 薪资档案固定项明细表 前端控制器
 * </p>
 * 注意：明细表不支持直接的增删改操作，所有修改必须通过主表发起调薪版本更迭！
 * @author system
 * @since 2026-03-13
 */
@RestController
@RequestMapping("/salary-archive-item")
@Tag(name = "薪资档案明细管理", description = "仅提供档案明细项的只读查询接口")
public class SalaryArchiveItemController {
    @Autowired
    private  ISalaryArchiveItemService iSalaryArchiveItemService;

    @GetMapping("/list/{archiveId}")
    @Operation(summary = "获取指定档案的明细项列表", description = "根据主表档案ID，获取该版本下配置的所有固定收入/扣款明细项（用于前端列表懒加载展开）")
    public ApiResult<List<SalaryArchiveItem>> listItemsByArchiveId(
            @Parameter(description = "档案主表ID", required = true) @PathVariable("archiveId") Long archiveId) {

        // 调用我们在上一步 Service 中写好的 getItemsByArchiveId 方法
        List<SalaryArchiveItem> items = iSalaryArchiveItemService.getItemsByArchiveId(archiveId);
        return ApiResult.successResult(items);
    }
}
