package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.kpi.KpiRecordConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryKpiRecordExtMapper;
import com.salary.admin.model.dto.salary.kpi.KpiBatchInitReqDTO;
import com.salary.admin.model.dto.salary.kpi.KpiEvaluateReqDTO;
import com.salary.admin.model.dto.salary.kpi.KpiRecordQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryKpiRecord;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.salary.kpi.SalaryKpiRecordVO;
import com.salary.admin.service.ISalaryKpiRecordService;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import com.salary.admin.service.salary.ISalaryPeriodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工月度绩效考核记录表 服务实现类
 * 核心定位：连接业务打分与薪资引擎计算的关键桥梁
 * </p>
 *
 * @author system
 * @since 2026-04-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryKpiRecordServiceImpl extends ServiceImpl<SalaryKpiRecordExtMapper, SalaryKpiRecord> implements ISalaryKpiRecordService {

    private final SalaryKpiRecordExtMapper salaryKpiRecordExtMapper;
    private final KpiRecordConvert kpiRecordConvert;
    private final ISalaryPeriodService iSalaryPeriodService;
    private final ISalaryEmployeeService iSalaryEmployeeService;

    @Override
    public PageResult<SalaryKpiRecordVO> getKpiRecordPage(KpiRecordQueryReqDTO reqDTO) {
        // 1. 构建 MyBatis-Plus 泛型为 VO 的分页对象
        Page<SalaryKpiRecordVO> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 2. 直接调用自定义 XML 执行连表查询
        IPage<SalaryKpiRecordVO> resultPage = salaryKpiRecordExtMapper.selectKpiRecordPage(page, reqDTO);

        // 3. 封装标准 PageResult 返回
        return PageResult.of(resultPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initMonthlyKpi(KpiBatchInitReqDTO reqDTO) {
        String month = reqDTO.getSettlementMonth();

        // 1. 查出当月所有的薪资周期数据 (Period 是底座)
        List<SalaryPeriod> periodList = iSalaryPeriodService.lambdaQuery()
                .eq(SalaryPeriod::getSettlementMonth, month)
                .list();

        if (CollUtil.isEmpty(periodList)) {
            throw new BusinessException("该月份暂无考勤周期数据，请先初始化薪资账套！");
        }

        // 2. 查出当月已经存在的绩效记录，防止重复生成 (幂等性控制)
        Set<Long> existPeriodIds = this.lambdaQuery()
                .eq(SalaryKpiRecord::getSettlementMonth, month)
                .list()
                .stream()
                .map(SalaryKpiRecord::getPeriodId)
                .collect(Collectors.toSet());

        // 3. 过滤出需要新建绩效单的周期，并组装实体
        List<SalaryKpiRecord> newRecords = periodList.stream()
                .filter(p -> !existPeriodIds.contains(p.getId()))
                .map(p -> {
                    SalaryKpiRecord kpi = new SalaryKpiRecord();
                    kpi.setEmployeeId(p.getEmployeeId());
                    kpi.setPeriodId(p.getId()); //  核心硬关联
                    kpi.setSettlementMonth(month);
                    kpi.setAuditStatus(0); // 0-打分中/草稿
                    kpi.setEffectiveFlag(1); // 1-生效
                    kpi.setKpiGrade("WAITING"); // 待评估标识
                    kpi.setKpiCoefficient(BigDecimal.ZERO);
                    return kpi;
                }).collect(Collectors.toList());

        // 4. 批量落盘
        if (CollUtil.isNotEmpty(newRecords)) {
            this.saveBatch(newRecords);
            log.info("🎯 成功初始化 {} 条 {} 月份的绩效草稿记录", newRecords.size(), month);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluateKpi(KpiEvaluateReqDTO reqDTO) {
        SalaryKpiRecord record = this.getById(reqDTO.getId());
        if (record == null) {
            throw new BusinessException("该绩效考核单不存在！");
        }
        if (record.getAuditStatus() == 1) {
            throw new BusinessException("该绩效单已确认定稿，禁止再修改打分！");
        }

        // 1. 覆盖主管打分数据
        record.setKpiGrade(reqDTO.getKpiGrade().toUpperCase());
        if (reqDTO.getKpiScore() != null) {
            record.setKpiScore(reqDTO.getKpiScore());
        }
        if (StringUtils.isNotBlank(reqDTO.getEvaluateRemark())) {
            record.setEvaluateRemark(reqDTO.getEvaluateRemark());
        }

        // 2.  核心业务：根据配置的规则智能转换系数 (供算薪引擎使用)
        record.setKpiCoefficient(this.convertGradeToCoefficient(record.getKpiGrade()));

        this.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmKpi(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) return;

        List<SalaryKpiRecord> records = this.listByIds(ids);
        for (SalaryKpiRecord record : records) {
            // 阻断未打分的非法越级确认
            if ("WAITING".equalsIgnoreCase(record.getKpiGrade())) {
                throw new BusinessException("存在未打评级的绩效单，无法进行批量定稿！");
            }
            record.setAuditStatus(1); // 1-已确认(可算薪)
        }
        this.updateBatchById(records);
    }

    /**
     * 辅助方法：评级转换系数路由中心
     * (根据实际公司制度调整，未来可配置入字典表)
     */
    private BigDecimal convertGradeToCoefficient(String grade) {
        if (StringUtils.isBlank(grade)) return BigDecimal.ZERO;

        return switch (grade) {
            case "S" -> new BigDecimal("1.0000");// 拿底薪的 100%
            case "A" -> new BigDecimal("0.5000"); // A级上浮 20%
            case "B" -> new BigDecimal("0.2000"); // B级正常拿 100%
            case "C" -> BigDecimal.ZERO; // C级  0%
            case "D" -> BigDecimal.ZERO; // D级 0%
            default  -> BigDecimal.ZERO; // 默认 0%
        };
    }
}
