package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.calcrule.CalcRuleAddReqDTO;
import com.salary.admin.model.dto.calcrule.CalcRuleEditReqDTO;
import com.salary.admin.model.dto.calcrule.CalcRuleQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryCalcRule;
import com.salary.admin.model.vo.calcrule.CalcRuleVO;

import java.util.List;

/**
 * <p>
 * 薪资计算规则库表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
public interface ISalaryCalcRuleService extends IService<SalaryCalcRule> {

    // ======================== 1. 新增操作 (Create) ========================
    /**
     * 新增薪资计算规则
     *
     * @param reqDTO 新增请求对象
     * @return 新生成的规则ID
     */
    Long addRule(CalcRuleAddReqDTO reqDTO);
    // ======================== 2. 删除操作 (Delete) ========================
    /**
     * 删除规则 (逻辑/物理双模式)
     *
     * @param id 主键ID
     * @param logicalDelete 是否逻辑删除
     * @return 是否成功
     */
    boolean deleteRuleById(Long id, boolean logicalDelete);
    /**
     * 批量删除规则 (逻辑/物理双模式)
     *
     * @param ids 主键ID列表
     * @param logicalDelete 是否逻辑删除
     * @return 是否成功
     */
    boolean deleteRuleByIds(List<Long> ids, boolean logicalDelete);
    // ======================== 3. 修改操作 (Update) ========================
    /**
     * 修改薪资计算规则
     *
     * @param reqDTO 修改请求对象
     * @return 修改结果
     */
    boolean editRule(CalcRuleEditReqDTO reqDTO);
    // ======================== 4. 查询操作 (Read) ========================
    /**
     * 分页查询薪资计算规则
     *
     * @param reqDTO 查询条件及分页参数
     * @return 分页结果对象
     */
    PageResult<CalcRuleVO> getCalcRulePage(CalcRuleQueryReqDTO reqDTO);

    /**
     * 获取规则详情
     *
     * @param id 规则ID
     * @return 规则视图对象
     */
    CalcRuleVO getRuleDetail(Long id);

    /**
     * 获取所有启用状态的规则列表
     * 通常用于计算引擎初始化缓存
     *
     * @return 活跃规则列表
     */
    List<CalcRuleVO> listActiveRules();
}
