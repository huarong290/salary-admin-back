package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.calclog.CalcLogConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryCalcLogExtMapper;
import com.salary.admin.model.dto.calclog.CalcLogQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcLog;
import com.salary.admin.model.vo.calclog.CalcLogVO;
import com.salary.admin.service.salary.ISalaryCalcLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 薪资计算日志表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryCalcLogServiceImpl extends ServiceImpl<SalaryCalcLogExtMapper, SalaryCalcLog> implements ISalaryCalcLogService {

    private final SalaryCalcLogExtMapper salaryCalcLogExtMapper;

    private final CalcLogConvert calcLogConvert;

    // ======================== 1. 新增操作 (Create) ========================
    // ======================== 2. 删除操作 (Delete) ========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLogById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            // 触发 @TableLogic 逻辑删除
            return this.removeById(id);
        } else {
            // 物理删除，用于运维清理历史冗余数据
            return salaryCalcLogExtMapper.physicalDeleteById(id) > 0;
        }
    }

    @Override
    public boolean deleteLogByIds(List<Long> ids, boolean logicalDelete) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        if (logicalDelete) {
            return this.removeByIds(ids);
        } else {
            return salaryCalcLogExtMapper.physicalDeleteByIds(ids) > 0;
        }
    }
    // ======================== 3. 修改操作 (Update) ========================
    // ======================== 4. 查询操作 (Read) ========================

    @Override
    public PageResult<CalcLogVO> selectLogPage(CalcLogQueryReqDTO reqDTO) {
        Page<SalaryCalcLog> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        LambdaQueryWrapper<SalaryCalcLog> wrapper = new LambdaQueryWrapper<>();

        // 筛选过滤
        wrapper.eq(reqDTO.getEmployeeId() != null, SalaryCalcLog::getEmployeeId, reqDTO.getEmployeeId())
                .eq(reqDTO.getPeriodId() != null, SalaryCalcLog::getPeriodId, reqDTO.getPeriodId())
                .eq(reqDTO.getRuleCode() != null, SalaryCalcLog::getRuleCode, reqDTO.getRuleCode());

        // 严格按照执行顺序排列，还原案发现场
        wrapper.orderByAsc(SalaryCalcLog::getStage, SalaryCalcLog::getId);

        IPage<SalaryCalcLog> resultPage = this.page(page, wrapper);
        return PageResult.of(resultPage, calcLogConvert.toVOList(resultPage.getRecords()));
    }

    @Override
    public CalcLogVO getLogDetail(Long id) {
        SalaryCalcLog entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException("计算日志不存在或已被清理");
        }
        return calcLogConvert.toVO(entity);
    }

    @Override
    public List<CalcLogVO> listLogsByEmployee(Long employeeId, Long periodId) {
        // 用于在工资详情页侧边栏展示“计算链路”
        LambdaQueryWrapper<SalaryCalcLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalaryCalcLog::getEmployeeId, employeeId)
                .eq(SalaryCalcLog::getPeriodId, periodId)
                // 严格按照引擎执行的阶段和顺序排序，还原计算现场
                .orderByAsc(SalaryCalcLog::getStage, SalaryCalcLog::getId);

        return calcLogConvert.toVOList(this.list(wrapper));
    }
}
