package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.deductiontype.DeductionTypeConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.salary.SalaryDeductionTypeExtMapper;
import com.salary.admin.model.dto.salary.deductiontype.DeductionTypeAddReqDTO;
import com.salary.admin.model.dto.salary.deductiontype.DeductionTypeEditReqDTO;
import com.salary.admin.model.dto.salary.deductiontype.DeductionTypeQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryDeductionType;
import com.salary.admin.model.vo.salary.deductiontype.DeductionTypeVO;
import com.salary.admin.service.salary.ISalaryDeductionTypeService;
import com.salary.admin.utils.UserContextUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 扣款类型字典表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@Service
@Slf4j
public class SalaryDeductionTypeServiceImpl
        extends ServiceImpl<SalaryDeductionTypeExtMapper, SalaryDeductionType>
        implements ISalaryDeductionTypeService {

    @Resource
    private SalaryDeductionTypeExtMapper salaryDeductionTypeExtMapper;

    @Resource
    private DeductionTypeConvert deductionTypeConvert;

    @Value("${salary.delete.allow-physical:false}")
    private boolean allowPhysicalDelete;

    /**
     * 新增扣款类型
     * 1. 校验编码唯一性
     * 2. DTO 转换为实体
     * 3. 保存到数据库
     * @param reqDTO 新增请求参数
     * @return 新增记录的主键 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addDeductionType(DeductionTypeAddReqDTO reqDTO) {
        checkUniqueCode(reqDTO.getTypeCode(), null);
        SalaryDeductionType entity = deductionTypeConvert.toEntity(reqDTO);
        this.save(entity);
        return entity.getId();
    }

    /**
     * 编辑扣款类型
     * 1. 校验是否存在
     * 2. 校验编码唯一性
     * 3. DTO 转换为实体并更新
     * @param reqDTO 编辑请求参数
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editDeductionType(DeductionTypeEditReqDTO reqDTO) {
        if (!this.exists(new LambdaQueryWrapper<SalaryDeductionType>()
                .eq(SalaryDeductionType::getId, reqDTO.getId()))) {
            throw new BusinessException("扣款类型不存在");
        }
        checkUniqueCode(reqDTO.getTypeCode(), reqDTO.getId());
        SalaryDeductionType entity = deductionTypeConvert.toEntity(reqDTO);
        return this.updateById(entity);
    }

    /**
     * 分页查询扣款类型
     * 支持关键字模糊查询（编码、名称）
     * @param reqDTO 查询参数
     * @return 分页结果（VO 列表）
     */
    @Override
    public PageResult<DeductionTypeVO> selectDeductionTypePage(DeductionTypeQueryReqDTO reqDTO) {
        Page<SalaryDeductionType> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        LambdaQueryWrapper<SalaryDeductionType> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(reqDTO.getKeyword())) {
            wrapper.and(q -> q.like(SalaryDeductionType::getTypeCode, reqDTO.getKeyword())
                    .or()
                    .like(SalaryDeductionType::getTypeName, reqDTO.getKeyword()));
        }

        wrapper.orderByAsc(SalaryDeductionType::getSortValue)
                .orderByDesc(SalaryDeductionType::getCreateTime);

        IPage<SalaryDeductionType> resultPage = this.page(page, wrapper);
        return PageResult.of(resultPage, deductionTypeConvert.toVOList(resultPage.getRecords()));
    }

    /**
     * 获取扣款类型详情
     * @param id 主键 ID
     * @return VO 对象
     */
    @Override
    public DeductionTypeVO getDeductionTypeDetail(Long id) {
        SalaryDeductionType entity = this.getById(id);
        return entity != null ? deductionTypeConvert.toVO(entity) : null;
    }

    /**
     * 删除单条记录
     * 支持逻辑删除和物理删除
     * @param id 主键 ID
     * @param logicalDelete 是否逻辑删除
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id, boolean logicalDelete) {
        if (logicalDelete) return this.removeById(id);
        validateDeleteAuth();
        return salaryDeductionTypeExtMapper.physicalDeleteById(id) > 0;
    }

    /**
     * 批量删除记录
     * 支持逻辑删除和物理删除
     * @param ids 主键 ID 列表
     * @param logicalDelete 是否逻辑删除
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids, boolean logicalDelete) {
        if (CollUtil.isEmpty(ids)) return false;
        if (logicalDelete) return this.removeByIds(ids);
        validateDeleteAuth();
        return salaryDeductionTypeExtMapper.physicalDeleteByIds(ids) > 0;
    }

    /**
     * 校验扣款类型编码唯一性
     * @param code 编码
     * @param excludeId 排除的 ID（编辑时用）
     */
    private void checkUniqueCode(String code, Long excludeId) {
        LambdaQueryWrapper<SalaryDeductionType> wrapper = new LambdaQueryWrapper<SalaryDeductionType>()
                .eq(SalaryDeductionType::getTypeCode, code);
        if (excludeId != null) wrapper.ne(SalaryDeductionType::getId, excludeId);
        if (this.count(wrapper) > 0) throw new BusinessException("扣款类型编码 [" + code + "] 已存在");
    }

    /**
     * 校验是否允许物理删除
     * 仅管理员且配置允许时才可执行
     */
    private void validateDeleteAuth() {
        if (!allowPhysicalDelete || !UserContextUtil.isAdmin()) {
            throw new BusinessException("受限操作：禁止物理删除字典数据");
        }
    }
}

