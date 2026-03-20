package com.salary.admin.service.impl.salary;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.config.ConfigConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.salary.SalaryConfigExtMapper;
import com.salary.admin.model.dto.salary.config.SalaryConfigAddReqDTO;
import com.salary.admin.model.dto.salary.config.SalaryConfigEditReqDTO;
import com.salary.admin.model.dto.salary.config.SalaryConfigQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryConfig;
import com.salary.admin.model.vo.salary.config.SalaryConfigVO;
import com.salary.admin.service.IRedisService;
import com.salary.admin.service.salary.ISalaryConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 薪资系统全局配置表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-20
 */
@Service
public class SalaryConfigServiceImpl extends ServiceImpl<SalaryConfigExtMapper, SalaryConfig> implements ISalaryConfigService {

    @Autowired
    private ConfigConvert configConvert;
    @Autowired
    private IRedisService redisService;

    private static final String CONFIG_CACHE_PREFIX = "sys:config:val:";
    private static final String SETTLEMENT_KEY = "SETTLEMENT_CURRENCY";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addConfig(SalaryConfigAddReqDTO reqDTO) {
        // 1. 唯一性校验：不允许重复的 ConfigKey
        long count = this.count(new LambdaQueryWrapper<SalaryConfig>()
                .eq(SalaryConfig::getConfigKey, reqDTO.getConfigKey()));

        if (count > 0) {
            throw new RuntimeException("配置键 [" + reqDTO.getConfigKey() + "] 已存在，请勿重复添加");
        }

        // 2. 转换为实体并保存
        SalaryConfig entity = configConvert.toEntity(reqDTO);
        // 设置默认值（如需）
        if (entity.getActiveFlag() == null) {
            entity.setActiveFlag(1); // 默认激活
        }

        this.save(entity);

        // 3. 此时无需清理缓存，因为是新 Key，缓存中本来就没有
        return entity.getId();
    }
    @Override
    public List<SalaryConfigVO> selectAllConfigs() {
        return this.list().stream()
                .map(configConvert::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public String getConfigValue(String configKey) {
        String cacheKey = CONFIG_CACHE_PREFIX + configKey;
        // 1. 尝试从 Redis 获取
        String value = redisService.get(cacheKey, String.class);
        if (value != null) {
            return value;
        }
        // 2. 缓存缺失，查库
        SalaryConfig config = this.getOne(new LambdaQueryWrapper<SalaryConfig>()
                .eq(SalaryConfig::getConfigKey, configKey)
                .eq(SalaryConfig::getActiveFlag, 1));

        if (config != null) {
            // 3. 回填缓存 (过期时间设为 24 小时)
            redisService.setEx(cacheKey, config.getConfigValue(), 24, TimeUnit.HOURS);
            return config.getConfigValue();
        }else{
            // 🌟 防缓存穿透：存入空串，有效期 5 分钟
            redisService.setEx(cacheKey, "", 5, TimeUnit.MINUTES);
            return "";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfig(SalaryConfigEditReqDTO reqDTO) {
        // 🌟 1. 必须先查出旧数据，因为我们需要拿到 configKey 去删缓存
        SalaryConfig existing = this.getById(reqDTO.getId());
        if (existing == null) {
            throw new BusinessException("待修改的配置项不存在！");
        }

        // 2. 执行更新
        SalaryConfig entity = configConvert.toEntity(reqDTO);
        entity.setId(reqDTO.getId()); // 确保 ID 赋值
        boolean success = this.updateById(entity);

        // 3. 更新成功后，精准清理 Redis 缓存
        if (success) {
            redisService.del(CONFIG_CACHE_PREFIX + existing.getConfigKey());
        }
        return success;
    }



    @Override
    public String getCurrentSettlementCurrency() {
        return this.getConfigValue(SETTLEMENT_KEY);
    }

    @Override
    public PageResult<SalaryConfigVO> selectConfigByPage(SalaryConfigQueryReqDTO reqDTO) {
        // 1. 构建分页对象
        Page<SalaryConfig> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 2. 构建查询条件 (使用 StrUtil 处理空字符串)
        LambdaQueryWrapper<SalaryConfig> queryWrapper = new LambdaQueryWrapper<SalaryConfig>()
                .like(StrUtil.isNotBlank(reqDTO.getConfigName()), SalaryConfig::getConfigName, reqDTO.getConfigName())
                .like(StrUtil.isNotBlank(reqDTO.getConfigKey()), SalaryConfig::getConfigKey, reqDTO.getConfigKey())
                .eq(reqDTO.getActiveFlag() != null, SalaryConfig::getActiveFlag, reqDTO.getActiveFlag())
                .orderByDesc(SalaryConfig::getUpdateTime); // 按更新时间倒序

        // 3. 执行查询
        this.page(page, queryWrapper);

        // 4. 流式转换：Entity -> VO
        List<SalaryConfigVO> voList = page.getRecords().stream()
                .map(configConvert::toVO)
                .collect(Collectors.toList());

        // 5. 封装统一分页返回结果
        return PageResult.of(page, voList);
    }
}
