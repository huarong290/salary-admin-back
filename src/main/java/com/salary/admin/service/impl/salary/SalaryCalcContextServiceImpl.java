package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.convert.salary.calccontext.CalcContextConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryCalcContextExtMapper;
import com.salary.admin.model.dto.calccontext.CalcContextAddReqDTO;
import com.salary.admin.model.dto.calccontext.CalcContextEditReqDTO;
import com.salary.admin.model.dto.calccontext.CalcContextQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcContext;
import com.salary.admin.model.vo.calccontext.CalcContextVO;
import com.salary.admin.service.salary.ISalaryCalcContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

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
    // ======================== 1. 新增操作 (Create) ========================
    @Override
    public Long addContext(CalcContextAddReqDTO reqDTO) {
        SalaryCalcContext entity = calcContextConvert.toEntity(reqDTO);
        this.save(entity);
        log.info("已生成计算快照，ID: {}, 员工ID: {}, 周期ID: {}", entity.getId(), entity.getEmployeeId(), entity.getPeriodId());
        return entity.getId();
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
}
