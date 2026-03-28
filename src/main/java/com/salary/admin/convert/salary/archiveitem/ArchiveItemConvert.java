package com.salary.admin.convert.salary.archiveitem;

import com.salary.admin.model.dto.salary.archiveitem.ArchiveItemReqDTO;
import com.salary.admin.model.entity.salary.SalaryArchiveItem;
import com.salary.admin.model.vo.salary.archiveitem.SalaryArchiveItemVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 薪资档案固定项转换器
 *
 * 负责薪资档案中具体科目的映射，如：基本工资、交通补贴、五险一金等。
 * 包含从录入 DTO 到数据库 Entity，以及从 Entity 到展示 VO 的双向转换。
 */
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArchiveItemConvert {

    /**
     * 将新增 DTO 转换为数据库实体
     * MapStruct 会自动映射同名属性：itemType, typeId, calcType, baseAmount, amount, ratio
     * * @param addDTO 前端传入的明细配置
     * @return 准备入库的实体对象
     */
    SalaryArchiveItem toEntity(ArchiveItemReqDTO addDTO);

    /**
     * 将实体对象转换为 VO 用于前端展示
     * 如果后期增加了冗余字段（如 itemName, categoryName），也会在此处自动完成映射
     * * @param entity 数据库实体
     * @return 视图对象
     */
    SalaryArchiveItemVO toVO(SalaryArchiveItem entity);

    /**
     * 批量转换实体列表
     * 常用于查看薪资档案详情时，展示所有的固定薪资项
     */
    List<SalaryArchiveItemVO> toVOList(List<SalaryArchiveItem> list);
}
