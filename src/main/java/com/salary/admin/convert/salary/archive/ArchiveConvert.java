package com.salary.admin.convert.salary.archive;

import com.salary.admin.model.dto.salary.archive.ArchiveAdjustReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveInitReqDTO;
import com.salary.admin.model.entity.salary.SalaryArchive;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 薪资档案主表转换器
 * * 处理员工薪资版本的核心转换逻辑。
 * 注意：主表通常包含 base_salary (底薪)，这是所有比例项计算的默认基数。
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArchiveConvert {

    /**
     * 用于入职定薪：将初始化 DTO 转换为新的档案实体 (V1)
     */
    SalaryArchive initToEntity(ArchiveInitReqDTO req);

    /**
     * 用于调薪申请：将调薪 DTO 转换为新的档案草稿实体 (V n+1)
     */
    SalaryArchive adjustToEntity(ArchiveAdjustReqDTO req);

    /**
     * 用于展示与核算引擎：将实体转换为 VO
     * 注意：VO 中的 employeeName, currencyLabel, archiveItems 等复杂级联字段，
     * MapStruct 无法自动查库，需在 Service 层手动回填。
     */
    SalaryArchiveVO toVO(SalaryArchive entity);
}