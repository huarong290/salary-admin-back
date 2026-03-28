package com.salary.admin.controller.salary;


import com.salary.admin.annotation.Loggable;
import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.archive.ArchiveAdjustReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveAuditReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveInitReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveQueryReqDTO;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.service.salary.ISalaryArchiveService;
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
 * 员工薪资标准配置表(含版本历史) 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@RestController
@RequestMapping("/api/salary/archive")
@Tag(name = "薪资档案管理", description = "员工入职定薪、调薪申请、审批与历史版本查询接口")
@Slf4j
@RequiredArgsConstructor // 架构师推荐：使用构造器注入代替 @Autowired
public class SalaryArchiveController {

    private final ISalaryArchiveService salaryArchiveService;

    @PostMapping("/init")
    @Operation(summary = "入职定薪 (初始化V1版本)", description = "为新员工创建第一份薪资档案及明细项")
    @Loggable(title = "薪资档案-入职定薪", logRequest = true)
    public ApiResult<Long> initEmployeeArchive(@Validated @RequestBody ArchiveInitReqDTO reqDTO) {
        return ApiResult.successResult(salaryArchiveService.initEmployeeArchive(reqDTO));
    }

    @PostMapping("/adjust")
    @Operation(summary = "调薪申请 (生成草稿版本)", description = "为老员工提交调薪申请，生成 V(n+1) 版本的草稿")
    @Loggable(title = "薪资档案-调薪申请", logRequest = true)
    public ApiResult<Long> adjustSalary(@Validated @RequestBody ArchiveAdjustReqDTO reqDTO) {
        return ApiResult.successResult(salaryArchiveService.adjustSalary(reqDTO));
    }

    @PostMapping("/audit")
    @Operation(summary = "调薪审批处理 (生效/驳回)", description = "审批调薪草稿。若通过，则旧版失效，新版上位。")
    @Loggable(title = "薪资档案-调薪审批", logRequest = true)
    public ApiResult<Boolean> auditArchive(@Validated @RequestBody ArchiveAuditReqDTO reqDTO) {
        return ApiResult.successResult(salaryArchiveService.auditArchive(reqDTO));
    }

    @GetMapping("/latest/{employeeId}")
    @Operation(summary = "获取当前生效档案", description = "获取该员工当前生效的最新薪资档案及其固定项明细（供前端展示或结算引擎调用）")
    public ApiResult<SalaryArchiveVO> getLatestEffectiveArchive(
            @Parameter(description = "员工ID", example = "1") @PathVariable Long employeeId) {
        return ApiResult.successResult(salaryArchiveService.getLatestEffectiveArchive(employeeId));
    }

    @GetMapping("/history/{employeeId}")
    @Operation(summary = "获取历史档案列表", description = "按版本号倒序，获取员工的所有薪资档案调整记录")
    public ApiResult<List<SalaryArchiveVO>> listArchiveHistory(
            @Parameter(description = "员工ID", example = "1") @PathVariable Long employeeId) {
        return ApiResult.successResult(salaryArchiveService.listArchiveHistory(employeeId));
    }
    @PostMapping("/page")
    @Operation(summary = "分页查询薪资档案", description = "支持按员工姓名、工号、审批状态等多条件分页查询")
    // 这里一般不用加 @Loggable，因为查询操作太频繁且不修改敏感数据
    public ApiResult<PageResult<SalaryArchiveVO>> getArchivePage(@RequestBody ArchiveQueryReqDTO reqDTO) {
        return ApiResult.successResult(salaryArchiveService.getArchivePage(reqDTO));
    }
}
