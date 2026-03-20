package com.salary.admin.controller;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.dicttype.DictTypeAddReqDTO;
import com.salary.admin.model.dto.dicttype.DictTypeQueryReqDTO;
import com.salary.admin.model.dto.dicttype.DictTypeUpdateReqDTO;
import com.salary.admin.model.vo.dicttype.DictTypeVO;
import com.salary.admin.service.ISysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统字典类型表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-20
 */
@RestController
@RequestMapping("/api/dict/type")
@Tag(name = "字典类型管理", description = "系统字典分类定义（如：币种类型、员工状态等）")
@Slf4j
public class SysDictTypeController {

    @Autowired
    private ISysDictTypeService dictTypeService;

    @PostMapping("/page")
    @Operation(summary = "分页查询字典类型列表")
    @Loggable(title = "字典管理-查询类型分页")
    public ApiResult<PageResult<DictTypeVO>> getDictTypePage(@RequestBody DictTypeQueryReqDTO reqDTO) {
        return ApiResult.successResult(dictTypeService.selectDictTypePage(reqDTO));
    }

    @PostMapping("/add")
    @Operation(summary = "新增字典类型")
    @Loggable(title = "字典管理-新增类型", logRequest = true)
    public ApiResult<Long> addDictType(@Validated @RequestBody DictTypeAddReqDTO reqDTO) {
        return ApiResult.successResult(dictTypeService.addDictType(reqDTO));
    }

    @PutMapping("/edit")
    @Operation(summary = "修改字典类型")
    @Loggable(title = "字典管理-修改类型", logRequest = true)
    public ApiResult<Boolean> editDictType(@Validated @RequestBody DictTypeUpdateReqDTO reqDTO) {
        return ApiResult.successResult(dictTypeService.editDictType(reqDTO));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除字典类型")
    @Loggable(title = "字典管理-删除类型")
    public ApiResult<Boolean> deleteDictType(@Parameter(description = "主键ID", required = true) @PathVariable Long id) {
        return ApiResult.successResult(dictTypeService.deleteDictTypeById(id));
    }
}
