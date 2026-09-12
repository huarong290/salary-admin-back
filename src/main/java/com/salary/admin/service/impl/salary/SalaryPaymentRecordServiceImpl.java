package com.salary.admin.service.impl.salary;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryPaymentRecordExtMapper;
import com.salary.admin.model.dto.salary.paymentrecord.PaymentRecordQueryReqDTO;
import com.salary.admin.model.dto.salary.snapshot.SalaryDetailItemDTO;
import com.salary.admin.model.dto.salary.snapshot.SalarySnapshotDTO;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.model.entity.salary.SalaryPaymentRecord;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.paymentrecord.SalaryPaymentRecordVO;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import com.salary.admin.service.salary.ISalaryPaymentRecordService;
import com.salary.admin.service.salary.ISalarySummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 薪资结算明细/流水记录 服务实现类
 * </p>
 *
 * 业务定位：以 salary_summary 为主数据源提供"工资单流水底稿"视图，
 * 员工姓名/工号由 salary_employee 批量补全，明细快照由 detail_json 解析而来。
 *
 * @author system
 * @since 2026-03-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryPaymentRecordServiceImpl extends ServiceImpl<SalaryPaymentRecordExtMapper, SalaryPaymentRecord> implements ISalaryPaymentRecordService {

    private final ISalarySummaryService salarySummaryService;
    private final ISalaryEmployeeService salaryEmployeeService;

    // ======================== 1. 查询操作 (Read) ========================
    @Override
    public PageResult<SalaryPaymentRecordVO> pageQuery(PaymentRecordQueryReqDTO reqDTO) {
        IPage<SalarySummary> pageParam = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        IPage<SalarySummary> page = salarySummaryService.lambdaQuery()
                .eq(reqDTO.getSummaryId() != null, SalarySummary::getId, reqDTO.getSummaryId())
                .eq(reqDTO.getSettlementMonth() != null && !reqDTO.getSettlementMonth().isBlank(),
                        SalarySummary::getSettlementMonth,
                        reqDTO.getSettlementMonth() != null ? reqDTO.getSettlementMonth().replace("-", "") : null)
                .like(StringUtils.isNotBlank(reqDTO.getKeyword()), SalarySummary::getEmployeeName, reqDTO.getKeyword())
                .or(StringUtils.isNotBlank(reqDTO.getKeyword()), w -> w.like(SalarySummary::getEmployeeCode, reqDTO.getKeyword()))
                .orderByDesc(SalarySummary::getSettlementMonth)
                .orderByDesc(SalarySummary::getId)
                .page(pageParam);

        List<SalarySummary> records = page.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return PageResult.of(page, new ArrayList<>());
        }

        // 批量补全员工姓名/工号 (从 summary 快照兜底)
        Set<Long> empIds = records.stream().map(SalarySummary::getEmployeeId).collect(Collectors.toSet());
        Map<Long, SalaryEmployee> empMap = salaryEmployeeService.listByIds(empIds).stream()
                .collect(Collectors.toMap(SalaryEmployee::getId, e -> e, (v1, v2) -> v1));

        List<SalaryPaymentRecordVO> voList = records.stream().map(r -> {
            SalaryEmployee emp = empMap.get(r.getEmployeeId());
            SalaryPaymentRecordVO vo = new SalaryPaymentRecordVO();
            vo.setId(r.getId());
            vo.setSummaryId(r.getId());
            vo.setEmployeeId(r.getEmployeeId());
            vo.setEmployeeName(emp != null ? emp.getEmployeeName() : r.getEmployeeName());
            vo.setEmployeeNo(emp != null ? emp.getEmployeeCode() : r.getEmployeeCode());
            vo.setSettlementMonth(r.getSettlementMonth());
            // 多币种：结算币种与底薪从核算快照读取 (员工档案币种)
            SalarySnapshotDTO snap = parseSnapshot(r.getDetailJson());
            vo.setSettlementCurrency(snap != null && StringUtils.isNotBlank(snap.getSettlementCurrency())
                    ? snap.getSettlementCurrency() : "CNY");
            vo.setIncomeTotal(r.getIncomeTotal());
            vo.setDeductionTotal(r.getDeductionTotal());
            vo.setTaxTotal(r.getTaxTotal());
            vo.setBaseSalary(snap != null && snap.getBaseSalary() != null
                    ? snap.getBaseSalary() : BigDecimal.ZERO);
            // 实发 = 系统净额 + 手工调整
            BigDecimal manual = r.getManualPaymentAmount() != null ? r.getManualPaymentAmount() : BigDecimal.ZERO;
            vo.setFinalSalary(r.getNetSalary().add(manual));
            vo.setIsManual(manual.compareTo(BigDecimal.ZERO) != 0 ? 1 : 0);
            vo.setDetailJson(r.getDetailJson());
            vo.setSnapshotItems(parseSnapshotItems(r.getDetailJson()));
            vo.setRemark(r.getRemark());
            vo.setCreateTime(r.getCreateTime());
            vo.setUpdateTime(r.getUpdateTime());
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(page, voList);
    }

    @Override
    public SalaryPaymentRecordVO getRecordDetail(Long id) {
        SalarySummary summary = salarySummaryService.getById(id);
        if (summary == null) {
            throw new BusinessException("流水记录不存在或已被删除");
        }
        SalaryEmployee emp = salaryEmployeeService.getById(summary.getEmployeeId());
        SalaryPaymentRecordVO vo = new SalaryPaymentRecordVO();
        vo.setId(summary.getId());
        vo.setSummaryId(summary.getId());
        vo.setEmployeeId(summary.getEmployeeId());
        vo.setEmployeeName(emp != null ? emp.getEmployeeName() : summary.getEmployeeName());
        vo.setEmployeeNo(emp != null ? emp.getEmployeeCode() : summary.getEmployeeCode());
        vo.setSettlementMonth(summary.getSettlementMonth());
        // 多币种：结算币种与底薪从核算快照读取 (员工档案币种)
        SalarySnapshotDTO snap = parseSnapshot(summary.getDetailJson());
        vo.setSettlementCurrency(snap != null && StringUtils.isNotBlank(snap.getSettlementCurrency())
                ? snap.getSettlementCurrency() : "CNY");
        vo.setIncomeTotal(summary.getIncomeTotal());
        vo.setDeductionTotal(summary.getDeductionTotal());
        vo.setTaxTotal(summary.getTaxTotal());
        vo.setBaseSalary(snap != null && snap.getBaseSalary() != null
                ? snap.getBaseSalary() : BigDecimal.ZERO);
        BigDecimal manual = summary.getManualPaymentAmount() != null ? summary.getManualPaymentAmount() : BigDecimal.ZERO;
        vo.setFinalSalary(summary.getNetSalary().add(manual));
        vo.setIsManual(manual.compareTo(BigDecimal.ZERO) != 0 ? 1 : 0);
        vo.setDetailJson(summary.getDetailJson());
        vo.setSnapshotItems(parseSnapshotItems(summary.getDetailJson()));
        vo.setRemark(summary.getRemark());
        vo.setCreateTime(summary.getCreateTime());
        vo.setUpdateTime(summary.getUpdateTime());
        return vo;
    }

    // ======================== 2. 修改操作 (Update) ========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFinalSalary(Long id, BigDecimal finalSalary, String remark) {
        if (finalSalary == null) {
            throw new BusinessException("修正后的实发金额不能为空");
        }
        SalarySummary summary = salarySummaryService.getById(id);
        if (summary == null) {
            throw new BusinessException("流水记录不存在或已被删除");
        }
        // 状态机保护：已支付禁止篡改
        if (Integer.valueOf(1).equals(summary.getPaymentStatus())) {
            throw new BusinessException("该单据已支付完毕，严禁篡改金额！");
        }
        if (Integer.valueOf(1).equals(summary.getLockFlag())) {
            throw new BusinessException("该单据已被锁定准备发薪，请先解除锁定！");
        }

        // 联动：手工调整额 = 目标实发 - 系统净额
        BigDecimal manual = finalSalary.subtract(summary.getNetSalary());
        summary.setManualPaymentAmount(manual);

        // 审计留痕：追加调整备注
        if (StringUtils.isNotBlank(remark)) {
            String logPrefix = "[流水修正: 目标实发 " + finalSalary + "] ";
            String currentRemark = summary.getRemark() == null ? "" : summary.getRemark() + " | ";
            summary.setRemark(currentRemark + logPrefix + remark);
        }
        return salarySummaryService.updateById(summary);
    }

    // ======================== 3. 删除操作 (Delete) ========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRecord(Long id) {
        SalarySummary summary = salarySummaryService.getById(id);
        if (summary == null) {
            throw new BusinessException("流水记录不存在或已被删除");
        }
        if (Integer.valueOf(1).equals(summary.getPaymentStatus())) {
            throw new BusinessException("该单据已支付完毕，严禁删除！");
        }
        return salarySummaryService.removeById(id);
    }

    // ======================== 4. 私有辅助方法 ========================
    /**
     * 解析核算快照 JSON (失败返回 null)
     */
    private SalarySnapshotDTO parseSnapshot(String detailJson) {
        if (StringUtils.isBlank(detailJson)) {
            return null;
        }
        try {
            return JSONUtil.toBean(detailJson, SalarySnapshotDTO.class);
        } catch (Exception e) {
            log.warn("detailJson 解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析 detailJson 为明细快照列表
     */
    private List<SalaryPaymentRecordVO.ArchiveItemDetailVO> parseSnapshotItems(String detailJson) {
        SalarySnapshotDTO snapshot = parseSnapshot(detailJson);
        if (snapshot == null) {
            return Collections.emptyList();
        }
        try {
            List<SalaryPaymentRecordVO.ArchiveItemDetailVO> items = new ArrayList<>();
            appendItems(items, snapshot.getIncome(), 1);
            appendItems(items, snapshot.getDeduction(), 2);
            appendItems(items, snapshot.getTax(), 3);
            appendItems(items, snapshot.getCompanyExpense(), 4);
            return items;
        } catch (Exception e) {
            log.warn("detailJson 解析失败, 返回空明细: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private void appendItems(List<SalaryPaymentRecordVO.ArchiveItemDetailVO> target,
                             List<SalaryDetailItemDTO> source,
                             int itemType) {
        if (CollectionUtils.isEmpty(source)) {
            return;
        }
        source.forEach(dto -> {
            if (dto == null || dto.getSettlementAmount() == null || dto.getSettlementAmount().compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
            SalaryPaymentRecordVO.ArchiveItemDetailVO item = new SalaryPaymentRecordVO.ArchiveItemDetailVO();
            item.setItemName(dto.getItemName());
            item.setItemType(itemType);
            item.setAmount(dto.getSettlementAmount().abs());
            item.setFormula(dto.getCalcLog());
            target.add(item);
        });
    }
}
