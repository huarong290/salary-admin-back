package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.deductiondetail.DeductionDetailAddReqDTO;
import com.salary.admin.model.dto.salary.deductiondetail.DeductionDetailQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryDeductionDetail;
import com.salary.admin.model.vo.deductiondetail.DeductionDetailVO;

/**
 *
 * <p>
 * 扣款明细服务接口
 * </p>
 * 定义扣款明细模块的业务方法，供 ServiceImpl 实现。
 * 继承 MyBatis-Plus 的 IService<T>，获得基础 CRUD 能力。
 * @author system
 * @since 2026-03-11
 */
public interface ISalaryDeductionDetailService extends IService<SalaryDeductionDetail> {

    /**
     * 新增扣款明细
     * @param reqDTO 新增请求参数
     * @return 新增记录的主键 ID
     */
    Long addDeductionDetail(DeductionDetailAddReqDTO reqDTO);

    /**
     * 分页查询扣款明细
     * @param reqDTO 查询参数（包含页码、页大小、过滤条件等）
     * @return 分页结果（VO 列表）
     */
    PageResult<DeductionDetailVO> selectDeductionDetailPage(DeductionDetailQueryReqDTO reqDTO);

    /**
     * 删除扣款明细
     * @param id 主键 ID
     * @param logicalDelete 是否逻辑删除（true=逻辑删除，false=物理删除）
     * @return 是否删除成功
     */
    boolean deleteById(Long id, boolean logicalDelete);
}

