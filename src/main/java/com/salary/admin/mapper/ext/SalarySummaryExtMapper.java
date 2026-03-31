package com.salary.admin.mapper.ext;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.salary.admin.mapper.auto.SalarySummaryMapper;
import com.salary.admin.model.dto.salary.summary.SummaryQueryReqDTO;
import com.salary.admin.model.vo.salary.summary.SalarySummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 薪资汇总与结算表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Mapper
public interface SalarySummaryExtMapper extends SalarySummaryMapper {

    /**
     * 自定义分页查询（不查询 detail_json 提高列表加载速度）
     */
    IPage<SalarySummaryVO> selectSummaryPage(IPage<SalarySummaryVO> page, @Param("query") SummaryQueryReqDTO query);

}
