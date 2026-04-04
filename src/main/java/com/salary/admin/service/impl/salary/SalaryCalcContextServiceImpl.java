package com.salary.admin.service.impl.salary;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.convert.salary.calccontext.CalcContextConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryCalcContextExtMapper;
import com.salary.admin.model.dto.calccontext.CalcContextAddReqDTO;
import com.salary.admin.model.dto.calccontext.CalcContextEditReqDTO;
import com.salary.admin.model.dto.calccontext.CalcContextQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcContext;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.model.entity.salary.SalaryKpiRecord;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.calccontext.CalcContextVO;
import com.salary.admin.service.ISalaryKpiRecordService;
import com.salary.admin.service.salary.ISalaryArchiveService;
import com.salary.admin.service.salary.ISalaryCalcContextService;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import com.salary.admin.service.salary.ISalaryPeriodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // ======================== 1. 新增操作 (Create) ========================
    @Override
    public Long addContext(CalcContextAddReqDTO reqDTO) {
        SalaryCalcContext entity = calcContextConvert.toEntity(reqDTO);
        this.save(entity);
        log.info("已生成计算快照，ID: {}, 员工ID: {}, 周期ID: {}", entity.getId(), entity.getEmployeeId(), entity.getPeriodId());
        return entity.getId();
    }
    @Override
    public Map<String, Object> buildEmployeeContext(Long periodId, Long employeeId, String pipelineCode, Integer pipelineVersion) {
        log.debug("开始组装薪资核算上下文 Env | 周期: {}, 员工: {}", periodId, employeeId);
        Map<String, Object> env = new HashMap<>();

        // ==========================================
        // 1. 获取员工基础信息
        // ==========================================
        SalaryEmployee employee = iSalaryEmployeeService.getById(employeeId);
        if (employee == null) {
            throw new BusinessException("上下文中找不到对应的员工信息: " + employeeId);
        }
        // 注入员工变量
        env.put("employeeId", employee.getId());
        env.put("employmentStatus", employee.getEmploymentStatus()); // 如: 1-正式, 2-试用, 0-离职
        env.put("accommodationStatus", employee.getAccommodationStatus()); // 住宿状态，可用于房补规则条件

        // 我们之前设计的远程/坐班标志（示例扩展）
        // env.put("workMode", employee.getWorkMode());

        // ==========================================
        // 2. 获取薪资档案信息 (最新生效版本)
        // ==========================================
        // 这里假设你有获取最新生效档案的方法
        var archive = iSalaryArchiveService.getLatestEffectiveArchive(employeeId);
        if (archive == null) {
            throw new BusinessException("员工 [" + employee.getEmployeeName() + "] 缺失有效的薪资档案，请先定薪！");
        }

        // 注入档案变量：核心！(这些变量必须严格与 Aviator 公式里的英文对齐)
        env.put("baseSalary", archive.getBaseSalary() != null ? archive.getBaseSalary() : BigDecimal.ZERO);
        env.put("taxRuleCode", archive.getTaxRuleCode()); // 个税分发路由凭证 (如: TAX_RESIDENT_CN, NO_TAX)

        // 动态将档案明细 (Archive Items) 的配置项注入环境
        // 假设档案里配置了房补 std_housing_allow: 300，则 env.put("std_housing_allow", 300)
        if (!CollectionUtils.isEmpty(archive.getArchiveItems())) {
            archive.getArchiveItems().forEach(item -> {
                // itemCode 就是配置字典里的英文字段
                env.put(item.getTypeName(), item.getAmount());
            });
        }

        // ==========================================
        // 3. 跨模块获取动态业务数据 (考勤、绩效)-> 🌟 真实查库联调
        // ==========================================
        // 3.1 提取真实的考勤周期数据
        SalaryPeriod period = iSalaryPeriodService.getById(periodId);
        if (period != null) {
            env.put("monthDays", period.getMonthDays() != null ? period.getMonthDays() : BigDecimal.ZERO);
            env.put("attendanceDays", period.getAttendanceDays() != null ? period.getAttendanceDays() : BigDecimal.ZERO);
            env.put("isFullAttendance", period.getFullAttendanceFlag() != null && period.getFullAttendanceFlag() == 1);
        } else {
            // 防空兜底
            env.put("monthDays", BigDecimal.ZERO);
            env.put("attendanceDays", BigDecimal.ZERO);
            env.put("isFullAttendance", false);
        }

        // 3.2 提取真实的【已定稿】绩效打分数据
        SalaryKpiRecord kpi = iSalaryKpiRecordService.lambdaQuery()
                .eq(SalaryKpiRecord::getPeriodId, periodId)
                .eq(SalaryKpiRecord::getEmployeeId, employeeId)
                .eq(SalaryKpiRecord::getAuditStatus, 1) // ⚠️ 核心：必须是已定稿可算薪的状态
                .eq(SalaryKpiRecord::getEffectiveFlag, 1)
                .one();

        if (kpi != null) {
            env.put("kpiGrade", kpi.getKpiGrade());
            env.put("kpiScore", kpi.getKpiScore() != null ? kpi.getKpiScore() : BigDecimal.ZERO);
            //引擎的 KPI_BONUS 公式强依赖这个变量：
            env.put("kpiCoefficient", kpi.getKpiCoefficient() != null ? kpi.getKpiCoefficient() : BigDecimal.ZERO);
        } else {
            // 如果该员工本月没有定稿的绩效，默认不发绩效奖金
            env.put("kpiGrade", "WAITING");
            env.put("kpiScore", BigDecimal.ZERO);
            env.put("kpiCoefficient", BigDecimal.ZERO);
        }

        // ==========================================
        // 4. 落盘上下文计算快照，用于发薪审计和追溯
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
    private void saveAuditSnapshot(Long periodId, Long employeeId, Long archiveId, Map<String, Object> env,String pipelineCode, Integer pipelineVersion) {
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
