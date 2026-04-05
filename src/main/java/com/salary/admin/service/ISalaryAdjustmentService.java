package com.salary.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.adjustment.AdjustmentAddReqDTO;
import com.salary.admin.model.dto.adjustment.AdjustmentEditReqDTO;
import com.salary.admin.model.dto.adjustment.AdjustmentQueryDTO;
import com.salary.admin.model.entity.salary.SalaryAdjustment;

import java.util.List;

/**
 * <p>
 * 薪资周期专项调整表 (处理各类动态奖金与扣款) 服务类
 * </p>
 *
 * @author system
 * @since 2026-04-05
 */
public interface ISalaryAdjustmentService extends IService<SalaryAdjustment> {

    /**
     * 新增调整项 (自动折算本币)
     */
    Long addAdjustment(AdjustmentAddReqDTO reqDTO);

    /**
     * 编辑调整项 (状态机保护)
     */
    boolean editAdjustment(AdjustmentEditReqDTO reqDTO);

    /**
     * 批量删除调整项 (状态机保护)
     */
    boolean deleteAdjustments(List<Long> ids);

    /**
     * 批量修改生效状态
     */
    boolean auditAdjustments(List<Long> ids, Integer targetStatus);

    /**
     * 分页查询
     */
    PageResult<SalaryAdjustment> pageQuery(AdjustmentQueryDTO queryDTO);
}