package com.salary.admin.convert.dicttype;


import com.salary.admin.model.dto.dicttype.DictTypeAddReqDTO;
import com.salary.admin.model.entity.sys.SysDictType;
import com.salary.admin.model.vo.dicttype.DictTypeVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 系统字典类型转换器
 * 处理字典分类（如：currency_type）的核心转换逻辑。
 * * @author system
 * @since 2026-03-20
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictTypeConvert {

    /**
     * 将字典类型保存请求 DTO 转换为实体类
     */
    SysDictType toEntity(DictTypeAddReqDTO req);

    /**
     * 将字典类型实体转换为 VO，用于后台管理列表展示
     */
    DictTypeVO toVO(SysDictType entity);
}