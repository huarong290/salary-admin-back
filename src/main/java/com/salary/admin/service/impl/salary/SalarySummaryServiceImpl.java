package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.summary.SalarySummaryConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalarySummaryExtMapper;
import com.salary.admin.model.dto.salary.summary.SalarySummaryOperateDTO;
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
        // 直接调用 MapStruct 转换
        return salarySummaryConvert.toVO(summary);
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


}
