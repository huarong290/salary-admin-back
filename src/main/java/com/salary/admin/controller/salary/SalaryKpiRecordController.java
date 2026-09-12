package com.salary.admin.controller.salary;

import com.salary.admin.common.ApiResult;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.kpi.KpiBatchInitReqDTO;
import com.salary.admin.model.dto.salary.kpi.KpiEvaluateReqDTO;
import com.salary.admin.model.dto.salary.kpi.KpiRecordQueryReqDTO;
import com.salary.admin.model.vo.salary.kpi.SalaryKpiRecordVO;
import com.salary.admin.service.ISalaryKpiRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 员工月度绩效考核记录表 前端控制器
 * </p>
 * * 负责暴露绩效打分生命周期的核心入口：派发、打分、定稿
 *
 * @author system
 * @since 2026-04-04
 */
@Tag(name = "员工月度绩效大盘管理")
@RestController
@RequestMapping("/api/salary/kpi")
@Slf4j
@RequiredArgsConstructor
public class SalaryKpiRecordController {

    private final ISalaryKpiRecordService iSalaryKpiRecordService;

    /**
     * 【生命周期：0. 绩效大盘查询】
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询绩效大盘", description = "支持按月份、状态、员工姓名等多维筛选，自动聚合员工基础信息")
    public ApiResult<PageResult<SalaryKpiRecordVO>> getKpiRecordPage(@Validated @RequestBody KpiRecordQueryReqDTO reqDTO) {
        log.info("接收到绩效大盘分页查询指令，目标月份：{}", reqDTO.getSettlementMonth());
        PageResult<SalaryKpiRecordVO> page = iSalaryKpiRecordService.getKpiRecordPage(reqDTO);
        return ApiResult.successResult(page);
    }

    /**
     * 【生命周期：1. 派发单据】
     */
    @PostMapping("/init")
    @Operation(summary = "批量初始化当月绩效草稿单", description = "依赖考勤周期，为主管一键生成待打分的空白考核单据")
    public ApiResult<Boolean> initMonthlyKpi(@Validated @RequestBody KpiBatchInitReqDTO reqDTO) {
        log.info("接收到批量初始化绩效单指令，目标月份：{}", reqDTO.getSettlementMonth());
        iSalaryKpiRecordService.initMonthlyKpi(reqDTO);
        return ApiResult.successResult(true);
    }

    /**
     * 【生命周期：2. 业务打分】
     */
    @PostMapping("/evaluate")
    @Operation(summary = "提交/修改绩效打分", description = "主管录入评级(S/A/B/C/D)，系统将智能拦截并换算为薪资计算引擎所需的浮点系数")
    public ApiResult<Boolean> evaluateKpi(@Validated @RequestBody KpiEvaluateReqDTO reqDTO) {
        log.info("接收到绩效打分指令，单据ID：{}，评级：{}", reqDTO.getId(), reqDTO.getKpiGrade());
        iSalaryKpiRecordService.evaluateKpi(reqDTO);
        return ApiResult.successResult(true);
    }

    /**
     * 【生命周期：3. 审核定稿】
     */
    @PostMapping("/confirm")
    @Operation(summary = "批量定稿绩效单", description = "确认后的绩效数据将被彻底锁定，作为合法数据源供发薪引擎抓取算钱")
    public ApiResult<Boolean> confirmKpi(@RequestBody List<Long> ids) {
        log.info("接收到批量定稿绩效单指令，待定稿数量：{}", ids != null ? ids.size() : 0);
        iSalaryKpiRecordService.confirmKpi(ids);
        return ApiResult.successResult(true);
    }

    /**
     * 【生命周期：3.5 撤回重审】撤回已定稿的绩效单, 允许重新打分后再次定稿
     */
    @PostMapping("/revoke")
    @Operation(summary = "撤回已定稿绩效单", description = "状态回退为打分中(不再参与算薪)，可修改打分后重新定稿")
    public ApiResult<Boolean> revokeKpi(@RequestBody List<Long> ids) {
        log.info("接收到撤回定稿指令，待撤回数量：{}", ids != null ? ids.size() : 0);
        iSalaryKpiRecordService.revokeKpi(ids);
        return ApiResult.successResult(true);
    }
}