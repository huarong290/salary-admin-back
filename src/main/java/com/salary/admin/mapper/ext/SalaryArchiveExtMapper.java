package com.salary.admin.mapper.ext;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.salary.admin.mapper.auto.SalaryArchiveMapper;
import com.salary.admin.model.dto.salary.archive.ArchiveQueryReqDTO;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 员工薪资标准配置表(含版本历史) Mapper 扩展接口
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Mapper
public interface SalaryArchiveExtMapper extends SalaryArchiveMapper {

    IPage<SalaryArchiveVO> selectArchivePage(IPage<SalaryArchiveVO> page, @Param("req") ArchiveQueryReqDTO reqDTO);
}
