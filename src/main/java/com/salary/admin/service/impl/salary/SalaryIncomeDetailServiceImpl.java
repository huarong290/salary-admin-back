package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.incomedetail.IncomeDetailConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.salary.SalaryIncomeDetailExtMapper;
import com.salary.admin.model.dto.salary.imcomedetail.IncomeDetailAddReqDTO;
import com.salary.admin.model.dto.salary.imcomedetail.IncomeDetailQueryReqDTO;
import com.salary.admin.model.dto.salary.imcomedetail.IncomeDetailUpdateReqDTO;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.model.entity.salary.SalaryIncomeDetail;
import com.salary.admin.model.entity.salary.SalaryIncomeType;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.salary.incomedetail.IncomeDetailVO;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import com.salary.admin.service.salary.ISalaryIncomeDetailService;
import com.salary.admin.service.salary.ISalaryIncomeTypeService;
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
 * 员工收入明细表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
/**
 * 收入明细服务实现类
 *
 * 继承 MyBatis-Plus 的 ServiceImpl，获得基础 CRUD 能力，
 * 并实现业务接口 ISalaryIncomeDetailService，封装收入明细的业务逻辑。
 */
@Service
@Slf4j
public class SalaryIncomeDetailServiceImpl
        extends ServiceImpl<SalaryIncomeDetailExtMapper, SalaryIncomeDetail>
        implements ISalaryIncomeDetailService {

    @Resource
    private IncomeDetailConvert incomeDetailConvert; // DTO ↔ Entity ↔ VO 转换器

    @Resource
    private ISalaryPeriodService periodService; // 薪资周期服务

    @Resource
    private ISalaryIncomeTypeService incomeTypeService; // 收入类型服务

    @Resource
    private ISalaryEmployeeService employeeService; // 员工服务

    @Value("${salary.delete.allow-physical:false}")
    private boolean allowPhysicalDelete; // 是否允许物理删除（配置项）
    @Resource
    private SalaryIncomeDetailExtMapper salaryIncomeDetailExtMapper;
    /**
     * 新增收入明细 (包含完整的快照烙印逻辑)
     * 1. 校验薪资周期是否存在
     * 2. 校验收入类型是否存在
     * 3. DTO 转换为实体并保存
     * @param reqDTO 新增请求参数
     * @return 新增记录的主键 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addIncomeDetail(IncomeDetailAddReqDTO reqDTO) {
        // 1. 查周期，获取归属的员工ID
        SalaryPeriod period = periodService.getById(reqDTO.getPeriodId());
        if (period == null) {
            throw new BusinessException("关联的薪资周期不存在");
        }

        // 2. 查字典，获取名称和分类快照
        SalaryIncomeType type = incomeTypeService.getById(reqDTO.getIncomeTypeId());
        if (type == null) {
            throw new BusinessException("关联的收入类型不存在");
        }

        // 3. 组装实体并烙印数据
        SalaryIncomeDetail entity = incomeDetailConvert.toEntity(reqDTO);
        entity.setEmployeeId(period.getEmployeeId()); // 🌟 核心：自动补齐员工ID
        entity.setIncomeTypeName(type.getTypeName()); // 🌟 核心：烙印项目名称
        entity.setCategoryName(type.getCategoryName() != null ? type.getCategoryName() : "未分类");

        this.save(entity);
        return entity.getId();
    }
    /**
     * 修改收入明细
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateIncomeDetail(IncomeDetailUpdateReqDTO reqDTO) {
        // 1. 确保记录存在
        SalaryIncomeDetail existing = this.getById(reqDTO.getId());
        if (existing == null) {
            throw new BusinessException("待修改的收入明细不存在");
        }

        SalaryPeriod period = periodService.getById(reqDTO.getPeriodId());
        if (period == null) throw new BusinessException("关联的薪资周期不存在");

        SalaryIncomeType type = incomeTypeService.getById(reqDTO.getIncomeTypeId());
        if (type == null) throw new BusinessException("关联的收入类型不存在");

        // 2. 拷贝新值并重新烙印
        SalaryIncomeDetail updateEntity = incomeDetailConvert.toEntity(reqDTO);
        updateEntity.setId(reqDTO.getId()); // 确保 ID 不丢失
        updateEntity.setEmployeeId(period.getEmployeeId());
        updateEntity.setIncomeTypeName(type.getTypeName());
        updateEntity.setCategoryName(type.getCategoryName() != null ? type.getCategoryName() : "未分类");

        return this.updateById(updateEntity);
    }

    /**
     * 分页查询收入明细
     * 支持按周期过滤，并批量填充收入类型名称和员工姓名
     * @param reqDTO 查询参数
     * @return 分页结果（VO 列表）
     */
    @Override
    public PageResult<IncomeDetailVO> selectIncomeDetailPage(IncomeDetailQueryReqDTO reqDTO) {
        Page<SalaryIncomeDetail> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        LambdaQueryWrapper<SalaryIncomeDetail> wrapper = new LambdaQueryWrapper<>();

        if (reqDTO.getPeriodId() != null) {
            wrapper.eq(SalaryIncomeDetail::getPeriodId, reqDTO.getPeriodId());
        }
        wrapper.orderByDesc(SalaryIncomeDetail::getCreateTime);

        IPage<SalaryIncomeDetail> resultPage = this.page(page, wrapper);
        List<IncomeDetailVO> voList = incomeDetailConvert.toVOList(resultPage.getRecords());

        // 批量填充扩展字段（收入类型名称、员工姓名）
        if (CollUtil.isNotEmpty(voList)) {
            List<Long> periodIds = voList.stream().map(IncomeDetailVO::getPeriodId).distinct().collect(Collectors.toList());
            List<Long> typeIds = voList.stream().map(IncomeDetailVO::getIncomeTypeId).distinct().collect(Collectors.toList());

            Map<Long, SalaryPeriod> periodMap = periodService.listByIds(periodIds).stream()
                    .collect(Collectors.toMap(SalaryPeriod::getId, p -> p));
            Map<Long, String> typeMap = incomeTypeService.listByIds(typeIds).stream()
                    .collect(Collectors.toMap(SalaryIncomeType::getId, SalaryIncomeType::getTypeName));

            List<Long> empIds = periodMap.values().stream().map(SalaryPeriod::getEmployeeId).distinct().collect(Collectors.toList());
            Map<Long, String> empNameMap = employeeService.listByIds(empIds).stream()
                    .collect(Collectors.toMap(SalaryEmployee::getId, SalaryEmployee::getEmployeeName));

            voList.forEach(vo -> {
                vo.setIncomeTypeName(typeMap.get(vo.getIncomeTypeId()));
                SalaryPeriod p = periodMap.get(vo.getPeriodId());
                if (p != null) {
                    vo.setEmployeeName(empNameMap.get(p.getEmployeeId()));
                }
            });
        }

        return PageResult.of(resultPage, voList);
    }

    /**
     * 删除收入明细
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
            throw new BusinessException("权限不足或环境受限：禁止物理删除薪资流水明细");
        }
        return salaryIncomeDetailExtMapper.physicalDeleteById(id) > 0;
    }

    /**
     * 批量删除收入明细
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids, boolean logicalDelete) {
        if (CollUtil.isEmpty(ids)) {
            return false;
        }

        if (logicalDelete) {
            // MyBatis-Plus 自带的逻辑批量删除
            return this.removeByIds(ids);
        }

        // 物理批量删除
        if (!allowPhysicalDelete || !UserContextUtil.isAdmin()) {
            throw new BusinessException("权限不足或环境受限：禁止物理删除薪资流水明细");
        }

        // 循环调用自定义的物理删除 mapper，或者在 Mapper 层写一个 foreach 批量物理删除
        for (Long id : ids) {
            salaryIncomeDetailExtMapper.physicalDeleteById(id);
        }
        return true;
    }
}

