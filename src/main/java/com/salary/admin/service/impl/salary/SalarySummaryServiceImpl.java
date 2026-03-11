package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.summary.SummaryConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.salary.SalarySummaryExtMapper;
import com.salary.admin.model.dto.salary.summary.SummaryCalcReqDTO;
import com.salary.admin.model.dto.salary.summary.SummaryQueryReqDTO;
import com.salary.admin.model.entity.salary.*;
import com.salary.admin.model.vo.salary.summary.SummaryVO;
import com.salary.admin.service.salary.*;
import com.salary.admin.utils.UserContextUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 薪资结算汇总单 服务实现类
 *
 * @author system
 * @since 2026-03-11
 */
@Service
@Slf4j
public class SalarySummaryServiceImpl extends ServiceImpl<SalarySummaryExtMapper, SalarySummary> implements ISalarySummaryService {

    @Resource
    private SalarySummaryExtMapper salarySummaryExtMapper;
    @Resource
    private ISalaryIncomeDetailService incomeDetailService;
    @Resource
    private ISalaryDeductionDetailService deductionDetailService;
    @Resource
    private ISalaryPeriodService periodService;
    @Resource
    private ISalaryEmployeeService employeeService;
    @Resource
    private SummaryConvert summaryConvert;

    @Value("${salary.delete.allow-physical:false}")
    private boolean allowPhysicalDelete;

