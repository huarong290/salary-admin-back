package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.calcrule.CalcRuleConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryCalcRuleExtMapper;
import com.salary.admin.model.dto.salary.calcrule.CalcRuleAddReqDTO;
import com.salary.admin.model.dto.salary.calcrule.CalcRuleEditReqDTO;
import com.salary.admin.model.dto.salary.calcrule.CalcRuleQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcRule;
import com.salary.admin.model.vo.calcrule.CalcRuleVO;
import com.salary.admin.service.salary.ISalaryCalcRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 薪资计算规则库表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryCalcRuleServiceImpl extends ServiceImpl<SalaryCalcRuleExtMapper, SalaryCalcRule> implements ISalaryCalcRuleService {

    private final SalaryCalcRuleExtMapper salaryCalcRuleExtMapper;
    //  注入 MapStruct 转换器
    private final CalcRuleConvert calcRuleConvert;

    // ======================== 1. 新增操作 (Create) ========================
    @Override
    public Long addRule(CalcRuleAddReqDTO reqDTO) {
        // 1. 业务校验：防重判定 (规则编码在全系统必须唯一)
        long count = this.count(new LambdaQueryWrapper<SalaryCalcRule>()
                .eq(SalaryCalcRule::getRuleCode, reqDTO.getRuleCode())
                .eq(SalaryCalcRule::getDeleteFlag, 0L));
        if (count > 0) {
            throw new BusinessException("规则编码已存在，请更换：" + reqDTO.getRuleCode());
        }

        // 2. DTO 转 Entity (使用 MapStruct)
        SalaryCalcRule rule = calcRuleConvert.toEntity(reqDTO);

        // 3. 落盘并返回 ID
        this.save(rule);
        log.info("新增薪资规则成功: [{}] {}", rule.getRuleCode(), rule.getRuleName());
        return rule.getId();
    }
    // ======================== 2. 删除操作 (Delete) ========================
    @Override
    public boolean deleteRuleById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            //触发 MP 逻辑删除，会自动执行: UPDATE ... SET delete_flag = id WHERE id = ?
            return this.removeById(id);
        }
        //  触发 ExtMapper 物理删除，绕过 MP 的逻辑删除机制
        return salaryCalcRuleExtMapper.physicalDeleteById(id) > 0;
    }

    @Override
    public boolean deleteRuleByIds(List<Long> ids, boolean logicalDelete) {
        if (CollectionUtils.isEmpty(ids)){
            return false;
        }
        if (logicalDelete) {
            return this.removeByIds(ids); // MP 自动批处理逻辑删除
        }
        return salaryCalcRuleExtMapper.physicalDeleteByIds(ids) > 0;
    }
    // ======================== 3. 修改操作 (Update) ========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editRule(CalcRuleEditReqDTO reqDTO) {
        SalaryCalcRule existRule = this.getById(reqDTO.getId());
        if (existRule == null) {
            throw new BusinessException("该计算规则不存在或已被删除！");
        }

        // 修改了编码需校验唯一性
        if (StringUtils.isNotBlank(reqDTO.getRuleCode()) && !existRule.getRuleCode().equals(reqDTO.getRuleCode())) {
            long count = this.count(new LambdaQueryWrapper<SalaryCalcRule>()
                    .eq(SalaryCalcRule::getRuleCode, reqDTO.getRuleCode()));
            if (count > 0) {
                throw new BusinessException("新的规则编码已被其他规则占用！");
            }
        }

        SalaryCalcRule updateEntity = calcRuleConvert.toEntity(reqDTO);
        // updateTime, updateBy 由 MybatisPlusHandler 自动填充
        return this.updateById(updateEntity);
    }

    // ======================== 4. 查询操作 (Read) ========================

    @Override
    public PageResult<CalcRuleVO> getCalcRulePage(CalcRuleQueryReqDTO reqDTO) {
        // 1. 构建分页对象 (MyBatis-Plus 标准分页)
        Page<SalaryCalcRule> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<SalaryCalcRule> wrapper = new LambdaQueryWrapper<>();

        // 模糊搜索：支持根据 规则名称 或 规则编码 搜索
        if (StringUtils.isNotBlank(reqDTO.getKeyword())) {
            wrapper.and(w -> w.like(SalaryCalcRule::getRuleName, reqDTO.getKeyword())
                    .or()
                    .like(SalaryCalcRule::getRuleCode, reqDTO.getKeyword()));
        }

        // 分类筛选 (如果 DTO 中有 category 字段)
        // wrapper.eq(reqDTO.getCategory() != null, SalaryCalcRule::getCategory, reqDTO.getCategory());

        // 状态筛选
        wrapper.eq(reqDTO.getStatus() != null, SalaryCalcRule::getStatus, reqDTO.getStatus());

        // 建议的 MyBatis-Plus 查询条件
        wrapper.orderByAsc(SalaryCalcRule::getStage)         // 第一顺位：按核算阶段排序 (基础->补贴->扣款->税)
                .orderByAsc(SalaryCalcRule::getSortValue)     // 第二顺位：阶段内按 sort_value 排序
                .orderByDesc(SalaryCalcRule::getUpdateTime);  // 第三顺位：同阶段同权重时，最新修改的排前面
        // 3. 执行查询
        IPage<SalaryCalcRule> resultPage = this.page(page, wrapper);

        // 4. 将 Entity 分页对象转换为 VO 分页对象返回
        return PageResult.of(resultPage, calcRuleConvert.toVOList(resultPage.getRecords()));
    }

    @Override
    public CalcRuleVO getRuleDetail(Long id) {
        SalaryCalcRule entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException("未找到对应的计算规则详情！");
        }
        return calcRuleConvert.toVO(entity);
    }

    @Override
    public List<CalcRuleVO> listActiveRules() {
        // 引擎核心调度逻辑：只加载状态为“启用”的规则
        List<SalaryCalcRule> activeEntities = this.lambdaQuery()
                .eq(SalaryCalcRule::getStatus, 1) // 假设 1 为启用
                .orderByAsc(SalaryCalcRule::getRuleCode)
                .list();

        return calcRuleConvert.toVOList(activeEntities);
    }

    @Override
    public CalcRuleVO getByRuleCode(String ruleCode) {
        if (StringUtils.isBlank(ruleCode)) {
            return null;
        }

        // 利用 LambdaQueryWrapper 查询唯一记录
        SalaryCalcRule entity = this.lambdaQuery()
                .eq(SalaryCalcRule::getRuleCode, ruleCode)
                .one(); // 因为数据库有 uk_rule_code_del 唯一约束，用 one() 没问题

        if (entity == null) {
            return null;
        }
        return calcRuleConvert.toVO(entity);
    }
}
