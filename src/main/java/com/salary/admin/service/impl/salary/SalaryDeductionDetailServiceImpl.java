package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
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
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.salary.deductiondetail.DeductionDetailVO;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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
        entity.setCurrency(reqDTO.getCurrency()); // 记录原币种

        // 🌟 统一调用内部方法进行多币种核算
        recalculateAmount(entity, reqDTO.getOriginalAmount(), reqDTO.getExchangeRate());

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
        updateEntity.setCurrency(reqDTO.getCurrency()); // 允许修改币种

        // 🌟 核心修复：修改时必须重新核算本币金额
        recalculateAmount(updateEntity, reqDTO.getOriginalAmount(), reqDTO.getExchangeRate());
        return this.updateById(updateEntity);
    }


    /**
     * 分页查询扣款明细 (企业级重构版)
     * 所有的多表关联查询、字段回填（员工名、扣款类型、结算月份）以及条件过滤，
     * 均已下推至底层 XML (selectDeductionDetailByPage) 交由数据库执行，极大降低了 JVM 内存消耗。
     * * @param reqDTO 查询参数
     * @return 分页结果（VO 列表）
     */
    @Override
    public PageResult<DeductionDetailVO> selectDeductionDetailByPage(DeductionDetailQueryReqDTO reqDTO) {
        // 1. 构造 MyBatis-Plus 分页对象 (注意泛型直接用 VO)
        Page<DeductionDetailVO> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 2. 直接调用我们在 ExtMapper 中定义好的 XML 关联查询方法
        // 这里的 salaryDeductionDetailExtMapper 底层会拦截并自动加上 LIMIT 进行物理分页
        Page<DeductionDetailVO> resultPage = salaryDeductionDetailExtMapper.selectDeductionDetailByPage(page, reqDTO);

        // 3. 直接包装返回，告别手动 for 循环拼装！
        return PageResult.of(resultPage);
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

    /**
     * 核心：重新核算本币金额
     * 逻辑：折合本币金额 = 原币金额 * 汇率，保留2位小数，四舍五入
     */
    private void recalculateAmount(SalaryDeductionDetail entity, BigDecimal originalAmount, BigDecimal exchangeRate) {
        if (originalAmount != null && exchangeRate != null) {
            BigDecimal calculatedAmount = originalAmount.multiply(exchangeRate)
                    .setScale(2, RoundingMode.HALF_UP);
            entity.setAmount(calculatedAmount); // 引擎最终认准的本币金额
            entity.setOriginalAmount(originalAmount);
            entity.setExchangeRate(exchangeRate);
        }
    }
}

