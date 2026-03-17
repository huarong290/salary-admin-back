package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.salary.SalaryPaymentRecordExtMapper;
import com.salary.admin.model.dto.salary.paymentrecord.PaymentRecordQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryPaymentRecord;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.model.vo.salary.archiveitem.SalaryArchiveItemVO;
import com.salary.admin.model.vo.salary.paymentrecord.SalaryPaymentRecordVO;
import com.salary.admin.service.salary.ISalaryPaymentRecordService;
import com.salary.admin.service.salary.ISalarySummaryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 薪资结算明细记录表 服务实现类
 *
 * @author system
 * @since 2026-03-15
 */
@Service
@Slf4j
public class SalaryPaymentRecordServiceImpl extends ServiceImpl<SalaryPaymentRecordExtMapper, SalaryPaymentRecord> implements ISalaryPaymentRecordService {

    @Resource
    @Lazy // 防止循环依赖
    private ISalarySummaryService salarySummaryService;

    @Override
    public PageResult<SalaryPaymentRecordVO> selectRecordPage(PaymentRecordQueryReqDTO queryReq) {
        Page<SalaryPaymentRecordVO> page = new Page<>(queryReq.getPageNum(), queryReq.getPageSize());
        Page<SalaryPaymentRecordVO> resultPage = baseMapper.selectRecordPage(page, queryReq);

        // 处理 JSON 明细转换
        resultPage.getRecords().forEach(vo -> {
            if (vo.getDetailJson() != null) {
                try {
                    List<SalaryArchiveItemVO> details = JSONUtil.toList(vo.getDetailJson(), SalaryArchiveItemVO.class);
                    vo.setItemDetails(details);
                } catch (Exception e) {
                    log.error("解析薪资明细JSON失败, recordId: {}", vo.getId(), e);
                }
            }
        });

        return PageResult.of(resultPage);
    }
    /**
     * 根据档案核算单条薪资明细
     * @param summaryId 汇总ID
     * @param archive 员工核算基准档案
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createByCalculation(Long summaryId, SalaryArchiveVO archive) {
        log.info("开始系统核算生成结算记录: employee={}, summaryId={}", archive.getEmployeeName(), summaryId);

        SalaryPaymentRecord record = new SalaryPaymentRecord();
        record.setSummaryId(summaryId);
        record.setEmployeeId(archive.getEmployeeId());
        record.setArchiveId(archive.getId());
        record.setIsManual(0);

        // 1. 设置底薪快照基础薪资抓取
        BigDecimal baseSalary = archive.getBaseSalary() != null ? archive.getBaseSalary() : BigDecimal.ZERO;
        record.setBaseSalary(baseSalary);

        // 2. 动态计算明细项 (Items)-统计各项明细
        BigDecimal incomeTotal = BigDecimal.ZERO;
        BigDecimal deductionTotal = BigDecimal.ZERO;

        if (CollUtil.isNotEmpty(archive.getItems())) {
            for (SalaryArchiveItemVO item : archive.getItems()) {
                BigDecimal amount = item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO;
                // itemType: 1-收入, 2-扣款
                if (Integer.valueOf(1).equals(item.getItemType())) {
                    incomeTotal = incomeTotal.add(amount);
                } else if (Integer.valueOf(2).equals(item.getItemType())) {
                    deductionTotal = deductionTotal.add(amount);
                }
            }
            // 存入 JSON 快照，保留核算依据
            record.setDetailJson(JSONUtil.toJsonStr(archive.getItems()));
        }

        record.setIncomeTotal(incomeTotal);
        record.setDeductionTotal(deductionTotal);

        // 3. 计算最终总额
        record.setFinalSalary(baseSalary.add(incomeTotal).subtract(deductionTotal));

        this.save(record);
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createByManual(Long summaryId, Long employeeId, BigDecimal finalAmount, String remark) {
        SalaryPaymentRecord record = new SalaryPaymentRecord();
        record.setSummaryId(summaryId);
        record.setEmployeeId(employeeId);
        record.setIsManual(1);
        record.setFinalSalary(finalAmount != null ? finalAmount : BigDecimal.ZERO);
        record.setBaseSalary(BigDecimal.ZERO);
        record.setIncomeTotal(BigDecimal.ZERO);
        record.setDeductionTotal(BigDecimal.ZERO);
        record.setRemark(remark);

        this.save(record);
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRecord(SalaryPaymentRecord record) {
        if (record.getId() == null) {
            throw new BusinessException("记录ID不能为空");
        }
        boolean success = this.updateById(record);
        if (success) {
            // 重新获取 summaryId 刷新总额
            SalaryPaymentRecord latest = this.getById(record.getId());
            this.refreshSummaryAmount(latest.getSummaryId());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeRecord(Long id) {
        SalaryPaymentRecord record = this.getById(id);
        if (record == null) return false;

        boolean success = this.removeById(id);
        if (success) {
            this.refreshSummaryAmount(record.getSummaryId());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRemoveRecords(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) return false;

        // 获取所有涉及的 summaryId (去重)
        List<Long> summaryIds = this.listByIds(ids).stream()
                .map(SalaryPaymentRecord::getSummaryId)
                .distinct()
                .collect(Collectors.toList());

        boolean success = this.removeByIds(ids);
        if (success) {
            summaryIds.forEach(this::refreshSummaryAmount);
        }
        return success;
    }

    @Override
    public void refreshSummaryAmount(Long summaryId) {
        log.info("刷新薪资汇总总额, summaryId: {}", summaryId);

        // 1. 统计该批次下所有记录的金额之和
        List<SalaryPaymentRecord> records = this.lambdaQuery()
                .eq(SalaryPaymentRecord::getSummaryId, summaryId)
                .list();

        BigDecimal subtotal = BigDecimal.ZERO;      // 应发合计
        BigDecimal deductionTotal = BigDecimal.ZERO; // 扣款合计
        BigDecimal finalTotal = BigDecimal.ZERO;     // 最终实发

        for (SalaryPaymentRecord r : records) {
            // 统计收入 (底薪 + 收入明细)
            subtotal = subtotal.add(r.getBaseSalary()).add(r.getIncomeTotal());
            // 统计扣款
            deductionTotal = deductionTotal.add(r.getDeductionTotal());
            // 统计实发
            finalTotal = finalTotal.add(r.getFinalSalary());
        }

        // 2. 更新汇总表
        SalarySummary summary = new SalarySummary();
        summary.setId(summaryId);
        summary.setSalarySubtotal(subtotal);
        summary.setSalaryDeductionTotal(deductionTotal);
        summary.setSalaryTotal(finalTotal);

        salarySummaryService.updateById(summary);
    }
}
