package com.salary.admin.service.impl.salary;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.convert.salary.calccontext.CalcContextConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryCalcContextExtMapper;
import com.salary.admin.model.dto.salary.calccontext.CalcContextAddReqDTO;
import com.salary.admin.model.dto.salary.calccontext.CalcContextEditReqDTO;
import com.salary.admin.model.dto.salary.calccontext.CalcContextQueryReqDTO;
import com.salary.admin.model.dto.salary.snapshot.ArchiveSnapshot;
import com.salary.admin.model.entity.salary.*;
import com.salary.admin.model.vo.calccontext.CalcContextVO;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.service.ISalaryAdjustmentService;
import com.salary.admin.service.ISalaryKpiRecordService;
import com.salary.admin.service.salary.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 薪资计算上下文快照表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryCalcContextServiceImpl extends ServiceImpl<SalaryCalcContextExtMapper, SalaryCalcContext> implements ISalaryCalcContextService {

    private final SalaryCalcContextExtMapper salaryCalcContextExtMapper;

    private final CalcContextConvert calcContextConvert;

    private final ISalaryEmployeeService iSalaryEmployeeService;

    private final ISalaryArchiveService iSalaryArchiveService;

    private final ISalaryPeriodService iSalaryPeriodService;

    private final ISalaryKpiRecordService iSalaryKpiRecordService;

    // 注入薪资配置字典服务
    private final ISalaryItemConfigService iSalaryItemConfigService;
    // 注入调账服务
    private final ISalaryAdjustmentService iSalaryAdjustmentService;
    // ======================== 1. 新增操作 (Create) ========================
    @Override
    public Long addContext(CalcContextAddReqDTO reqDTO) {
        SalaryCalcContext entity = calcContextConvert.toEntity(reqDTO);
        this.save(entity);
        log.info("已生成计算快照，ID: {}, 员工ID: {}, 周期ID: {}", entity.getId(), entity.getEmployeeId(), entity.getPeriodId());
        return entity.getId();
    }

    @Override
    public Map<String, Object> buildEmployeeContext(Long periodId, Long employeeId, String pipelineCode, Integer pipelineVersion, Long specifyArchiveId) {
        log.debug("开始组装薪资核算上下文 Env | 周期: {}, 员工: {}", periodId, employeeId);
        Map<String, Object> env = new HashMap<>();

        // ==========================================
        // 1. 获取周期信息 (提前获取，用于时间切片)
        // ==========================================
        SalaryPeriod period = iSalaryPeriodService.getById(periodId);
        if (period != null) {
            env.put("monthDays", period.getMonthDays() != null ? period.getMonthDays() : BigDecimal.ZERO);
            env.put("standardRestDays", period.getStandardRestDays());
            env.put("attendanceDays", period.getAttendanceDays() != null ? period.getAttendanceDays() : BigDecimal.ZERO);
            env.put("officeDays", period.getOfficeDays() != null ? period.getOfficeDays() : BigDecimal.ZERO);
            env.put("wfhDays", period.getWfhDays() != null ? period.getWfhDays() : BigDecimal.ZERO);
            env.put("isFullAttendance", period.getFullAttendanceFlag() != null && period.getFullAttendanceFlag() == 1);

            // 将欠勤天数注入引擎，供 ABSENT_DEDUCTION 规则读取
            env.put("unpaidLeaveDays", period.getUnpaidLeaveDays() != null ? period.getUnpaidLeaveDays() : BigDecimal.ZERO);

            // 把带薪假注入引擎！
            env.put("paidLeaveDays", period.getPaidLeaveDays() != null ? period.getPaidLeaveDays() : BigDecimal.ZERO);
        } else {
            throw new BusinessException("上下文中找不到对应的薪资周期: " + periodId);
        }

        // ==========================================
        // 2. 获取员工基础信息
        // ==========================================
        SalaryEmployee employee = iSalaryEmployeeService.getById(employeeId);
        if (employee == null) {
            throw new BusinessException("上下文中找不到对应的员工信息: " + employeeId);
        }
        env.put("employeeId", employee.getId());
        env.put("accommodationStatus", employee.getAccommodationStatus());

        // 状态时间旅行 (Time Travel)
        Integer actualStatus = employee.getEmploymentStatus(); // 获取此刻的真实状态

        // 假设 1 是正式，2 是试用期
        if (actualStatus == 1 && employee.getProbationEndDate() != null) {
            // 如果员工当前已转正，但他的【试用期结束日期】晚于【当前计薪周期的最后一天】
            // 说明在算这个月工资的时候，他实际上完全处于试用期！
            if (period.getEndDate().isBefore(employee.getProbationEndDate())) {
                actualStatus = 2; // 🛡️ 强制降级环境变更为试用期
                log.info("🕒 触发状态回溯: 员工[{}]当前已转正，但在计薪周期[{}]内仍为试用期",
                        employee.getEmployeeName(), period.getSettlementMonth());
            }
        }

        // 将修正后的“期内真实状态”注入引擎
        env.put("employmentStatus", actualStatus);
        // ==========================================
        // 3. 获取档案时间切片快照 (解决 UI 溯源和分段计薪问题)
        // ==========================================
        List<SalaryArchive> activeArchives = iSalaryArchiveService.lambdaQuery()
                .eq(SalaryArchive::getEmployeeId, employeeId)
                .eq(SalaryArchive::getAuditStatus, 1) // 必须是已生效的状态
                .le(SalaryArchive::getEffectiveDate, period.getEndDate()) // 生效日 <= 周期结束日
                .ge(SalaryArchive::getExpiryDate, period.getStartDate())  // 失效日 >= 周期开始日
                .orderByAsc(SalaryArchive::getEffectiveDate)
                .list();

        // 转化为轻量级溯源快照
        List<ArchiveSnapshot> archiveSnapshots = activeArchives.stream().map(arch -> {
            ArchiveSnapshot snap = new ArchiveSnapshot();
            snap.setArchiveId(arch.getId());
            snap.setVersion(arch.getVersion());
            snap.setEffectiveDate(arch.getEffectiveDate());
            snap.setExpiryDate(arch.getExpiryDate());
            return snap;
        }).collect(Collectors.toList());

        // 注入环境，供 CoreEngine 和 前端展示使用
        env.put("_usedArchives", archiveSnapshots);

        // ==========================================
        // 4. 获取薪资档案明细信息 (以最新生效版本作为基础托底)
        // ==========================================
        SalaryArchiveVO archive;
        if (specifyArchiveId != null) {
            // 【时间旅行模式】：精准提取 HR 指定的历史版本档案详情 (底层会把 archiveItems 也查出来)
            archive = iSalaryArchiveService.getArchiveDetail(specifyArchiveId);
            log.info("🕒 触发追溯核算：强制使用指定历史薪资档案 ID: {}", specifyArchiveId);
        } else {
            // 【默认模式】：取该员工当前最新生效的托底档案
            archive = iSalaryArchiveService.getLatestEffectiveArchive(employeeId);
            // ====================================================================
            // >>>>>>> 🚀 【核心修复变动部分：开始】 <<<<<<<
            // 原因：getLatestEffectiveArchive 内部仅查询了档案主表，未联动或二次查询明细子表。
            //       导致在预览（specifyArchiveId 为 null）时，拿到的是缺失 archiveItems 的空壳 VO。
            // 修复方案：在此处进行高可用防御性装配，若明细为空，则通过主表 ID 重新回补完整的明细项。
            if (archive != null && CollectionUtils.isEmpty(archive.getArchiveItems())) {
                log.warn("⚠️ 发现预览断层：getLatestEffectiveArchive 未携带明细数据，触发手动二次装配。档案主键 ID: {}", archive.getId());
                // 调用已确信可以查出 items 明细的详情接口进行覆盖填充
                SalaryArchiveVO fullDetail = iSalaryArchiveService.getArchiveDetail(archive.getId());
                if (fullDetail != null) {
                    archive.setArchiveItems(fullDetail.getArchiveItems());
                }
            }
        }

        if (archive == null) {
            throw new BusinessException("员工 [" + employee.getEmployeeName() + "] 缺失有效的薪资档案，请先定薪！");
        }

        env.put("baseSalary", archive.getBaseSalary() != null ? archive.getBaseSalary() : BigDecimal.ZERO);
        env.put("taxRuleCode", archive.getTaxRuleCode());

        // 通过 itemConfigId 批量查出真实的 envVarName 注入 Aviator 引擎！
        if (!CollectionUtils.isEmpty(archive.getArchiveItems())) {
            // 提取所有的 itemConfigId
            Set<Long> configIds = archive.getArchiveItems().stream()
                    .map(item -> item.getItemConfigId()) // ⚠️ 确保你的 SalaryArchiveItemVO 里有 itemConfigId 字段
                    .collect(Collectors.toSet());

            if (!CollectionUtils.isEmpty(configIds)) {
                // 批量查询配置字典实体
                Map<Long, SalaryItemConfig> configMap = iSalaryItemConfigService.listByIds(configIds).stream()
                        .collect(Collectors.toMap(SalaryItemConfig::getId, c -> c));

                // 遍历员工的档案明细，将金额与引擎能识别的变量名绑定
                archive.getArchiveItems().forEach(item -> {
                    SalaryItemConfig config = configMap.get(item.getItemConfigId());
                    // 必须使用 envVarName (例如 "housingAllowance") 作为键！
                    if (config != null && StringUtils.isNotBlank(config.getEnvVarName())) {
                        // 1. 注入基准标准金额（可以是月总额，也可以是日单价）
                        env.put(config.getEnvVarName(), item.getAmount());
                        //  核心新增：动态注入该薪资项的计算模式，变量名规则为：环境变量名 + "_calcMode"
                        // 例如：housingAllowance_calcMode 或 mealAllowance_calcMode
                        env.put(config.getEnvVarName() + "_calcMode", item.getCalcMode());
                        log.debug("🔧 薪资档案项注入上下文: {} = {}, {}_calcMode = {}",
                                config.getEnvVarName(), item.getAmount(), config.getEnvVarName(), item.getCalcMode());
                    }

                });
            }
        }
// ==========================================
// 5. 注入手工账变动数据（水电费、罚款等）
// 🌟 修正：通过 ItemConfig 转换，确保注入的是小驼峰变量名
// ==========================================
        List<SalaryAdjustment> adjustments = iSalaryAdjustmentService.lambdaQuery()
                .eq(SalaryAdjustment::getPeriodId, periodId)
                .eq(SalaryAdjustment::getEmployeeId, employeeId)
                .eq(SalaryAdjustment::getStatus, 1)
                .list();

        if (!CollectionUtils.isEmpty(adjustments)) {
            // 1. 提取本次涉及的所有 itemCode
            Set<String> itemCodes = adjustments.stream()
                    .map(SalaryAdjustment::getItemCode)
                    .collect(Collectors.toSet());

            // 2. 批量获取配置字典，建立 Code -> EnvVarName 的映射
            Map<String, String> codeToVarMap = iSalaryItemConfigService.lambdaQuery()
                    .in(SalaryItemConfig::getItemCode, itemCodes)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(SalaryItemConfig::getItemCode, SalaryItemConfig::getEnvVarName));

            // 3. 按照标准注入 env
            adjustments.forEach(adj -> {
                String varName = codeToVarMap.get(adj.getItemCode());
                // 🌟🌟🌟 核心修复：必须根据 adjustType 赋予正负号 🌟🌟🌟
                BigDecimal finalAmount = adj.getSettlementAmount();
                if (adj.getAdjustType() != null && adj.getAdjustType() == 2) {
                    // 如果是扣减(2)，强制转为负数，防止数据库正数穿透
                    finalAmount = finalAmount.abs().negate();
                } else {
                    // 如果是增加(1)，强制确保是正数
                    finalAmount = finalAmount.abs();
                }
                if (StringUtils.isNotBlank(varName)) {
                    // 注入带正确符号的金额 (如：-40)
                    env.put(varName, finalAmount);
                    log.debug("手工账注入成功: 变量名={}, 金额={}", varName, finalAmount);
                } else {
                    // 兜底逻辑：如果没配 envVarName，报警但注入 Code 防止计算报错
                    env.put(adj.getItemCode(), finalAmount);
                    log.warn("⚠️ 薪资项目 [{}] 未配置环境变量名，已降级使用 ItemCode 注入", finalAmount);
                }
            });
        }
        // ==========================================
        // 6. 跨模块获取动态业务数据 (考勤、绩效)
        // ==========================================
        SalaryKpiRecord kpi = iSalaryKpiRecordService.lambdaQuery()
                .eq(SalaryKpiRecord::getPeriodId, periodId)
                .eq(SalaryKpiRecord::getEmployeeId, employeeId)
                .eq(SalaryKpiRecord::getAuditStatus, 1) // 必须是已定稿可算薪的状态
                .eq(SalaryKpiRecord::getEffectiveFlag, 1)
                .one();

        if (kpi != null) {
            env.put("kpiGrade", kpi.getKpiGrade());
            env.put("kpiScore", kpi.getKpiScore() != null ? kpi.getKpiScore() : BigDecimal.ZERO);
            env.put("kpiCoefficient", kpi.getKpiCoefficient() != null ? kpi.getKpiCoefficient() : BigDecimal.ZERO);
        } else {
            env.put("kpiGrade", "WAITING");
            env.put("kpiScore", BigDecimal.ZERO);
            env.put("kpiCoefficient", BigDecimal.ZERO);
        }

        // ==========================================
        // 6. 落盘上下文计算快照，用于发薪审计和追溯
        // ==========================================
        this.saveAuditSnapshot(periodId, employeeId, archive.getId(), env, pipelineCode, pipelineVersion);

        log.debug("组装完成，当前员工计算环境变量: {}", env);
        return env;
    }

    // ======================== 2. 删除操作 (Delete) ========================
    @Override
    public boolean deleteContextById(Long id, boolean logicalDelete) {

        // 💡 路由分发：逻辑删除 vs 物理删除
        if (logicalDelete) {
            // 魔法发生的地方：
            // 只要实体类有 @TableLogic，下面这行代码就不会执行 DELETE FROM，
            // 而是自动被 MyBatis-Plus 替换成：UPDATE XX SET delete_flag = 1 WHERE id = ?
            // 💡 直接使用 Mapper 的 deleteById 触发 @TableLogic
            return salaryCalcContextExtMapper.deleteById(id) > 0;
        } else {
            return salaryCalcContextExtMapper.physicalDeleteById(id) > 0;
        }
    }

    @Override
    public boolean deleteContextByIds(List<Long> ids, boolean logicalDelete) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        // 💡 路由分发：批量逻辑删除 vs 批量物理删除
        if (logicalDelete) {
            // 💡 重点魔法：因为我们在 BaseEntity 的 deleteFlag 字段上加了 @TableLogic 注解
            // 所以底层执行的不是 DELETE FROM，而是 UPDATE sys_user SET delete_flag = 1 WHERE id IN (...)
            return salaryCalcContextExtMapper.deleteByIds(ids) > 0;
        } else {
            return salaryCalcContextExtMapper.physicalDeleteByIds(ids) > 0;
        }
    }

    // ======================== 3. 修改操作 (Update) ========================
    @Override
    public boolean editContext(CalcContextEditReqDTO reqDTO) {
        // 薪资计算上下文属于审计快照数据，原则上禁止修改
        throw new BusinessException("薪资计算快照受合规性保护，生成后禁止手动修改");
    }

    // ======================== 4. 查询操作 (Read) ========================
    @Override
    public List<CalcContextVO> listContext(CalcContextQueryReqDTO reqDTO) {
        LambdaQueryWrapper<SalaryCalcContext> wrapper = new LambdaQueryWrapper<>();
        // 根据员工ID筛选
        wrapper.eq(reqDTO.getEmployeeId() != null, SalaryCalcContext::getEmployeeId, reqDTO.getEmployeeId());
        // 根据周期ID筛选
        wrapper.eq(reqDTO.getPeriodId() != null, SalaryCalcContext::getPeriodId, reqDTO.getPeriodId());

        wrapper.orderByDesc(SalaryCalcContext::getCreateTime);

        List<SalaryCalcContext> list = this.list(wrapper);
        return calcContextConvert.toVOList(list);
    }

    @Override
    public CalcContextVO getContextDetail(Long id) {
        SalaryCalcContext entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException("该计算快照不存在");
        }
        return calcContextConvert.toVO(entity);
    }


    /**
     * 辅助方法：保存快照防篡改
     */
    private void saveAuditSnapshot(Long periodId, Long employeeId, Long archiveId, Map<String, Object> env, String pipelineCode, Integer pipelineVersion) {
        // 先删除该周期下的旧快照，保证幂等性
        this.remove(new LambdaQueryWrapper<SalaryCalcContext>()
                .eq(SalaryCalcContext::getPeriodId, periodId)
                .eq(SalaryCalcContext::getEmployeeId, employeeId));

        SalaryCalcContext snapshot = new SalaryCalcContext();
        snapshot.setPeriodId(periodId);
        snapshot.setEmployeeId(employeeId);
        snapshot.setArchiveId(archiveId);
        // 将 Map 转换为 JSON 文本存入数据库
        snapshot.setEnvJson(JSONUtil.toJsonStr(env));
        snapshot.setRemark("引擎自动装配提取快照");

        // 为实体赋予管道编码和版本号，解决非空约束报错！
        snapshot.setPipelineCode(pipelineCode);
        snapshot.setPipelineVersion(pipelineVersion);
        this.save(snapshot);
    }
}
