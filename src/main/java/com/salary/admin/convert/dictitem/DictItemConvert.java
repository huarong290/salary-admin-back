package com.salary.admin.convert.dictitem;

import com.salary.admin.model.dto.dictitem.DictItemAddReqDTO;
import com.salary.admin.model.entity.sys.SysDictItem;
import com.salary.admin.model.vo.dictitem.DictItemVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 系统字典项转换器
 * 处理具体枚举值（如：USDT、CNY）的转换。
 * 注意：字典项转换通常用于前端下拉框渲染及业务枚举匹配。
 * * @author system
 * @since 2026-03-20
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictItemConvert {

    /**
     * 将字典项保存请求 DTO 转换为实体类
     */
    SysDictItem toEntity(DictItemAddReqDTO req);

    /**
     * 将字典项实体转换为 VO，支持前端下拉框动态展示
     */
    DictItemVO toVO(SysDictItem entity);
}
