package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.deductiondetail.DeductionDetailConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.salary.SalaryDeductionDetailExtMapper;
import com.salary.admin.model.dto.salary.deductiondetail.DeductionDetailAddReqDTO;
import com.salary.admin.model.dto.salary.deductiondetail.DeductionDetailQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryDeductionDetail;
import com.salary.admin.model.entity.salary.SalaryDeductionType;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.deductiondetail.DeductionDetailVO;
import com.salary.admin.service.salary.ISalaryDeductionDetailService;
import com.salary.admin.service.salary.ISalaryDeductionTypeService;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import com.salary.admin.service.salary.ISalaryPeriodService;
import com.salary.admin.utils.UserContextUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工扣款明细表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
/**
 * 扣款明细服务实现类
 *
 * 继承 MyBatis-Plus 的 ServiceImpl，获得基础 CRUD 能力，
 * 并实现业务接口 ISalaryDeductionDetailService，封装扣款明细的业务逻辑。
 */
@Service
@Slf4j
public class SalaryDeductionDetailServiceImpl extends ServiceImpl<SalaryDeductionDetailExtMapper, SalaryDeductionDetail> implements ISalaryDeductionDetailService {
    @Resource
    private  SalaryDeductionDetailExtMapper salaryDeductionDetailExtMapper;
    @Resource
    private DeductionDetailConvert deductionDetailConvert; // MapStruct 转换器，用于 DTO ↔ Entity ↔ VO 转换

    @Resource
    private ISalaryPeriodService periodService; // 薪资周期服务，用于校验周期是否存在

    @Resource
    private ISalaryDeductionTypeService deductionTypeService; // 扣款类型服务，用于校验扣款类型是否存在

    @Resource
    private ISalaryEmployeeService employeeService; // 员工服务，用于获取员工信息

    @Value("${salary.delete.allow-physical:false}")
    private boolean allowPhysicalDelete; // 配置项：是否允许物理删除

    /**
     * 新增扣款明细
     * 1. 校验薪资周期是否存在
     * 2. 校验扣款类型是否存在
     * 3. DTO 转换为实体并保存
     * @param reqDTO 新增请求参数
     * @return 新增记录的主键 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addDeductionDetail(DeductionDetailAddReqDTO reqDTO) {
        SalaryPeriod period = periodService.getById(reqDTO.getPeriodId());
        if (period == null){
            throw new BusinessException("关联的薪资周期不存在");
        }
        SalaryDeductionType type = deductionTypeService.getById(reqDTO.getDeductionTypeId());
        if (type == null){
            throw new BusinessException("关联的扣款类型不存在");
        }

        SalaryDeductionDetail entity = deductionDetailConvert.toEntity(reqDTO);
        entity.setEmployeeId(period.getEmployeeId()); // 🌟 烙印员工ID
        entity.setDeductionTypeName(type.getTypeName()); // 🌟 烙印名称
        entity.setCategoryName(type.getCategoryName() != null ? type.getCategoryName() : "未分类");

        this.save(entity);
        return entity.getId();
    }

    /**
     * 修改扣款明细
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDeductionDetail(com.salary.admin.model.dto.salary.deductiondetail.DeductionDetailUpdateReqDTO reqDTO) {
        SalaryDeductionDetail existing = this.getById(reqDTO.getId());
        if (existing == null){
            throw new BusinessException("待修改的扣款明细不存在");
        }

        SalaryPeriod period = periodService.getById(reqDTO.getPeriodId());
        if (period == null) {
            throw new BusinessException("关联的薪资周期不存在");
        }

        SalaryDeductionType type = deductionTypeService.getById(reqDTO.getDeductionTypeId());
        if (type == null) {
            throw new BusinessException("关联的扣款类型不存在");
        }

        SalaryDeductionDetail updateEntity = deductionDetailConvert.toEntity(reqDTO);
        updateEntity.setId(reqDTO.getId());
        updateEntity.setEmployeeId(period.getEmployeeId());
        updateEntity.setDeductionTypeName(type.getTypeName());
        updateEntity.setCategoryName(type.getCategoryName() != null ? type.getCategoryName() : "未分类");

        return this.updateById(updateEntity);
    }

    /**
     * 分页查询扣款明细
     * 支持按周期过滤，并批量填充扣款类型名称和员工姓名
     * @param reqDTO 查询参数
     * @return 分页结果（VO 列表）
     */
    @Override
    public PageResult<DeductionDetailVO> selectDeductionDetailPage(DeductionDetailQueryReqDTO reqDTO) {
        Page<SalaryDeductionDetail> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        LambdaQueryWrapper<SalaryDeductionDetail> wrapper = new LambdaQueryWrapper<>();

        if (reqDTO.getPeriodId() != null) {
            wrapper.eq(SalaryDeductionDetail::getPeriodId, reqDTO.getPeriodId());
        }
        wrapper.orderByDesc(SalaryDeductionDetail::getCreateTime);

        IPage<SalaryDeductionDetail> resultPage = this.page(page, wrapper);
        List<DeductionDetailVO> voList = deductionDetailConvert.toVOList(resultPage.getRecords());

        // 批量填充扩展字段（扣款类型名称、员工姓名）
        if (CollUtil.isNotEmpty(voList)) {
            List<Long> periodIds = voList.stream().map(DeductionDetailVO::getPeriodId).distinct().collect(Collectors.toList());
            List<Long> typeIds = voList.stream().map(DeductionDetailVO::getDeductionTypeId).distinct().collect(Collectors.toList());

            Map<Long, SalaryPeriod> periodMap = periodService.listByIds(periodIds).stream()
                    .collect(Collectors.toMap(SalaryPeriod::getId, p -> p));
            Map<Long, String> typeMap = deductionTypeService.listByIds(typeIds).stream()
                    .collect(Collectors.toMap(SalaryDeductionType::getId, SalaryDeductionType::getTypeName));

            List<Long> empIds = periodMap.values().stream().map(SalaryPeriod::getEmployeeId).distinct().collect(Collectors.toList());
            Map<Long, String> empNameMap = employeeService.listByIds(empIds).stream()
                    .collect(Collectors.toMap(SalaryEmployee::getId, SalaryEmployee::getEmployeeName));

            voList.forEach(vo -> {
                vo.setDeductionTypeName(typeMap.get(vo.getDeductionTypeId()));
                SalaryPeriod p = periodMap.get(vo.getPeriodId());
                if (p != null) {
                    vo.setEmployeeName(empNameMap.get(p.getEmployeeId()));
                }
            });
        }

        return PageResult.of(resultPage, voList);
    }

    /**
     * 删除扣款明细
     * 支持逻辑删除和物理删除，物理删除需管理员权限且配置允许
     * @param id 主键 ID
     * @param logicalDelete 是否逻辑删除
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id, boolean logicalDelete) {
        if (logicalDelete) return this.removeById(id);

        if (!allowPhysicalDelete || !UserContextUtil.isAdmin()) {
            throw new BusinessException("权限不足或环境受限：禁止物理删除扣款流水明细");
        }
        return salaryDeductionDetailExtMapper.physicalDeleteById(id) > 0;
    }

    /**
     * 批量删除扣款明细
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids, boolean logicalDelete) {
        if (CollUtil.isEmpty(ids)) return false;

        if (logicalDelete) {
            return this.removeByIds(ids);
        }

        if (!allowPhysicalDelete || !UserContextUtil.isAdmin()) {
            throw new BusinessException("权限不足或环境受限：禁止物理删除扣款流水明细");
        }

        for (Long id : ids) {
            salaryDeductionDetailExtMapper.physicalDeleteById(id);
        }
        return true;
    }
}

