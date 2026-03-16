package com.salary.admin.service.salary;


import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.period.PeriodAddReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodBatchInitReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodEditReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.salary.period.PeriodOptionVO;
import com.salary.admin.model.vo.salary.period.PeriodVO;

import java.util.List;

/**
 * <p>
 * 薪资周期信息表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
public interface ISalaryPeriodService extends IService<SalaryPeriod> {

    /**
     * 新增薪资周期
     *
     * @param reqDTO 新增请求对象
     * @return 新生成的周期ID
     */
    Long addPeriod(PeriodAddReqDTO reqDTO);

    /**
     * 修改薪资周期信息
     *
     * @param reqDTO 修改请求对象
     * @return 修改结果
     */
    boolean editPeriod(PeriodEditReqDTO reqDTO);

    /**
     * 分页查询薪资周期列表 (包含员工姓名填充)
     *
     * @param reqDTO 分页查询请求对象
     * @return 分页结果封装
     */
    PageResult<PeriodVO> selectPeriodPage(PeriodQueryReqDTO reqDTO);

    /**
     * 获取薪资周期详情
     *
     * @param id 周期主键ID
     * @return 周期视图对象
     */
    PeriodVO getPeriodDetail(Long id);

    /**
     * 删除薪资周期 (逻辑/物理双模式)
     *
     * @param id            主键ID
     * @param logicalDelete 是否逻辑删除 (true: 逻辑, false: 物理)
     * @return 是否成功
     */
    boolean deletePeriodById(Long id, boolean logicalDelete);

    /**
     * 批量删除薪资周期 (逻辑/物理双模式)
     *
     * @param ids           主键ID列表
     * @param logicalDelete 是否逻辑删除
     * @return 是否成功
     */
    boolean deletePeriodByIds(List<Long> ids, boolean logicalDelete);

    /**
     * 仅批量初始化薪资周期 (剥离汇总逻辑)
     * @param reqDTO 初始化请求对象
     * @return 成功生成的周期实体列表 (返回给 Engine 联动使用)
     */
    List<SalaryPeriod> batchInitPeriodsOnly(PeriodBatchInitReqDTO reqDTO);

    /**
     * 获取去重后的结算月份下拉列表
     * @return 简易选项列表 (settlementMonth -> YYYYMM)
     */
    List<PeriodOptionVO> listOption();
}
