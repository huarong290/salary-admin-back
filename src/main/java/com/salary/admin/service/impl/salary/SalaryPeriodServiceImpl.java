package com.salary.admin.service.salary.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.period.PeriodConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.salary.SalaryPeriodExtMapper;
import com.salary.admin.model.dto.salary.period.PeriodAddReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodEditReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.salary.period.PeriodVO;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import com.salary.admin.service.salary.ISalaryPeriodService;
import com.salary.admin.utils.UserContextUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 薪资周期信息表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@Service
@Slf4j
public class SalaryPeriodServiceImpl extends ServiceImpl<SalaryPeriodExtMapper, SalaryPeriod> implements ISalaryPeriodService {

    @Resource
    private PeriodConvert periodConvert;

    @Autowired
    private ISalaryEmployeeService employeeService;

    @Value("${salary.delete.allow-physical:false}")
    private boolean allowPhysicalDelete;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addPeriod(PeriodAddReqDTO reqDTO) {
        // 1. 业务校验：防止同一个员工在同一个结算月出现重复周期
        checkUniquePeriod(reqDTO.getEmployeeId(), reqDTO.getSettlementMonth(), null);

        // 2. 转换并保存
        SalaryPeriod entity = periodConvert.toEntity(reqDTO);
        this.save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editPeriod(PeriodEditReqDTO reqDTO) {
        // 1. 检查是否存在
        SalaryPeriod exist = this.getById(reqDTO.getId());
        if (exist == null) {
            throw new BusinessException("薪资周期档案不存在");
        }

        // 2. 修改时同样校验唯一性
        checkUniquePeriod(reqDTO.getEmployeeId(), reqDTO.getSettlementMonth(), reqDTO.getId());

        // 3. 转换并更新
        SalaryPeriod entity = periodConvert.toEntity(reqDTO);
        return this.updateById(entity);
    }

    @Override
    public PageResult<PeriodVO> selectPeriodPage(PeriodQueryReqDTO reqDTO) {
        Page<SalaryPeriod> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        LambdaQueryWrapper<SalaryPeriod> wrapper = new LambdaQueryWrapper<>();

        // 关键词过滤：员工ID或结算月份
        if (reqDTO.getEmployeeId() != null) {
            wrapper.eq(SalaryPeriod::getEmployeeId, reqDTO.getEmployeeId());
        }
        if (StrUtil.isNotBlank(reqDTO.getSettlementMonth())) {
            wrapper.eq(SalaryPeriod::getSettlementMonth, reqDTO.getSettlementMonth());
        }

        wrapper.orderByDesc(SalaryPeriod::getCreateTime);

        IPage<SalaryPeriod> resultPage = this.page(page, wrapper);

        // 转换 VO 列表
        List<PeriodVO> voList = periodConvert.toVOList(resultPage.getRecords());

        // 🌟 核心增强：批量填充员工姓名，避免 N+1 查询
        if (CollUtil.isNotEmpty(voList)) {
            List<Long> employeeIds = voList.stream().map(PeriodVO::getEmployeeId).distinct().collect(Collectors.toList());
            Map<Long, String> nameMap = employeeService.listByIds(employeeIds).stream()
                    .collect(Collectors.toMap(SalaryEmployee::getId, SalaryEmployee::getEmployeeName));
            voList.forEach(vo -> vo.setEmployeeName(nameMap.get(vo.getEmployeeId())));
        }

        return PageResult.of(resultPage, voList);
    }

    @Override
    public PeriodVO getPeriodDetail(Long id) {
        SalaryPeriod entity = this.getById(id);
        if (entity == null) return null;

        PeriodVO vo = periodConvert.toVO(entity);
        // 详情页同样填充姓名
        SalaryEmployee employee = employeeService.getById(vo.getEmployeeId());
        if (employee != null) vo.setEmployeeName(employee.getEmployeeName());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePeriodById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            return this.removeById(id);
        }
        validatePhysicalDeleteAction();
        return baseMapper.physicalDeleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePeriodByIds(List<Long> ids, boolean logicalDelete) {
        if (CollUtil.isEmpty(ids)) return false;
        if (logicalDelete) {
            return this.removeByIds(ids);
        }
        validatePhysicalDeleteAction();
        return baseMapper.physicalDeleteByIds(ids) > 0;
    }

    /**
     * 内部校验：薪资周期唯一性
     */
    private void checkUniquePeriod(Long employeeId, String settlementMonth, Long excludeId) {
        LambdaQueryWrapper<SalaryPeriod> wrapper = new LambdaQueryWrapper<SalaryPeriod>()
                .eq(SalaryPeriod::getEmployeeId, employeeId)
                .eq(SalaryPeriod::getSettlementMonth, settlementMonth);
        if (excludeId != null) {
            wrapper.ne(SalaryPeriod::getId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException("该员工在结算月[" + settlementMonth + "]已存在薪资周期，请勿重复创建");
        }
    }

    /**
     * 物理删除安全校验逻辑
     */
    private void validatePhysicalDeleteAction() {
        if (!allowPhysicalDelete) {
            throw new BusinessException("系统安全策略：当前环境禁止物理删除薪资周期数据");
        }
        if (!UserContextUtil.isAdmin()) {
            throw new BusinessException("权限不足：只有超级管理员可执行物理删除操作");
        }
        log.warn("管理员 {} 正在对薪资周期执行物理删除", UserContextUtil.getUsername());
    }
}