package com.salary.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.dicttype.DictTypeConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SysDictTypeExtMapper;
import com.salary.admin.model.dto.dicttype.DictTypeAddReqDTO;
import com.salary.admin.model.dto.dicttype.DictTypeQueryReqDTO;
import com.salary.admin.model.dto.dicttype.DictTypeUpdateReqDTO;
import com.salary.admin.model.entity.sys.SysDictItem;
import com.salary.admin.model.entity.sys.SysDictType;
import com.salary.admin.model.vo.dicttype.DictTypeVO;
import com.salary.admin.service.ISysDictItemService;
import com.salary.admin.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统字典类型表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-20
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeExtMapper, SysDictType> implements ISysDictTypeService {

    @Autowired
    private DictTypeConvert dictTypeConvert;

    @Autowired
    private ISysDictItemService dictItemService;

    @Override
    public PageResult<DictTypeVO> selectDictTypePage(DictTypeQueryReqDTO reqDTO) {
        // 1. 构建 MyBatis-Plus 分页对象
        Page<SysDictType> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 2. 执行分页查询
        this.page(page, new LambdaQueryWrapper<SysDictType>()
                .like(StrUtil.isNotBlank(reqDTO.getDictTypeName()), SysDictType::getDictTypeName, reqDTO.getDictTypeName())
                .eq(StrUtil.isNotBlank(reqDTO.getDictTypeCode()), SysDictType::getDictTypeCode, reqDTO.getDictTypeCode())
                .eq(reqDTO.getStatus() != null, SysDictType::getStatus, reqDTO.getStatus())
                .orderByDesc(SysDictType::getCreateTime));

        // 3. 将 Entity 转换为 VO 列表
        List<DictTypeVO> voList = page.getRecords().stream()
                .map(dictTypeConvert::toVO)
                .collect(Collectors.toList());

        // 4. 使用你提供的 PageResult.of 场景 2 进行封装
        return PageResult.of(page, voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addDictType(DictTypeAddReqDTO reqDTO) {
        // 校验唯一性
        long count = this.count(new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictTypeCode, reqDTO.getDictTypeCode()));
        if (count > 0) {
            throw new BusinessException("字典类型编码已存在，请勿重复添加");
        }

        SysDictType entity = dictTypeConvert.toEntity(reqDTO);
        this.save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editDictType(DictTypeUpdateReqDTO reqDTO) { // 🌟 入参变更为 UpdateReqDTO
        SysDictType existing = this.getById(reqDTO.getId());
        if (existing == null) {
            throw new BusinessException("待修改的字典类型不存在");
        }

        // 🌟 核心防坑：校验修改后的编码是否与【其他】字典冲突 (排除自己)
        long count = this.count(new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictTypeCode, reqDTO.getDictTypeCode())
                .ne(SysDictType::getId, reqDTO.getId())); // 排除当前 ID
        if (count > 0) {
            throw new BusinessException("字典类型编码已被其他字典使用，请更换");
        }

        SysDictType entity = dictTypeConvert.toEntity(reqDTO);
        entity.setId(reqDTO.getId()); // 确保 ID 赋值
        return this.updateById(entity);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictTypeById(Long id) {
        SysDictType type = this.getById(id);
        if (type == null) {
            return false;
        }

        // 🌟 核心校验：检查该类型下是否已有字典项
        long count = dictItemService.count(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictTypeCode, type.getDictTypeCode()));

        if (count > 0) {
            throw new RuntimeException("该字典类型下已存在字典项，无法删除！");
        }

        return this.removeById(id);
    }
}