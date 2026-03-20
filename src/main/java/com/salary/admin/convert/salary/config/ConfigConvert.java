package com.salary.admin.convert.salary.config;

import com.salary.admin.model.dto.salary.config.SalaryConfigAddReqDTO;
import com.salary.admin.model.dto.salary.config.SalaryConfigEditReqDTO;
import com.salary.admin.model.entity.salary.SalaryConfig;
import com.salary.admin.model.vo.salary.config.SalaryConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 薪资系统全局配置转换器
 * 处理系统参数（如结算币种、计算精度等）的 DTO/Entity/VO 转换。
 * * @author system
 * @since 2026-03-20
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigConvert {

    /**
     * 将保存请求 DTO 转换为配置实体
     */
    SalaryConfig toEntity(SalaryConfigAddReqDTO req);

    /**
     * Edit DTO 转 Entity
     */
    SalaryConfig toEntity(SalaryConfigEditReqDTO reqDTO);

    /**
     * 将配置实体转换为视图对象，用于前端页面展示
     */
    SalaryConfigVO toVO(SalaryConfig entity);
}
