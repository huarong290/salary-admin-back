package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.deductiontype.DeductionTypeAddReqDTO;
import com.salary.admin.model.dto.salary.deductiontype.DeductionTypeEditReqDTO;
import com.salary.admin.model.dto.salary.deductiontype.DeductionTypeQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryDeductionType;
import com.salary.admin.model.vo.salary.deductiontype.DeductionTypeVO;

import java.util.List;

/**
 * <p>
 * 扣款类型字典表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
public interface ISalaryDeductionTypeService extends IService<SalaryDeductionType> {

    /**
     * 新增扣款类型
     * @param reqDTO 新增请求参数
     * @return 新增记录的主键 ID
     */
    Long addDeductionType(DeductionTypeAddReqDTO reqDTO);

    /**
     * 编辑扣款类型
     * @param reqDTO 编辑请求参数
     * @return 是否更新成功
     */
    boolean editDeductionType(DeductionTypeEditReqDTO reqDTO);

    /**
     * 分页查询扣款类型
     * @param reqDTO 查询参数（包含页码、页大小、关键字等）
     * @return 分页结果（VO 列表）
     */
    PageResult<DeductionTypeVO> selectDeductionTypePage(DeductionTypeQueryReqDTO reqDTO);

    /**
     * 获取扣款类型详情
     * @param id 主键 ID
     * @return VO 对象
     */
    DeductionTypeVO getDeductionTypeDetail(Long id);

    /**
     * 删除单条记录
     * @param id 主键 ID
     * @param logicalDelete 是否逻辑删除（true=逻辑删除，false=物理删除）
     * @return 是否删除成功
     */
    boolean deleteById(Long id, boolean logicalDelete);

    /**
     * 批量删除记录
     * @param ids 主键 ID 列表
     * @param logicalDelete 是否逻辑删除（true=逻辑删除，false=物理删除）
     * @return 是否删除成功
     */
    boolean deleteByIds(List<Long> ids, boolean logicalDelete);
}
