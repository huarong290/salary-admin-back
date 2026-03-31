package com.salary.admin.convert.salary.summary;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salary.admin.model.dto.salary.snapshot.SalarySnapshotDTO;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.summary.SalarySummaryVO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 薪资汇总 MapStruct 转换器
 * componentModel = "spring" 表示生成的实现类会自动加上 @Component 注解，交由 Spring 管理
 */
@Mapper(componentModel = "spring")
public abstract class SalarySummaryConvert {

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Entity -> VO
     * 将数据库实体转换为展示层对象
     */
    @Mapping(target = "details", source = "detailJson", qualifiedByName = "jsonToSnapshot")
    public abstract SalarySummaryVO toVO(SalarySummary entity);

    /**
     * 批量转换 List<Entity> -> List<VO>
     */
    public abstract List<SalarySummaryVO> toVOList(List<SalarySummary> entities);
    /**
     * 自定义解析逻辑：String -> Object
     */
    @Named("jsonToSnapshot")
    protected SalarySnapshotDTO jsonToSnapshot(String detailJson) {
        if (StringUtils.isBlank(detailJson)) {
            return null;
        }
        try {
            return objectMapper.readValue(detailJson, SalarySnapshotDTO.class);
        } catch (JsonProcessingException e) {
            // 记录日志，防止单条数据 JSON 异常导致接口报错
            return null;
        }
    }

}