    /**
     * 🌟 核心引擎：一键汇总结算
     * 逻辑：根据 PeriodID 拉取所有收入/扣款明细 -> 累加 -> 计算实发金额 -> 存在则更新，不存在则插入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long calculateSummary(SummaryCalcReqDTO reqDTO) {
        Long periodId = reqDTO.getPeriodId();

        // 1. 业务前置校验：确保该周期真实存在
        SalaryPeriod period = periodService.getById(periodId);
        if (period == null) {
            throw new BusinessException("异常操作：对应的薪资周期不存在，无法结算");
        }

        // 2. 累加应发合计 (总收入)
        // 使用 stream map-reduce 优雅累加 BigDecimal，若无明细则兜底为 ZERO
        BigDecimal totalIncome = incomeDetailService.list(
                        new LambdaQueryWrapper<SalaryIncomeDetail>().eq(SalaryIncomeDetail::getPeriodId, periodId)
                ).stream()
                .map(SalaryIncomeDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 累加应扣合计 (总扣款)
        BigDecimal totalDeduction = deductionDetailService.list(
                        new LambdaQueryWrapper<SalaryDeductionDetail>().eq(SalaryDeductionDetail::getPeriodId, periodId)
                ).stream()
                .map(SalaryDeductionDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. 计算实发工资 (TotalIncome - TotalDeduction)
        BigDecimal netSalary = totalIncome.subtract(totalDeduction);

        // 5. 数据持久化 (幂等性设计)
        // 尝试查询当前周期是否已经生成过汇总单
        SalarySummary summary = this.getOne(new LambdaQueryWrapper<SalarySummary>().eq(SalarySummary::getPeriodId, periodId));
        if (summary == null) {
            summary = new SalarySummary(); // 首次结算，新建实体
        }


        // 覆盖最新计算结果 (严格对齐真实实体类字段)
        summary.setPeriodId(periodId);

        // 应发小计 (本币)
        summary.setSalarySubtotal(totalIncome);

        // 扣款小计 (本币)
        summary.setSalaryDeductionTotal(totalDeduction);

        // 最终结算薪资 (本币：应发 - 应扣)
        summary.setSalaryTotal(netSalary);

        // 如果传入了新备注时才更新，避免覆盖原有的审批备注
        if (StrUtil.isNotBlank(reqDTO.getRemark())) {
            summary.setRemark(reqDTO.getRemark());
        }


        this.saveOrUpdate(summary);
        log.info("周期ID [{}] 结算完成。总收入:{}, 总扣款:{}, 实发:{}", periodId, totalIncome, totalDeduction, netSalary);
        return summary.getId();
    }

    @Override
    public PageResult<SummaryVO> selectSummaryPage(SummaryQueryReqDTO reqDTO) {
        Page<SalarySummary> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        LambdaQueryWrapper<SalarySummary> wrapper = new LambdaQueryWrapper<>();

        // 如果传了具体周期则精确匹配
        if (reqDTO.getPeriodId() != null) {
            wrapper.eq(SalarySummary::getPeriodId, reqDTO.getPeriodId());
        }

        // 🌟 复杂过滤：如果按员工或月份查，需要先从 Period 表倒推出 periodIds
        if (reqDTO.getEmployeeId() != null || StrUtil.isNotBlank(reqDTO.getSettlementMonth())) {
            LambdaQueryWrapper<SalaryPeriod> pWrapper = new LambdaQueryWrapper<>();
            if (reqDTO.getEmployeeId() != null) pWrapper.eq(SalaryPeriod::getEmployeeId, reqDTO.getEmployeeId());
            if (StrUtil.isNotBlank(reqDTO.getSettlementMonth()))
                pWrapper.eq(SalaryPeriod::getSettlementMonth, reqDTO.getSettlementMonth());

            List<Long> pIds = periodService.list(pWrapper).stream().map(SalaryPeriod::getId).collect(Collectors.toList());
            if (CollUtil.isEmpty(pIds)) {
                return PageResult.empty(); // 如果没找到相关的周期，直接返回空分页
            }
            wrapper.in(SalarySummary::getPeriodId, pIds);
        }

        wrapper.orderByDesc(SalarySummary::getCreateTime);
        IPage<SalarySummary> resultPage = this.page(page, wrapper);
        List<SummaryVO> voList = summaryConvert.toVOList(resultPage.getRecords());

        // 🌟 内存拼接：批量填充员工姓名与结算月份
        fillExtensionFields(voList);

        return PageResult.of(resultPage, voList);
    }

    @Override
    public SummaryVO getSummaryDetail(Long id) {
        SalarySummary entity = this.getById(id);
        if (entity == null) return null;

        SummaryVO vo = summaryConvert.toVO(entity);
        fillExtensionFields(CollUtil.newArrayList(vo)); // 复用批量填充逻辑
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id, boolean logicalDelete) {
        if (logicalDelete) return this.removeById(id);
        validateDeleteAuth();
        return salarySummaryExtMapper.physicalDeleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids, boolean logicalDelete) {
        if (CollUtil.isEmpty(ids)) return false;
        if (logicalDelete) return this.removeByIds(ids);
        validateDeleteAuth();
        return salarySummaryExtMapper.physicalDeleteByIds(ids) > 0;
    }

    /**
     * 内部私有方法：批量填充 VO 的扩展字段 (降低与数据库的交互频次)
     */
    private void fillExtensionFields(List<SummaryVO> voList) {
        if (CollUtil.isEmpty(voList)) return;

        // 提取所有关联的 PeriodId
        List<Long> periodIds = voList.stream().map(SummaryVO::getPeriodId).distinct().collect(Collectors.toList());

        // 批量查询 Period 映射
        Map<Long, SalaryPeriod> periodMap = periodService.listByIds(periodIds).stream()
                .collect(Collectors.toMap(SalaryPeriod::getId, p -> p));

        // 提取所有的 EmployeeId 并批量查询员工映射
        List<Long> employeeIds = periodMap.values().stream().map(SalaryPeriod::getEmployeeId).distinct().collect(Collectors.toList());
        Map<Long, String> empNameMap = employeeService.listByIds(employeeIds).stream()
                .collect(Collectors.toMap(SalaryEmployee::getId, SalaryEmployee::getEmployeeName));

        // 内存组装
        voList.forEach(vo -> {
            SalaryPeriod period = periodMap.get(vo.getPeriodId());
            if (period != null) {
                vo.setSettlementMonth(period.getSettlementMonth());
                vo.setEmployeeName(empNameMap.get(period.getEmployeeId()));
            }
        });
    }

    /**
     * 物理删除权限锁
     */
    private void validateDeleteAuth() {
        if (!allowPhysicalDelete || !UserContextUtil.isAdmin()) {
            throw new BusinessException("安全策略：禁止物理删除最终的结算账单数据");
        }
    }
}