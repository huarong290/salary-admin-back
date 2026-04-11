package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.summary.SalarySummaryConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalarySummaryExtMapper;
import com.salary.admin.model.dto.salary.summary.SalarySummaryOperateDTO;
import com.salary.admin.model.dto.salary.summary.SummaryAdjustReqDTO;
import com.salary.admin.model.dto.salary.summary.SummaryQueryReqDTO;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.summary.SalarySummaryVO;
import com.salary.admin.service.salary.ISalarySummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 薪资汇总与结算表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalarySummaryServiceImpl extends ServiceImpl<SalarySummaryExtMapper, SalarySummary> implements ISalarySummaryService {

    private  final  SalarySummaryExtMapper salarySummaryExtMapper;
    private  final  SalarySummaryConvert salarySummaryConvert;

    @Override
    public PageResult<SalarySummaryVO> getSummaryPage(SummaryQueryReqDTO queryDTO) {
        // 1. 创建分页对象
        IPage<SalarySummaryVO> pageParam = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2. 调用自定义 SQL
        // 此时查询结果中 details 字段为 null，因为列表页不需要显示具体的计算日志
        IPage<SalarySummaryVO> pageResult = salarySummaryExtMapper.selectSummaryPage(pageParam, queryDTO);

        // 3. 利用你 PageResult.of(page) 静态方法自动构建返回对象
        return PageResult.of(pageResult);

    }

    @Override
    public SalarySummaryVO getSummaryDetail(Long id) {
        SalarySummary summary = this.getById(id);
        if (summary == null || summary.getDeleteFlag() > 0) {
            throw new BusinessException("该薪资结算单不存在或已被删除");
        }
        // 1. 检查数据库原始字符串
        log.info("DEBUG 1: 数据库原始 JSON 长度 = {}",
                summary.getDetailJson() != null ? summary.getDetailJson().length() : "NULL");
        // 直接调用 MapStruct 转换
        SalarySummaryVO vo = salarySummaryConvert.toVO(summary);
        // 2. 检查 MapStruct 转换后的对象状态
        if (vo.getDetails() != null) {
            log.info("DEBUG 2: VO.details 转换成功! 包含收入项数量: {}",
                    vo.getDetails().getIncome() != null ? vo.getDetails().getIncome().size() : 0);
        } else {
            log.warn("DEBUG 2: VO.details 依然是 NULL！");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLockStatus(SalarySummaryOperateDTO dto) {
        List<Long> ids = dto.getIds();
        Integer targetStatus = dto.getLockFlag();

        if (CollectionUtils.isEmpty(ids)) return false;

        // 1. 根据目标状态执行不同的业务校验
        if (Integer.valueOf(1).equals(targetStatus)) {
            // 【锁定红线】只有计算成功 (calcStatus=1) 的才允许锁定
            long count = this.lambdaQuery()
                    .in(SalarySummary::getId, ids)
                    .ne(SalarySummary::getCalcStatus, 1)
                    .count();
            if (count > 0) throw new BusinessException("包含未计算成功单据，无法执行锁定");

        } else if (Integer.valueOf(0).equals(targetStatus)) {
            // 【解锁红线】已经支付 (paymentStatus=1) 的绝对严禁解锁
            long count = this.lambdaQuery()
                    .in(SalarySummary::getId, ids)
                    .eq(SalarySummary::getPaymentStatus, 1)
                    .count();
            if (count > 0) throw new BusinessException("包含已发放单据，受合规保护禁止解锁重算");

        } else {
            throw new BusinessException("无效的锁定状态指令");
        }

        // 2. 执行统一更新
        log.info("执行薪资单状态变更，目标状态: {}, 涉及记录: {}", targetStatus, ids.size());
        return this.lambdaUpdate()
                .in(SalarySummary::getId, ids)
                .set(SalarySummary::getLockFlag, targetStatus)
                .update();
    }

    @Override
    public SalarySummary getSummaryByUnique(Long periodId, Long employeeId) {
        if (periodId == null || employeeId == null) {
            return null;
        }

        // 1. 执行唯一查询
        SalarySummary summary = this.lambdaQuery()
                .eq(SalarySummary::getPeriodId, periodId)
                .eq(SalarySummary::getEmployeeId, employeeId)
                .one(); // 数据库中有唯一索引保证

        // 2. 💡 架构师建议：增加“锁定保护”校验
        if (summary != null && Integer.valueOf(1).equals(summary.getLockFlag())) {
            // 如果单据已锁定，说明已经财务关账或发放，此时引擎尝试重写数据是非常危险的
            log.warn("⚠️ 警告：员工[{}]在周期[{}]的薪资单已锁定，引擎尝试重算已被拦截。", employeeId, periodId);
            throw new BusinessException("该薪资单已锁定（可能已关账或发放），禁止重算！请先联系财务解锁。");
        }

        return summary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adjustManualAmount(SummaryAdjustReqDTO reqDTO) {
        SalarySummary summary = this.getById(reqDTO.getId());
        if (summary == null || summary.getDeleteFlag() > 0) {
            throw new BusinessException("该薪资结算单不存在或已被删除！");
        }

        // 后端强制双重校验状态，防止越权恶意调用
        if (Integer.valueOf(1).equals(summary.getLockFlag())) {
            log.warn("越权操作拦截：尝试修改已锁定的薪资单，ID: {}", summary.getId());
            throw new BusinessException("该单据已被锁定准备发薪，请先解除锁定！");
        }
        if (Integer.valueOf(1).equals(summary.getPaymentStatus())) {
            log.warn("越权操作拦截：尝试修改已支付的薪资单，ID: {}", summary.getId());
            throw new BusinessException("该单据已支付完毕，严禁篡改金额！");
        }

        // 执行资金覆写
        summary.setManualPaymentAmount(reqDTO.getManualPaymentAmount());

        // 追加审计留痕备注 (不覆盖原有备注，追加时间戳信息)
        if (reqDTO.getRemark() != null && !reqDTO.getRemark().isBlank()) {
            String logPrefix = "[手工账调整: " + reqDTO.getManualPaymentAmount() + "] ";
            String currentRemark = summary.getRemark() == null ? "" : summary.getRemark() + " | ";
            summary.setRemark(currentRemark + logPrefix + reqDTO.getRemark());
        }

        return this.updateById(summary);
    }


}
