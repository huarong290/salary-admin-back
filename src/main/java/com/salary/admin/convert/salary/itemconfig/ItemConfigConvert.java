package com.salary.admin.convert.salary.itemconfig;

import com.salary.admin.model.dto.salary.itemconfig.ItemConfigAddReqDTO;
import com.salary.admin.model.dto.salary.itemconfig.ItemConfigEditReqDTO;
import com.salary.admin.model.entity.salary.SalaryItemConfig;
import com.salary.admin.model.vo.salary.itemconfig.SalaryItemConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 薪资项目配置 实体转换接口
 * 使用 MapStruct 自动生成实现类，确保字段映射的高性能与准确性
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemConfigConvert {

    /**
     * 新增 DTO 转 实体
     */
    SalaryItemConfig toEntity(ItemConfigAddReqDTO dto);

    /**
     * 修改 DTO 转 实体
     */
    SalaryItemConfig toEntity(ItemConfigEditReqDTO dto);

    /**
     * 实体 转 视图对象 (VO)
     */
    SalaryItemConfigVO toVO(SalaryItemConfig entity);

    /**
     * 实体列表 转 视图对象列表 (用于分页查询返回)
     */
    List<SalaryItemConfigVO> toVOList(List<SalaryItemConfig> list);

}
