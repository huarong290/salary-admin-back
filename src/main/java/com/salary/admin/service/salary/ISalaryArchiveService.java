package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.archive.ArchiveAdjustReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveAuditReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveInitReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryArchive;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;

import java.util.List;



/**
 * 员工薪资标准配置表(含版本历史) 服务类（领域化接口）
 *
 * 说明：
 * - 提供入职定薪、调薪申请、审批、获取已生效最新档案、历史列表等业务方法
 * - 所有写操作在实现层应保证事务与并发安全
 *
 * @author system
 * @since 2026-03-27
 */
public interface ISalaryArchiveService extends IService<SalaryArchive> {

    /**
     * 员工入职定薪 (初始化 V1 版本)
     *
     * @param reqDTO 定薪参数
     * @return 新建档案 ID
     */
    Long initEmployeeArchive(ArchiveInitReqDTO reqDTO);

    /**
     * 员工调薪申请 (生成 V(n+1) 草稿版本)
     *
     * @param reqDTO 调薪参数
     * @return 新档案 ID
     */
    Long adjustSalary(ArchiveAdjustReqDTO reqDTO);

    /**
     * 调薪审批处理（拉链时间切片逻辑）
     *
     * @param archiveAuditReqDTO 档案 ID（必须是草稿/待审）
     * @return 是否成功
     */
    boolean auditArchive(ArchiveAuditReqDTO archiveAuditReqDTO);

    /**
     * 获取员工当前已生效且最新的薪资档案（供计算引擎使用）
     *
     * @param employeeId 员工 ID
     * @return 薪资档案视图（若无返回 null）
     */
    SalaryArchiveVO getLatestEffectiveArchive(Long employeeId);

    /**
     * 获取员工薪资调整历史版本记录（按版本号倒序）
     *
     * @param employeeId 员工 ID
     * @return 历史版本列表
     */
    List<SalaryArchiveVO> listArchiveHistory(Long employeeId);


    /**
     * 分页查询薪资档案列表
     * * @param reqDTO 查询条件（含关键字、状态、版本标识等）
     * @return 分页结果
     */
    PageResult<SalaryArchiveVO> getArchivePage(ArchiveQueryReqDTO reqDTO);
}
