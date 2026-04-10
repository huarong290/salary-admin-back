package com.salary.admin.mapper.ext;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.admin.mapper.auto.SalaryKpiRecordMapper;
import com.salary.admin.model.dto.salary.kpi.KpiRecordQueryReqDTO;
import com.salary.admin.model.vo.salary.kpi.SalaryKpiRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 员工月度绩效考核记录表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-04-04
 */
@Mapper
public interface SalaryKpiRecordExtMapper extends SalaryKpiRecordMapper {

    /**
     * 自定义 XML 连表分页查询绩效记录
     * * @param page 分页对象
     * @param req 查询参数
     * @return 包含员工基础信息的绩效 VO 列表
     */
    IPage<SalaryKpiRecordVO> selectKpiRecordPage(Page<SalaryKpiRecordVO> page, @Param("req") KpiRecordQueryReqDTO req);
}
