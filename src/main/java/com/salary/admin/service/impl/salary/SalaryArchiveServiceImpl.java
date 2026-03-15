package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.salary.SalaryArchiveExtMapper;
import com.salary.admin.model.dto.salary.archive.ArchiveAddReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveAuditDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryArchive;
import com.salary.admin.model.entity.salary.SalaryArchiveItem;
import com.salary.admin.model.entity.salary.SalaryPaymentRecord;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.service.salary.ISalaryArchiveItemService;
import com.salary.admin.service.salary.ISalaryArchiveService;
import com.salary.admin.service.salary.ISalaryPaymentRecordService;
import com.salary.admin.utils.UserContextUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工薪资标准配置表(含版本历史) 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-13
 */
@Service
@Slf4j
public class SalaryArchiveServiceImpl extends ServiceImpl<SalaryArchiveExtMapper, SalaryArchive> implements ISalaryArchiveService {

    @Resource
    private ISalaryArchiveItemService iSalaryArchiveItemService;
    @Resource
    private ISalaryPaymentRecordService iSalaryPaymentRecordService;

    @Resource
    private SalaryArchiveExtMapper salaryArchiveExtMapper;

    @Override
    public PageResult<SalaryArchiveVO> selectArchivePage(ArchiveQueryReqDTO queryReq) {
        // 1. 构造 MyBatis-Plus 的分页参数对象
        Page<SalaryArchiveVO> page = new Page<>(queryReq.getPageNum(), queryReq.getPageSize());

        // 2. 执行底层扩展 Mapper 的联表分页查询
        Page<SalaryArchiveVO> resultPage = salaryArchiveExtMapper.selectArchivePage(page, queryReq);

        // 3. 直接调用 PageResult 的静态工厂方法完美封装返回！
        return PageResult.of(resultPage);
    }

