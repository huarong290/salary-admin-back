package com.salary.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.model.dto.dictitem.DictItemAddReqDTO;
import com.salary.admin.model.dto.dictitem.DictItemUpdateReqDTO;
import com.salary.admin.model.entity.sys.SysDictItem;
import com.salary.admin.model.vo.dictitem.DictItemVO;

import java.util.List;

/**
 * <p>
 * 系统字典项表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-20
 */
public interface ISysDictItemService extends IService<SysDictItem> {

    /**
     * 根据字典类型编码获取所有启用的字典项
     * 建议实现类加入 Redis 缓存：key = "sys:dict:list:{dictTypeCode}"
     *
     * @param dictTypeCode 字典类型编码 (如: currency_type)
     * @return 字典项列表
     */
    List<DictItemVO> selectDictItemsByTypeCode(String dictTypeCode);

    /**
     * 新增字典项
     *
     * @param reqDTO 字典项参数
     * @return 新生成的 ID
     */
    Long addDictItem(DictItemAddReqDTO reqDTO);

    /**
     * 修改字典项
     * 更新成功后需刷新对应类型的 Redis 缓存列表
     *
     * @param reqDTO 修改参数
     * @return 是否成功
     */
    boolean editDictItem(DictItemUpdateReqDTO reqDTO);
    /**
     * 删除单个字典项
     */
    boolean deleteDictItemById(Long id);
    /**
     * 批量删除字典项
     *
     * @param ids ID 列表
     * @return 是否成功
     */
    boolean deleteDictItemByIds(List<Long> ids);

    /**
     * 根据类型编码和键值获取标签名 (内部业务逻辑转换使用)
     *
     * @param dictTypeCode 字典类型编码
     * @param dictItemValue 字典项键值
     * @return 标签名 (如: 泰达币)
     */
    String getDictLabel(String dictTypeCode, String dictItemValue);
}
