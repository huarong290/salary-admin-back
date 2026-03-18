package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.imcomedetail.IncomeDetailAddReqDTO;
import com.salary.admin.model.dto.salary.imcomedetail.IncomeDetailQueryReqDTO;
import com.salary.admin.model.dto.salary.imcomedetail.IncomeDetailUpdateReqDTO;
import com.salary.admin.model.entity.salary.SalaryIncomeDetail;
import com.salary.admin.model.vo.salary.incomedetail.IncomeDetailVO;

import java.util.List;

/**
 * <p>
 * 员工收入明细表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
public interface ISalaryIncomeDetailService extends IService<SalaryIncomeDetail> {

    /**
     * 新增收入明细
     * @param reqDTO 新增请求参数
     * @return 新增记录的主键 ID
     */
    Long addIncomeDetail(IncomeDetailAddReqDTO reqDTO);

    /**
     * 修改收入明细
     * @param reqDTO 修改请求参数 (必须包含主键 ID)
     * @return 是否修改成功
     */
    boolean updateIncomeDetail(IncomeDetailUpdateReqDTO reqDTO);

    /**
     * 分页查询收入明细
     * @param reqDTO 查询参数（包含页码、页大小、周期 ID 等）
     * @return 分页结果（VO 列表）
     */
    PageResult<IncomeDetailVO> selectIncomeDetailPage(IncomeDetailQueryReqDTO reqDTO);

    /**
     * 删除收入明细
     * @param id 主键 ID
     * @param logicalDelete 是否逻辑删除（true=逻辑删除，false=物理删除）
     * @return 是否删除成功
     */
    boolean deleteById(Long id, boolean logicalDelete);

    /**
     * 批量删除收入明细
     * @param ids 主键 ID 集合
     * @param logicalDelete 是否逻辑删除（true=逻辑删除，false=物理删除）
     * @return 是否删除成功
     */
    boolean deleteByIds(List<Long> ids, boolean logicalDelete);
}

