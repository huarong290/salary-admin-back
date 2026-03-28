package com.salary.admin.service.impl.salary;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.itemconfig.ItemConfigConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryItemConfigExtMapper;
import com.salary.admin.model.dto.salary.itemconfig.ItemConfigAddReqDTO;
import com.salary.admin.model.dto.salary.itemconfig.ItemConfigEditReqDTO;
import com.salary.admin.model.dto.salary.itemconfig.ItemConfigQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryItemConfig;
import com.salary.admin.model.vo.salary.itemconfig.SalaryItemConfigVO;
import com.salary.admin.service.IRedisService;
import com.salary.admin.service.ISysDictItemService;
import com.salary.admin.service.salary.ISalaryItemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 薪资项目统一配置表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryItemConfigServiceImpl extends ServiceImpl<SalaryItemConfigExtMapper, SalaryItemConfig> implements ISalaryItemConfigService {

    private final  SalaryItemConfigExtMapper salaryItemConfigExtMapper;
    private final ItemConfigConvert itemConfigConvert;
    private final IRedisService redisService;

    private static final String CACHE_KEY_LIST = "salary:item:active:list";
    private static final String CACHE_KEY_MAP = "salary:item:active:map";

    // 在类成员处注入字典服务
    private final ISysDictItemService iSysDictItemService;
    // ======================== 1. 新增操作 ========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addItemConfig(ItemConfigAddReqDTO reqDTO) {
        // [1] 唯一性校验：业务编码和引擎变量名
        checkUnique(reqDTO.getItemCode(), reqDTO.getEnvVarName(), null);

        // [2]使用 MapStruct 代替 BeanUtil
        SalaryItemConfig entity = itemConfigConvert.toEntity(reqDTO);

        // [3] 自动生成拼音简码 (用于前端 Select 快速过滤)
        if (StrUtil.isBlank(entity.getPinyinCode())) {
            entity.setPinyinCode(PinyinUtil.getFirstLetter(entity.getItemName(), ""));
        }

        this.save(entity);
        clearCache();
        return entity.getId();
    }

    // ======================== 2. 修改操作 ========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editItemConfig(ItemConfigEditReqDTO reqDTO) {
        SalaryItemConfig existing = this.getById(reqDTO.getId());
        if (existing == null) throw new BusinessException("薪资项目不存在");

        // [1] 逻辑保护：如果是系统固定项(fixed_flag=1)，禁止修改 item_code 和 env_var_name
        if (existing.getFixedFlag() == 1) {
            if (!StrUtil.equals(existing.getItemCode(), reqDTO.getItemCode()) ||
                    !StrUtil.equals(existing.getEnvVarName(), reqDTO.getEnvVarName())) {
                throw new BusinessException("系统固定项禁止修改编码或变量名");
            }
        }

        // [2] 校验变量名冲突 (排除自身)
        checkUnique(reqDTO.getItemCode(), reqDTO.getEnvVarName(), reqDTO.getId());

        // 使用 MapStruct
        SalaryItemConfig entity = itemConfigConvert.toEntity(reqDTO);

        // [3]如果名称变了，同步更新拼音码
        if (!StrUtil.equals(existing.getItemName(), reqDTO.getItemName())) {
            entity.setPinyinCode(PinyinUtil.getFirstLetter(reqDTO.getItemName(), "").toLowerCase());
        }

        boolean success = this.updateById(entity);
        if (success) clearCache();
        return success;
    }

    // ======================== 3. 删除操作 ========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteItemConfig(Long id, boolean logicalDelete) {
        SalaryItemConfig existing = this.getById(id);
        if (existing == null){
            return false;
        }
        // [1] 系统固定项保护
        if (existing.getFixedFlag() == 1) {
            throw new BusinessException("系统内置项目无法删除");
        }
        // [2] 使用校验：检查该薪资项是否已在汇总表或其他业务表中使用
        if (checkItemUsage(existing.getItemCode())) {
            throw new BusinessException("薪资项目[" + existing.getItemCode() + "]已被使用，无法删除");
        }
        // [3] 删除逻辑
        boolean success;
        if (logicalDelete) {
            // 逻辑删除：需要在 SalaryItemConfig 实体类上加 @TableLogic 注解
            existing.setDeleteFlag(1);
            success = this.updateById(existing);
        } else {
            // 物理删除
            success = this.getBaseMapper().deleteById(id) > 0;
        }
        // [4] 清理缓存
        if (success) {
            clearCache();
        }
        return success;
    }

    // ======================== 4. 查询操作 ========================
    @Override
    public PageResult<SalaryItemConfigVO> selectItemConfigPage(ItemConfigQueryReqDTO reqDTO) {
        // 1. 构造 MyBatis-Plus 分页对象
        Page<SalaryItemConfigVO> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 2. 直接调用 XML 中的自定义关联查询
        // 此时返回的 VO 已经包含了 categoryLabel 等关联字段
        IPage<SalaryItemConfigVO> resultPage = salaryItemConfigExtMapper.selectItemConfigPage(page, reqDTO);

        // 3. 封装结果
        return PageResult.of(resultPage, resultPage.getRecords());
    }

    @Override
    public SalaryItemConfigVO getItemConfigDetail(Long id) {
        // 1. 查询基础实体
        SalaryItemConfig entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException("该薪资配置项不存在或已被删除");
        }

        // 2. 利用 MapStruct 转换为 VO (高性能转换 VO Mapping)
        SalaryItemConfigVO vo = itemConfigConvert.toVO(entity);

        // [3] 补全字典 Label (遵循“大厂标准”统一字典逻辑)
        // 💡 关键修改：不再动态计算 dictTypeCode，而是直接指向统一的业务细类字典
        String unifiedDictType = "salary_item_sub_type";

        // [4] 调用字典服务获取 Label。如果字典项不存在，getDictLabel 内部应处理返回空或原值
        String label = iSysDictItemService.getDictLabel(unifiedDictType, entity.getCategoryDictValue());
        vo.setCategoryLabel(label);
        // [5] 业务安全检查：如果是系统固定项，给 VO 注入特殊的提示信息
        if (entity.getFixedFlag() == 1) {
            // 可以在 VO 中增加一个字段提示前端：该项涉及核心引擎计算，部分字段不可编辑
            log.debug("读取系统内置薪资项: [{}]", entity.getItemName());
        }
        // [6]. 补充拼音码（若前端需要展示且后端未生成）
        if (StringUtils.isBlank(vo.getPinyinCode()) && StringUtils.isNotBlank(vo.getItemName())) {
            vo.setPinyinCode(PinyinUtil.getFirstLetter(vo.getItemName(), "").toLowerCase());
        }
        // [7] (可选) 架构师建议：如果该项是“固定项”，可以在详情中明确标识提示前端只读
        log.debug("查询薪资项详情成功: [{}], 分类Label: [{}]", vo.getItemName(), label);

        return vo;
    }

    @Override
    public List<SalaryItemConfig> listActiveConfigsSorted() {
        // 尝试从缓存获取
        List<SalaryItemConfig> cache = redisService.getList(CACHE_KEY_LIST, SalaryItemConfig.class);
        if(!CollectionUtils.isEmpty(cache)){
            return cache;
        }
        // 这里的查询一定要按照优先级排序，这是计算引擎正确运行的前提
        List<SalaryItemConfig> list = this.list(new LambdaQueryWrapper<SalaryItemConfig>()
                .eq(SalaryItemConfig::getStatus, 1)
                .orderByAsc(SalaryItemConfig::getCalcPriority)); // 排序非常重要
        if(!CollectionUtils.isEmpty(list)){
            redisService.set(CACHE_KEY_LIST, list);
        }
        return list;
    }

    @Override
    public Map<String, SalaryItemConfig> getEnvVarMap() {
        return listActiveConfigsSorted().stream()
                .collect(Collectors.toMap(SalaryItemConfig::getEnvVarName, Function.identity(), (k1, k2) -> k1));
    }

    @Override
    public void clearCache() {
        redisService.del(CACHE_KEY_LIST);
        redisService.del(CACHE_KEY_MAP);
        log.info("已清理薪资项目配置全局缓存");
    }

    // ======================== 辅助方法 ========================

    private void checkUnique(String itemCode, String envVarName, Long excludeId) {
        long codeCount = this.count(new LambdaQueryWrapper<SalaryItemConfig>()
                .eq(SalaryItemConfig::getItemCode, itemCode)
                .ne(excludeId != null, SalaryItemConfig::getId, excludeId));
        if (codeCount > 0) throw new BusinessException("薪资项目编码[" + itemCode + "]已存在");

        long varCount = this.count(new LambdaQueryWrapper<SalaryItemConfig>()
                .eq(SalaryItemConfig::getEnvVarName, envVarName)
                .ne(excludeId != null, SalaryItemConfig::getId, excludeId));
        if (varCount > 0) throw new BusinessException("引擎变量名[" + envVarName + "]已被占用");
    }
    /**
     * 校验薪资项目是否已被使用
     * 例如：在 salary_summary_item 或其他业务表中是否存在引用
     */
    private boolean checkItemUsage(String itemCode) {
        // 这里可以调用 Mapper 查询，比如：
        // return salarySummaryItemMapper.existsByItemCode(itemCode);
        // todo

        return false;
    }
}
