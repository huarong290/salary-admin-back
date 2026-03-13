package com.salary.admin.mapper.ext.salary;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.admin.mapper.auto.salary.SalaryArchiveMapper;
import com.salary.admin.model.dto.salary.archive.ArchiveQueryReqDTO;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 员工薪资标准配置表(含版本历史)扩展 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-03-13
 */
@Mapper
public interface SalaryArchiveExtMapper extends SalaryArchiveMapper {

    /**
     * 联表分页查询薪资档案列表（包含员工基本信息）
     *
     * @param page 分页对象
     * @param req  查询条件DTO
     * @return 分页VO数据
     */
    Page<SalaryArchiveVO> selectArchivePage(Page<SalaryArchiveVO> page, @Param("req") ArchiveQueryReqDTO req);

    /**
     * 根据档案ID获取完整薪资档案详情（含明细）
     */
    SalaryArchiveVO getArchiveDetailById(@Param("archiveId") Long archiveId);

    /**
     * 根据员工ID获取当前最新生效的薪资档案详情
     */
    SalaryArchiveVO getLatestArchiveByEmployeeId(@Param("employeeId") Long employeeId);
}