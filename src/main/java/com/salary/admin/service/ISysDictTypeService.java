package com.salary.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.dicttype.DictTypeAddReqDTO;
import com.salary.admin.model.dto.dicttype.DictTypeQueryReqDTO;
import com.salary.admin.model.dto.dicttype.DictTypeUpdateReqDTO;
import com.salary.admin.model.entity.sys.SysDictType;
import com.salary.admin.model.vo.dicttype.DictTypeVO;

/**
 * <p>
 * 系统字典类型表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-20
 */

public interface ISysDictTypeService extends IService<SysDictType> {

    /**
     * 分页查询字典类型
     *
     * @param reqDTO 查询条件
     * @return 分页结果对象
     */
    PageResult<DictTypeVO> selectDictTypePage(DictTypeQueryReqDTO reqDTO);

    /**
     * 新增字典类型
     *
     * @param reqDTO 新增参数
     * @return 新生成的 ID
     */
    Long addDictType(DictTypeAddReqDTO reqDTO);

    /**
     * 修改字典类型
     *
     * @param reqDTO 修改参数
     * @return 是否成功
     */
    boolean editDictType(DictTypeUpdateReqDTO reqDTO);

    /**
     * 删除字典类型 (含校验：若该类型下存在字典项，则不允许删除)
     *
     * @param id 字典类型主键 ID
     * @return 是否成功
     */
    boolean deleteDictTypeById(Long id);
}