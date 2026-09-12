package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryConfigExtMapper;
import com.salary.admin.model.dto.salary.config.SalaryConfigAddReqDTO;
import com.salary.admin.model.dto.salary.config.SalaryConfigEditReqDTO;
import com.salary.admin.model.dto.salary.config.SalaryConfigQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryConfig;
import com.salary.admin.model.vo.salary.config.SalaryConfigVO;
import com.salary.admin.service.salary.ISalaryConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 薪资系统全局配置表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryConfigServiceImpl extends ServiceImpl<SalaryConfigExtMapper, SalaryConfig> implements ISalaryConfigService {

    private final SalaryConfigExtMapper salaryConfigExtMapper;

    // ======================== 1. 新增操作 (Create) ========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addConfig(SalaryConfigAddReqDTO reqDTO) {
        // 1. 唯一性校验：配置键全局唯一 (逻辑删除范围内)
        long exists = this.lambdaQuery()
                .eq(SalaryConfig::getConfigKey, reqDTO.getConfigKey())
                .count();
        if (exists > 0) {
            throw new BusinessException("配置键 [" + reqDTO.getConfigKey() + "] 已存在，请勿重复创建");
        }

        // 2. 组装实体
        SalaryConfig entity = new SalaryConfig()
                .setConfigKey(reqDTO.getConfigKey())
                .setConfigName(reqDTO.getConfigName())
                .setConfigValue(reqDTO.getConfigValue())
                .setActiveFlag(reqDTO.getActiveFlag() != null ? reqDTO.getActiveFlag() : 1)
                .setRemark(reqDTO.getRemark());
        // 3. 落库
        this.save(entity);
        log.info("新增薪资全局配置: key={}, name={}", entity.getConfigKey(), entity.getConfigName());
        return entity.getId();
    }

    // ======================== 2. 修改操作 (Update) ========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editConfig(SalaryConfigEditReqDTO reqDTO) {
        SalaryConfig existing = this.getById(reqDTO.getId());
        if (existing == null) {
            throw new BusinessException("配置项不存在或已被删除");
        }
        // 配置键只读，防止业务依赖断裂
        existing.setConfigName(reqDTO.getConfigName())
                .setConfigValue(reqDTO.getConfigValue())
                .setActiveFlag(reqDTO.getActiveFlag() != null ? reqDTO.getActiveFlag() : existing.getActiveFlag())
                .setRemark(reqDTO.getRemark());
        return this.updateById(existing);
    }

    // ======================== 3. 查询操作 (Read) ========================
    @Override
    public PageResult<SalaryConfigVO> pageQuery(SalaryConfigQueryReqDTO reqDTO) {
        IPage<SalaryConfig> pageParam = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        LambdaQueryWrapper<SalaryConfig> wrapper = new LambdaQueryWrapper<SalaryConfig>()
                .like(StringUtils.isNotBlank(reqDTO.getConfigName()), SalaryConfig::getConfigName, reqDTO.getConfigName())
                .like(StringUtils.isNotBlank(reqDTO.getConfigKey()), SalaryConfig::getConfigKey, reqDTO.getConfigKey())
                .eq(reqDTO.getActiveFlag() != null, SalaryConfig::getActiveFlag, reqDTO.getActiveFlag())
                .orderByDesc(SalaryConfig::getCreateTime);
        IPage<SalaryConfig> page = this.page(pageParam, wrapper);
        List<SalaryConfigVO> voList = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(page, voList);
    }

    @Override
    public List<SalaryConfigVO> listAllActive() {
        return this.lambdaQuery()
                .eq(SalaryConfig::getActiveFlag, 1)
                .orderByAsc(SalaryConfig::getId)
                .list()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public String getValueByKey(String configKey) {
        if (StringUtils.isBlank(configKey)) {
            return null;
        }
        SalaryConfig config = this.lambdaQuery()
                .eq(SalaryConfig::getConfigKey, configKey)
                .eq(SalaryConfig::getActiveFlag, 1)
                .one();
        return config != null ? config.getConfigValue() : null;
    }

    /**
     * 实体转 VO
     */
    private SalaryConfigVO toVO(SalaryConfig entity) {
        SalaryConfigVO vo = new SalaryConfigVO();
        vo.setId(entity.getId());
        vo.setConfigKey(entity.getConfigKey());
        vo.setConfigValue(entity.getConfigValue());
        vo.setConfigName(entity.getConfigName());
        vo.setConfigType(entity.getConfigType());
        vo.setConfigGroup(entity.getConfigGroup());
        vo.setActiveFlag(entity.getActiveFlag());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
