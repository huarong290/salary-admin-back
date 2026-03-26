package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.salary.SalarySummaryExtMapper;
import com.salary.admin.model.dto.salary.summary.SummaryQueryReqDTO;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.summary.SummaryVO;
import com.salary.admin.service.salary.ISalarySummaryService;
import com.salary.admin.utils.UserContextUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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

    @Value("${salary.delete.allow-physical:false}")
    private boolean allowPhysicalDelete;


    /**
     * 分页查询汇总列表
     * 利用 ExtMapper 的 selectSummaryPageVo 实现高效的多表关联查询
     */
    @Override
    public PageResult<SummaryVO> selectSummaryPage(SummaryQueryReqDTO reqDTO) {
        // 1. 构造 MyBatis-Plus 分页对象
        Page<SummaryVO> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 2. 直接调用关联查询接口，SQL 层面自动处理 employeeName 和 settlementMonth
        IPage<SummaryVO> resultPage = salarySummaryExtMapper.selectSummaryPageVo(page, reqDTO);

        // 3. 返回封装后的分页结果
        return PageResult.of(resultPage, resultPage.getRecords());
    }

    @Override
    public SummaryVO getSummaryDetail(Long id) {
        if (id == null) {
            throw new BusinessException("详情查询失败：ID不能为空");
        }

        // 直接通过自定义 SQL 获取包含关联字段的 VO
        SummaryVO vo = salarySummaryExtMapper.selectSummaryVoById(id);

        if (vo == null) {
            log.warn("未找到 ID 为 {} 的薪资汇总单", id);
            throw new BusinessException("该薪资汇总单不存在或已被删除");
        }

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

    @Override
    public int batchInsert(List<SalarySummary> summaries) {

        return salarySummaryExtMapper.batchInsert(summaries);
    }

    @Override
    public Long findIdByEmployeeAndMonth(Long employeeId, String settlementMonth) {
        return salarySummaryExtMapper.findIdByEmployeeAndMonth(employeeId,settlementMonth);
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