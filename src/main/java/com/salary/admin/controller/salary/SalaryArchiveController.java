package com.salary.admin.controller.salary;

import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.archive.ArchiveAddReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveAuditDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveQueryReqDTO;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.service.salary.ISalaryArchiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
/**
 * <p>
 * 员工薪资标准配置表(档案含版本历史) 控制器
 * </p>
 *
 * @author system
 * @since 2026-03-13
 */
@RestController
@RequestMapping("/api/salary/archive")
@RequiredArgsConstructor
@Tag(name = "薪资档案管理", description = "员工定薪、调薪及薪资版本控制接口")
public class SalaryArchiveController {

    private final ISalaryArchiveService salaryArchiveService;

    @PostMapping("/page")
    @Operation(summary = "分页查询薪资档案列表", description = "支持关键字搜索员工姓名和编号，默认查询当前最新版本")
    @Loggable(title = "薪资档案管理-分页查询薪资档案列表")
    public ApiResult<PageResult<SalaryArchiveVO>> pageQueryArchive(@RequestBody ArchiveQueryReqDTO queryReq) {
        PageResult<SalaryArchiveVO> pageResult = salaryArchiveService.selectArchivePage(queryReq);
        return ApiResult.successResult(pageResult);
    }

    @PostMapping("/save")
    @Operation(summary = "提交定薪/调薪方案", description = "为员工定薪或发起调薪。系统会自动闭合旧版本并生成待审核的新版本记录。")
    @Loggable(title = "薪资档案管理-提交定薪/调薪方案")
    public ApiResult<Boolean> createNewSalaryVersion(@Valid @RequestBody ArchiveAddReqDTO saveReq) {
        boolean success = salaryArchiveService.createNewSalaryVersion(saveReq);
        return ApiResult.successResult(success);
    }

    @GetMapping("/current/{employeeId:\\d+}")
    @Operation(summary = "获取当前生效档案", description = "根据员工ID获取当前正在生效的薪资档案详情及配置明细")
    @Loggable(title = "薪资档案管理-获取当前生效档案")
    public ApiResult<SalaryArchiveVO> getCurrentArchive(
            @Parameter(description = "员工ID", required = true) @PathVariable("employeeId") Long employeeId) {
        SalaryArchiveVO vo = salaryArchiveService.getCurrentArchive(employeeId);
        if (vo == null) {
            // 这样前端会进入 catch 块并显示“暂无档案”，而不是报错 null 引用
            return ApiResult.failResult("未找到该员工生效的薪资档案，请先进行入职定薪");
        }
        return ApiResult.successResult(vo);
    }

    @GetMapping("/detail/{archiveId:\\d+}")
    @Operation(summary = "获取特定版本详情", description = "根据档案ID(主键)查询历史某一次调薪的完整详情")
    @Loggable(title = "薪资档案管理-获取特定版本详情")
    public ApiResult<SalaryArchiveVO> getArchiveDetail(
            @Parameter(description = "档案ID", required = true) @PathVariable("archiveId") Long archiveId) {
        SalaryArchiveVO vo = salaryArchiveService.getArchiveDetail(archiveId);
        return ApiResult.successResult(vo);
    }

    @DeleteMapping("/revoke/{employeeId:\\d+}")
    @Operation(summary = "撤销最新调薪版本", description = "撤销草稿/待审状态的调薪记录，并恢复上一次的有效版本。已生效版本无法撤销。")
    @Loggable(title = "薪资档案管理-撤销最新调薪版本")
    public ApiResult<Boolean> revokeLatestVersion(
            @Parameter(description = "员工ID", required = true) @PathVariable("employeeId") Long employeeId) {
        boolean success = salaryArchiveService.revokeLatestVersion(employeeId);
        return ApiResult.successResult(success);
    }

    @Operation(summary = "审核薪资档案")
    @PostMapping("/audit")
    @Loggable(title = "薪资档案管理-审核薪资档案")
    public ApiResult<Boolean> auditArchive(@RequestBody ArchiveAuditDTO auditDTO) {
        return ApiResult.successResult(salaryArchiveService.auditArchive(auditDTO));
    }
}