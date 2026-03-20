package com.salary.admin.controller;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.model.dto.dictitem.DictItemAddReqDTO;
import com.salary.admin.model.dto.dictitem.DictItemUpdateReqDTO;
import com.salary.admin.model.vo.dictitem.DictItemVO;
import com.salary.admin.service.ISysDictItemService;
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
 * 系统字典项表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-20
 */
@RestController
@RequestMapping("/api/dict/item")
@Tag(name = "字典数据管理", description = "具体字典项维护（如：USDT、CNY、在职、离职等）")
@Slf4j
public class SysDictItemController {

    @Autowired
    private ISysDictItemService dictItemService;

    @GetMapping("/list/{typeCode}")
    @Operation(summary = "根据类型编码获取字典项列表", description = "前端下拉框最常用接口，带缓存优化")
    public ApiResult<List<DictItemVO>> getItemsByType(
            @Parameter(description = "字典类型编码", example = "currency_type") @PathVariable String typeCode) {
        return ApiResult.successResult(dictItemService.selectDictItemsByTypeCode(typeCode));
    }

    @PostMapping("/add")
    @Operation(summary = "新增字典项")
    @Loggable(title = "字典管理-新增数据", logRequest = true)
    public ApiResult<Long> addDictItem(@Validated @RequestBody DictItemAddReqDTO reqDTO) {
        return ApiResult.successResult(dictItemService.addDictItem(reqDTO));
    }

    @PutMapping("/edit")
    @Operation(summary = "修改字典项")
    @Loggable(title = "字典管理-修改数据", logRequest = true)
    public ApiResult<Boolean> editDictItem(@Validated @RequestBody DictItemUpdateReqDTO reqDTO) {
        return ApiResult.successResult(dictItemService.editDictItem(reqDTO));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除字典项")
    @Loggable(title = "字典管理-删除数据")
    public ApiResult<Boolean> deleteDictItem(@PathVariable Long id) {
        return ApiResult.successResult(dictItemService.deleteDictItemById(id));
    }

    @DeleteMapping("/batch-delete")
    @Operation(summary = "批量删除字典项")
    @Loggable(title = "字典管理-批量删除")
    public ApiResult<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        return ApiResult.successResult(dictItemService.deleteDictItemByIds(ids));
    }
}