package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.archive.ArchiveAddReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryArchive;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;

/**
 * <p>
 * 员工薪资标准配置表(含版本历史) 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-13
 */

public interface ISalaryArchiveService extends IService<SalaryArchive> {

    /**
     * 分页查询员工薪资档案列表
     * 通常需要关联 salary_employee 表获取员工姓名和工号
     */
    PageResult<SalaryArchiveVO> selectArchivePage(ArchiveQueryReqDTO queryReq);

    /**
     * 提交定薪/调薪方案 (核心业务)
     * 包含：闭合旧版本、生成新版本、保存动态明细项
     * @param saveReq 包含主表及明细项的请求对象
     */
    boolean createNewSalaryVersion(ArchiveAddReqDTO saveReq);

    /**
     * 获取员工当前正在生效的薪资档案详情
     * @param employeeId 员工ID
     */
    SalaryArchiveVO getCurrentArchive(Long employeeId);

    /**
     * 获取指定版本的档案详情（含明细项列表）
     * @param archiveId 档案主键ID
     */
    SalaryArchiveVO getArchiveDetail(Long archiveId);

    /**
     * 撤销/删除最新版本 (仅限最新版本且未被核算引用时)
     */
    boolean revokeLatestVersion(Long employeeId);
}
