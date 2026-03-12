package com.salary.admin.service.impl.salary;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.incometype.IncomeTypeConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.salary.SalaryIncomeTypeExtMapper;
import com.salary.admin.model.dto.salary.imcometype.IncomeTypeAddReqDTO;
import com.salary.admin.model.dto.salary.imcometype.IncomeTypeEditReqDTO;
import com.salary.admin.model.dto.salary.imcometype.IncomeTypeQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryIncomeType;
import com.salary.admin.model.vo.salary.incometype.IncomeTypeVO;
import com.salary.admin.service.salary.ISalaryIncomeTypeService;
import com.salary.admin.utils.UserContextUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 收入类型字典表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@Service
@Slf4j
public class SalaryIncomeTypeServiceImpl extends ServiceImpl<SalaryIncomeTypeExtMapper, SalaryIncomeType> implements ISalaryIncomeTypeService {
    @Resource
    private  SalaryIncomeTypeExtMapper  salaryIncomeTypeExtMapper;

    @Resource
    private IncomeTypeConvert incomeTypeConvert;
    /**
     * 物理删除安全开关 (通过配置文件管理)
     */
    @Value("${salary.delete.allow-physical:false}")
    private boolean allowPhysicalDelete;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addIncomeType(IncomeTypeAddReqDTO reqDTO) {
        // 1. 业务校验：类型编码(TypeCode)在系统中必须全局唯一
        checkUniqueCode(reqDTO.getTypeCode(), null);
        // 2. DTO 转 Entity 并保存
        SalaryIncomeType entity = incomeTypeConvert.toEntity(reqDTO);
        this.save(entity);
        log.info("新增收入类型成功: {}, ID: {}", reqDTO.getTypeName(), entity.getId());
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editIncomeType(IncomeTypeEditReqDTO reqDTO) {
        // 1. 存在性检查
        if (!this.exists(new LambdaQueryWrapper<SalaryIncomeType>().eq(SalaryIncomeType::getId, reqDTO.getId()))) {
            throw new BusinessException("收入类型不存在");
        }
        // 2. 修改时同样校验编码唯一性 (排除当前记录)
        checkUniqueCode(reqDTO.getTypeCode(), reqDTO.getId());
        // 3. 转换并更新
        SalaryIncomeType entity = incomeTypeConvert.toEntity(reqDTO);
        return this.updateById(entity);
    }

    @Override
    public PageResult<IncomeTypeVO> selectIncomeTypePage(IncomeTypeQueryReqDTO reqDTO) {
        // 1. 构造分页参数
        Page<SalaryIncomeType> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        // 2. 构造查询条件
        LambdaQueryWrapper<SalaryIncomeType> wrapper = new LambdaQueryWrapper<>();
        // 模糊搜索：支持根据编码或名称搜索
        if (StrUtil.isNotBlank(reqDTO.getKeyword())) {
            wrapper.and(q -> q.like(SalaryIncomeType::getTypeCode, reqDTO.getKeyword())
                    .or()
                    .like(SalaryIncomeType::getTypeName, reqDTO.getKeyword())
                    .or()
                    // 🌟 新增：支持拼音缩写搜索
                    .like(SalaryIncomeType::getPinyinCode, reqDTO.getKeyword()));
        }
        // 🌟 核心调整：优先按排序值升序，再按创建时间降序
        wrapper.orderByAsc(SalaryIncomeType::getSortValue)
                .orderByDesc(SalaryIncomeType::getCreateTime);
        // 3. 执行查询
        IPage<SalaryIncomeType> resultPage = this.page(page, wrapper);
        // 4. 使用 PageResult 静态方法包装并转换 VO 列表返回
        return PageResult.of(resultPage, incomeTypeConvert.toVOList(resultPage.getRecords()));
    }

    @Override
    public IncomeTypeVO getIncomeTypeDetail(Long id) {
        // 1. 获取实体记录
        SalaryIncomeType entity = this.getById(id);

        // 2. 转换为 VO 输出，若不存在则返回 null
        return entity != null ? incomeTypeConvert.toVO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id, boolean logicalDelete) {
        // 1. 逻辑删除：使用 MyBatis-Plus 自动处理字段更新
        if (logicalDelete) {
            return this.removeById(id);
        }
        // 2. 物理删除：执行安全校验后再调用 ExtMapper SQL
        validateDeleteAuth();
        log.warn("用户 {} 正在执行物理删除收入类型，目标ID: {}", UserContextUtil.getUsername(), id);
        return salaryIncomeTypeExtMapper.physicalDeleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids, boolean logicalDelete) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }

        // 1. 批量逻辑删除
        if (logicalDelete) {
            return this.removeByIds(ids);
        }

        // 2. 批量物理删除
        validateDeleteAuth();
        log.warn("用户 {} 正在批量物理删除收入类型，目标IDs: {}", UserContextUtil.getUsername(), ids);
        return salaryIncomeTypeExtMapper.physicalDeleteByIds(ids) > 0;
    }
    /**
     * 内部私有方法：校验 TypeCode 的全局唯一性
     */
    private void checkUniqueCode(String code, Long excludeId) {
        LambdaQueryWrapper<SalaryIncomeType> wrapper = new LambdaQueryWrapper<SalaryIncomeType>()
                .eq(SalaryIncomeType::getTypeCode, code);
        // 如果是修改场景，需要排除自身 ID
        if (excludeId != null) {
            wrapper.ne(SalaryIncomeType::getId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException("收入类型编码 [" + code + "] 已存在");
        }
    }
    /**
     * 内部私有方法：物理删除前的双重安全校验 (环境锁 + 权限锁)
     */
    private void validateDeleteAuth() {
        if (!allowPhysicalDelete) {
            throw new BusinessException("安全策略限制：当前环境禁止物理删除字典数据");
        }
        if (!UserContextUtil.isAdmin()) {
            throw new BusinessException("权限不足：只有超级管理员可执行物理删除操作");
        }
    }
}
