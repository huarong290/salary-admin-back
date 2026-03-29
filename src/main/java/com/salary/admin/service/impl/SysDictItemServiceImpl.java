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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * <p>
 * 系统字典项表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-20
 */
@Service
@Slf4j
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemExtMapper, SysDictItem> implements ISysDictItemService {

    @Autowired
    private DictItemConvert dictItemConvert;

    @Autowired
    private IRedisService redisService;
    // 缓存前缀
    private static final String DICT_CACHE_KEY = "sys:dict:list:";
    // 空缓存过期时间（秒），防止击穿
    private static final long EMPTY_CACHE_EXPIRE = 300L;

    @Override
    public List<DictItemVO> selectDictItemsByTypeCode(String dictTypeCode) {
        if (StringUtils.isBlank(dictTypeCode)) {
            return new ArrayList<>();
        }

        String cacheKey = DICT_CACHE_KEY + dictTypeCode;

        // 1. 优先从 Redis 获取列表
        List<DictItemVO> cacheList = redisService.getList(cacheKey, DictItemVO.class);

        // 💡 优化：判断非空且非空集合直接返回
        if (cacheList != null && !cacheList.isEmpty()) {
            return cacheList;
        }

        // 🚀 核心优化：引入双重检查锁 (Double-Check Locking) 机制
        // 使用 dictTypeCode 的 intern() 作为锁对象，确保相同类型的字典查询在并发时排队
        synchronized (dictTypeCode.intern()) {
            // 再次从 Redis 获取，防止在等待锁的过程中，前一个线程已经把数据存入缓存
            cacheList = redisService.getList(cacheKey, DictItemVO.class);
            if (cacheList != null && !cacheList.isEmpty()) {
                return cacheList;
            }

            // 2. 缓存不存在，查询数据库
            // 💡 增加日志，便于生产环境监控缓存击穿情况
            log.info("🚀 缓存失效，正在从数据库加载字典: [{}]", dictTypeCode);

            List<SysDictItem> entities = this.list(new LambdaQueryWrapper<SysDictItem>()
                    .eq(SysDictItem::getDictTypeCode, dictTypeCode)
                    .eq(SysDictItem::getStatus, 1) // 只查启用的
                    .orderByAsc(SysDictItem::getDictItemSort));

            List<DictItemVO> voList = entities.stream()
                    .map(dictItemConvert::toVO)
                    .collect(Collectors.toList());

            // 3. 存入 Redis (不设置过期时间，由增删改主动触发删除)
            if (CollectionUtils.isEmpty(voList)) {
                //  防止缓存穿透：即便数据库没数据，也存入一个空集合并设置短期过期 (300s)
                // 这样短时间内相同的恶意请求不会再次穿透到 DB
                redisService.setEx(cacheKey, Collections.emptyList(), EMPTY_CACHE_EXPIRE, SECONDS);
            } else {
                // 正常存入，不设过期时间，由 add/edit/delete 方法通过 cleanCacheAfterCommit 主动失效
                redisService.set(cacheKey, voList);
            }
            return voList;
        }
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
        // 事务提交后清理缓存
        cleanCacheAfterCommit(reqDTO.getDictTypeCode());

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
        // 2. 处理可能变更的 TypeCode 导致的两头缓存不一致
        String oldTypeCode = existing.getDictTypeCode();
        String newTypeCode = reqDTO.getDictTypeCode();
        boolean success = this.updateById(entity);
        if (success) {
            // 3. 稳妥的缓存清理策略：无论 typeCode 是否改变，把涉及到的都清了
            cleanCacheAfterCommit(oldTypeCode);
            if (!StrUtil.equals(oldTypeCode, newTypeCode)) {
                cleanCacheAfterCommit(newTypeCode);
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
            typeCodes.forEach(this::cleanCacheAfterCommit);
        }
        return success;
    }

    @Override
    public String getDictLabel(String dictTypeCode, String dictItemValue) {
        // 💡 这里的 Perfect 处理是利用 selectDictItemsByTypeCode 的缓存机制
        // 1. 获取该类型下的所有项 (走 Redis 缓存)
        List<DictItemVO> items = this.selectDictItemsByTypeCode(dictTypeCode);

        // 2. 查找匹配项 这样即使多次调用获取 Label，也只会触发一次 Redis/DB 查询
        return items.stream()
                .filter(item -> StrUtil.equals(item.getDictItemValue(), dictItemValue))
                .map(DictItemVO::getDictItemLabel)
                .findFirst()
                .orElse(dictItemValue); // 找不到返回原值，方便排查数据问题
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictItemById(Long id) {
        SysDictItem item = this.getById(id);
        if (item == null) {
            return false;
        }
        if (this.removeById(id)) {
            cleanCacheAfterCommit(item.getDictTypeCode());
            return true;
        }
        return false;
    }

    /**
     * 辅助方法：确保在事务成功提交后才删除缓存
     */
    private void cleanCacheAfterCommit(String typeCode) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    redisService.del(DICT_CACHE_KEY + typeCode);
                }
            });
        } else {
            redisService.del(DICT_CACHE_KEY + typeCode);
        }
    }
}