    /**
     * 定薪或调薪：企业级拉链表实现逻辑
     * 1. 闭合当前生效版本
     * 2. 生成新版本并计算动态金额项
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean createNewSalaryVersion(ArchiveAddReqDTO req) {
        // 1. 寻找该员工当前生效的最新版本 (is_latest = 1)
        SalaryArchive currentArchive = this.getOne(new LambdaQueryWrapper<SalaryArchive>()
                .eq(SalaryArchive::getEmployeeId, req.getEmployeeId())
                .eq(SalaryArchive::getIsLatest, 1));

        int nextVersion = 1;
        if (currentArchive != null) {
            // 【日期重叠校验：新版本生效日期必须晚于当前版本
            if (!req.getEffectiveDate().isAfter(currentArchive.getEffectiveDate())) {
                throw new BusinessException("新生效日期 [" + req.getEffectiveDate() + "] 必须晚于当前版本生效日期 [" + currentArchive.getEffectiveDate() + "]");
            }
            // 2. 闭合旧版本：将其设为历史版本，并设置失效日期
            currentArchive.setIsLatest(0);
            currentArchive.setExpiryDate(req.getEffectiveDate().minusDays(1));
            this.updateById(currentArchive);

            nextVersion = currentArchive.getVersion() + 1;
        }

        // 3. 创建新版本档案主表
        SalaryArchive newArchive = new SalaryArchive();
        BeanUtils.copyProperties(req, newArchive);
        newArchive.setVersion(nextVersion);
        newArchive.setIsLatest(1);
        newArchive.setAuditStatus(1); // 直接生效，或根据权限设为待审
        this.save(newArchive);

        // 4. 处理并保存明细项 (Items)
        if (CollUtil.isNotEmpty(req.getItems())) {
            List<SalaryArchiveItem> items = req.getItems().stream().map(itemDto -> {
                SalaryArchiveItem item = new SalaryArchiveItem();
                BeanUtils.copyProperties(itemDto, item);
                item.setArchiveId(newArchive.getId());

                // 核心计算逻辑：如果是比例计算(2)，自动根据基数（档案底薪）计算出具体金额并缓存
                if (Integer.valueOf(2).equals(item.getCalcType())) {
                    BigDecimal base = item.getBaseAmount() != null && item.getBaseAmount().compareTo(BigDecimal.ZERO) > 0
                            ? item.getBaseAmount() : newArchive.getBaseSalary();

                    if (base != null && item.getRatio() != null) {
                        // 财务标准保留2位小数
                        item.setAmount(base.multiply(item.getRatio()).setScale(2, RoundingMode.HALF_UP));
                    }
                }
                return item;
            }).collect(Collectors.toList());

            iSalaryArchiveItemService.saveBatch(items);
        }

        return true;
    }

    @Override
    public SalaryArchiveVO getCurrentArchive(Long employeeId) {
        return salaryArchiveExtMapper.getLatestArchiveByEmployeeId(employeeId);
    }

    @Override
    public SalaryArchiveVO getArchiveDetail(Long archiveId) {
        return salaryArchiveExtMapper.getArchiveDetailById(archiveId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revokeLatestVersion(Long employeeId) {
        // 1. 查找当前最新版本
        SalaryArchive latestArchive = this.getOne(Wrappers.<SalaryArchive>lambdaQuery()
                .eq(SalaryArchive::getEmployeeId, employeeId)
                .eq(SalaryArchive::getIsLatest, 1));

        if (latestArchive == null) {
            throw new BusinessException("未找到该员工的薪资档案记录");
        }

        // 2. 核心拦截：如果该版本的档案已经被用于某次薪资结算核算，严禁撤销
        boolean isUsedInPayment = iSalaryPaymentRecordService.lambdaQuery()
                .eq(SalaryPaymentRecord::getArchiveId, latestArchive.getId())
                .exists();
        if (isUsedInPayment) {
            throw new BusinessException("该档案版本已被薪资结算记录引用，无法撤销，请通过发起新调薪申请来修正");
        }

        // 3. 删除最新版本主表及对应的明细记录（真删或逻辑删除视业务要求，此处示例为逻辑删除）
        this.removeById(latestArchive.getId());
        iSalaryArchiveItemService.remove(Wrappers.<SalaryArchiveItem>lambdaQuery()
                .eq(SalaryArchiveItem::getArchiveId, latestArchive.getId()));

        // 4. 时光倒流：将上一个版本重新激活为 "最新状态"
        if (latestArchive.getVersion() > 1) {
            SalaryArchive previousArchive = this.getOne(Wrappers.<SalaryArchive>lambdaQuery()
                    .eq(SalaryArchive::getEmployeeId, employeeId)
                    .eq(SalaryArchive::getVersion, latestArchive.getVersion() - 1));

            if (previousArchive != null) {
                previousArchive.setIsLatest(1);
                previousArchive.setExpiryDate(LocalDate.of(9999, 12, 31));
                this.updateById(previousArchive);
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean auditArchive(ArchiveAuditDTO auditDTO) {
        // 1. 严谨查询：获取当前最新的实体（含 version 字段）
        SalaryArchive currentArchive = this.getById(auditDTO.getId());

        // 2. 业务防御校验
        if (currentArchive == null) {
            throw new BusinessException("档案不存在");
        }
        if (currentArchive.getAuditStatus() != 0) {
            throw new BusinessException("档案已处理，请勿重复操作");
        }

        // 3. 处理审核通过逻辑
        if (auditDTO.getAuditStatus() == 1) {
            // A. 批量将旧版本置为 0。注意：使用 LambdaUpdateWrapper 不会触发乐观锁插件，
            // 它是按条件执行 SQL，不涉及实体对象的版本比对。
            this.update(Wrappers.<SalaryArchive>lambdaUpdate()
                    .eq(SalaryArchive::getEmployeeId, currentArchive.getEmployeeId())
                    .eq(SalaryArchive::getIsLatest, 1)
                    // 🚩 必须排除当前审核的 ID，否则当前对象的 version 会在数据库先变动，导致最后一步 updateById 失败
                    .ne(SalaryArchive::getId, currentArchive.getId())
                    .set(SalaryArchive::getIsLatest, 0));

            currentArchive.setIsLatest(1);
            currentArchive.setAuditStatus(1);
        }// 审批驳回
        else if (Integer.valueOf(2).equals(auditDTO.getAuditStatus())) {
            currentArchive.setAuditStatus(2);
            currentArchive.setIsLatest(0);
        }

        // 4. 设置审计信息
        currentArchive.setRemark(auditDTO.getRemark());
        currentArchive.setUpdateBy(UserContextUtil.getUsername());
        currentArchive.setUpdateTime(LocalDateTime.now());

        // 5. 执行乐观锁更新
        // 插件会自动生成：WHERE id = ? AND version = ?
        boolean success = this.updateById(currentArchive);

        if (!success) {
            // 如果失败，说明在执行第 1 步到第 5 步之间，有其他人修改了这条记录
            throw new BusinessException("审核失败：数据已被他人抢先处理，请刷新页面重试");
        }

        return true;
    }

    @Override
    public List<SalaryArchiveVO> listActiveEmployeeArchives() {
        log.info("SalaryArchiveService: 正在获取全员核算基准数据...");
        // 调用 ExtMapper 中定义的联表查询，一次性拉取在职员工 + 最新档案 + 所有明细项
        return salaryArchiveExtMapper.listActiveEmployeeArchives();
    }
}
