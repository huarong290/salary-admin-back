package com.salary.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.convert.dictitem.DictItemConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SysDictItemExtMapper;
import com.salary.admin.model.dto.dictitem.DictItemAddReqDTO;
import com.salary.admin.model.dto.dictitem.DictItemUpdateReqDTO;
import com.salary.admin.model.entity.sys.SysDictItem;
import com.salary.admin.model.vo.dictitem.DictItemVO;
import com.salary.admin.service.IRedisService;
import com.salary.admin.service.ISysDictItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统字典项表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-20
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemExtMapper, SysDictItem> implements ISysDictItemService {

    @Autowired
    private DictItemConvert dictItemConvert;

    @Autowired
    private IRedisService redisService;

    private static final String DICT_CACHE_KEY = "sys:dict:list:";

    @Override
    public List<DictItemVO> selectDictItemsByTypeCode(String dictTypeCode) {
        if (StrUtil.isBlank(dictTypeCode)) {
            return new java.util.ArrayList<>();
        }

        String cacheKey = DICT_CACHE_KEY + dictTypeCode;
        // 1. 优先从 Redis 获取列表
        List<DictItemVO> cacheList = redisService.get(cacheKey, List.class);
        if (cacheList != null) {
            return cacheList;
        }

        // 2. 缓存不存在，查询数据库
        List<SysDictItem> entities = this.list(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictTypeCode, dictTypeCode)
                .eq(SysDictItem::getStatus, 1) // 只查启用的
                .orderByAsc(SysDictItem::getSort));

        List<DictItemVO> voList = entities.stream()
                .map(dictItemConvert::toVO)
                .collect(Collectors.toList());

        // 3. 存入 Redis (不设置过期时间，由增删改主动触发删除)
        if (!voList.isEmpty()) {
            redisService.set(cacheKey, voList);
        }
        return voList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addDictItem(DictItemAddReqDTO reqDTO) {
        // 1. 唯一性校验：同一字典类型下，键值不能重复
        long count = this.count(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictTypeCode, reqDTO.getDictTypeCode())
                .eq(SysDictItem::getDictItemValue, reqDTO.getDictItemValue()));
        if (count > 0) {
            throw new BusinessException("该字典类型下已存在相同的键值，请勿重复添加");
        }
        SysDictItem entity = dictItemConvert.toEntity(reqDTO);
        this.save(entity);
        // 2. 清理对应类型的缓存
        redisService.del(DICT_CACHE_KEY + reqDTO.getDictTypeCode());
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editDictItem(DictItemUpdateReqDTO reqDTO) {
        SysDictItem existing = this.getById(reqDTO.getId());
        if (existing == null) {
            throw new BusinessException("待修改的字典项不存在");
        }

        // 1. 唯一性校验：排除自身后，同一字典类型下，键值不能重复
        long count = this.count(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictTypeCode, reqDTO.getDictTypeCode())
                .eq(SysDictItem::getDictItemValue, reqDTO.getDictItemValue())
                .ne(SysDictItem::getId, reqDTO.getId()));
        if (count > 0) {
            throw new BusinessException("该字典类型下已存在相同的键值，请更换键值");
        }
        SysDictItem entity = dictItemConvert.toEntity(reqDTO);
        entity.setId(reqDTO.getId()); // 确保赋予 ID
        // 2. 🌟 处理可能变更的 TypeCode 导致的两头缓存不一致
        String oldTypeCode = existing.getDictTypeCode();
        String newTypeCode = reqDTO.getDictTypeCode();
        boolean success = this.updateById(entity);
        if (success) {
            // 3. 稳妥的缓存清理策略：无论 typeCode 是否改变，把涉及到的都清了
            redisService.del(DICT_CACHE_KEY + oldTypeCode);
            if (!StrUtil.equals(oldTypeCode, newTypeCode)) {
                redisService.del(DICT_CACHE_KEY + newTypeCode);
            }
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictItemByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        // 1. 获取这些 ID 涉及到的所有 typeCode，用于后续精准清理缓存
        List<SysDictItem> list = this.listByIds(ids);
        Set<String> typeCodes = list.stream()
                .map(SysDictItem::getDictTypeCode)
                .collect(Collectors.toSet());

        // 2. 执行批量删除
        boolean success = this.removeByIds(ids);

        // 3. 批量删除成功后，清理受影响的 typeCode 缓存
        if (success) {
            typeCodes.forEach(code -> redisService.del(DICT_CACHE_KEY + code));
        }
        return success;
    }

    @Override
    public String getDictLabel(String dictTypeCode, String dictItemValue) {
        // 💡 这里的 Perfect 处理是利用 selectDictItemsByTypeCode 的缓存机制
        // 这样即使多次调用获取 Label，也只会触发一次 Redis/DB 查询
        return this.selectDictItemsByTypeCode(dictTypeCode).stream()
                .filter(item -> StrUtil.equals(item.getDictItemValue(), dictItemValue))
                .map(DictItemVO::getDictItemLabel)
                .findFirst()
                .orElse(dictItemValue); // 找不到则返回原 Value
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictItemById(Long id) {
        SysDictItem item = this.getById(id);
        if (item != null) {
            this.removeById(id);
            redisService.del(DICT_CACHE_KEY + item.getDictTypeCode());
            return true;
        }
        return false;
    }
}