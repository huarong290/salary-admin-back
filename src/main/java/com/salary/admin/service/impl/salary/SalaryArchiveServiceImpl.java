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
        // 1. 🌟 强管控防线：检查是否已经有正在【待审核】的档案，防止HR重复点击提交
        boolean hasPending = this.lambdaQuery()
                .eq(SalaryArchive::getEmployeeId, req.getEmployeeId())
                .eq(SalaryArchive::getAuditStatus, 0) // 0-待审核
                .exists();
        if (hasPending) {
            throw new BusinessException("该员工已有正在待审核的调薪申请，请先处理完毕后再提交！");
        }

        // 2. 获取当前生效的最新版本，计算下一个版本号
        SalaryArchive currentArchive = this.getOne(new LambdaQueryWrapper<SalaryArchive>()
                .eq(SalaryArchive::getEmployeeId, req.getEmployeeId())
                .eq(SalaryArchive::getAuditStatus, 1) // 1-已生效
                .eq(SalaryArchive::getIsLatest, 1));

        int nextVersion = 1;
        if (currentArchive != null) {
            // 日期重叠校验
            if (!req.getEffectiveDate().isAfter(currentArchive.getEffectiveDate())) {
                throw new BusinessException("新生效日期 [" + req.getEffectiveDate() + "] 必须晚于当前版本生效日期 [" + currentArchive.getEffectiveDate() + "]");
            }
            nextVersion = currentArchive.getVersion() + 1;

            // 🚨 注意：这里删除了原来修改旧版本 isLatest 和 expiryDate 的代码。
            // 旧版本必须保持生效，直到新版本审核通过！
        }

        // 3. 创建新版本档案主表
        SalaryArchive newArchive = new SalaryArchive();
        BeanUtils.copyProperties(req, newArchive);
        newArchive.setVersion(nextVersion);
        newArchive.setIsLatest(1);
        newArchive.setAuditStatus(0); // 🌟 核心修正：强制设为 0-待审核状态
        this.save(newArchive);

        // 4. 处理并保存明细项 (Items) - 保持你原来完美的比例计算逻辑不变
        if (CollUtil.isNotEmpty(req.getItems())) {
            List<SalaryArchiveItem> items = req.getItems().stream().map(itemDto -> {
                SalaryArchiveItem item = new SalaryArchiveItem();
                BeanUtils.copyProperties(itemDto, item);
                item.setArchiveId(newArchive.getId());

                if (Integer.valueOf(2).equals(item.getCalcType())) {
                    BigDecimal base = item.getBaseAmount() != null && item.getBaseAmount().compareTo(BigDecimal.ZERO) > 0
                            ? item.getBaseAmount() : newArchive.getBaseSalary();

                    if (base != null && item.getRatio() != null) {
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

    /**
     * 审核薪资档案 (权力交接阶段)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean auditArchive(ArchiveAuditDTO auditDTO) {
        // 1. 严谨查询：获取当前正在审核的实体
        SalaryArchive pendingArchive = this.getById(auditDTO.getId());

        // 2. 业务防御校验
        if (pendingArchive == null) {
            throw new BusinessException("档案不存在");
        }
        if (pendingArchive.getAuditStatus() != 0) {
            throw new BusinessException("档案已处理，请勿重复操作");
        }

        // 3. 处理审核通过逻辑
        if (auditDTO.getAuditStatus() == 1) {
            // 🌟 核心交接：闭合旧的已生效版本 (设为非最新，并截断有效期)
            LocalDate newEffectiveDate = pendingArchive.getEffectiveDate();

            this.update(Wrappers.<SalaryArchive>lambdaUpdate()
                    .eq(SalaryArchive::getEmployeeId, pendingArchive.getEmployeeId())
                    .eq(SalaryArchive::getAuditStatus, 1) // 找到还在生效的旧版本
                    .eq(SalaryArchive::getIsLatest, 1)
                    .ne(SalaryArchive::getId, pendingArchive.getId())
                    .set(SalaryArchive::getIsLatest, 0)
                    .set(SalaryArchive::getExpiryDate, newEffectiveDate.minusDays(1))); // 截断到新版本生效的前一天

            // 让新版本正式生效
            pendingArchive.setAuditStatus(1);
            pendingArchive.setIsLatest(1);
        }
        // 4. 处理审批驳回逻辑
        else if (Integer.valueOf(2).equals(auditDTO.getAuditStatus())) {
            pendingArchive.setAuditStatus(2);
            pendingArchive.setIsLatest(0); // 驳回后直接沦为废弃历史，不再是 latest
        }

        // 5. 设置审计信息
        pendingArchive.setRemark(auditDTO.getRemark());
        pendingArchive.setUpdateBy(UserContextUtil.getUsername());
        pendingArchive.setUpdateTime(LocalDateTime.now());

        // 6. 执行更新
        boolean success = this.updateById(pendingArchive);
        if (!success) {
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
