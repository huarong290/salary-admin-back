package com.salary.admin.convert.salary.archive;

import com.salary.admin.model.dto.salary.archive.ArchiveAddReqDTO;
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
     * 用于调薪申请：将申请 DTO 转换为新的档案实体版本
     */
    SalaryArchive toEntity(ArchiveAddReqDTO req);

    /**
     * 用于核算引擎获取当前生效档案：将实体转换为 VO 供引擎计算使用
     */
    SalaryArchiveVO toVO(SalaryArchive entity);
}
