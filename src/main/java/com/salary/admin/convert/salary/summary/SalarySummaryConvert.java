package com.salary.admin.convert.salary.summary;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.salary.admin.model.dto.salary.snapshot.SalarySnapshotDTO;
import com.salary.admin.model.entity.salary.SalarySummary;
import com.salary.admin.model.vo.salary.summary.SalarySummaryVO;
import org.mapstruct.*;

import java.util.List;

/**
 * 薪资汇总 MapStruct 转换器
 * 使用 interface 配合 default 方法是最稳定的写法
 */
@Mapper(componentModel = "spring")
public interface SalarySummaryConvert {

    /**
     * Entity -> VO
     * 强制指定 source 为 detailJson
     */
    @Mapping(target = "details", source = "detailJson", qualifiedByName = "jsonToSnapshot")
    SalarySummaryVO toVO(SalarySummary entity);

    /**
     * 批量转换
     */
    List<SalarySummaryVO> toVOList(List<SalarySummary> entities);

    /**
     * 自定义解析逻辑
     * 🌟 注意：这里使用 public default，确保实现类有绝对的访问权限
     */
    @Named("jsonToSnapshot")
    default SalarySnapshotDTO jsonToSnapshot(String detailJson) {
        // 增加一行控制台强制输出，确认方法是否被触发
         System.out.println("--- MapStruct Triggered: jsonToSnapshot ---");
        if (StrUtil.isBlank(detailJson)) {
            return null;
        }
        try {
            return JSONUtil.toBean(detailJson, SalarySnapshotDTO.class);
        } catch (Exception e) {
            System.err.println("❌ JSON解析核心报错: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 🌟 收尾钩子
     */
    @AfterMapping
    default void afterToVO(SalarySummary entity, @MappingTarget SalarySummaryVO vo) {
        if (vo.getDetails() != null && vo.getDetails().getUsedArchives() != null) {
            vo.setUsedArchives(vo.getDetails().getUsedArchives());
        }
    }
}