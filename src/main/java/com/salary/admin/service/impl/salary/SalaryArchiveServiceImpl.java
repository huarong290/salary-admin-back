package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.service.salary.ISalaryArchiveItemService;
import com.salary.admin.service.salary.ISalaryArchiveService;
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
     * 定薪或调薪：企业级拉链表实现
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

                // 核心计算逻辑：如果是比例计算(2)，自动根据基数计算出 amount 缓存
                if (item.getCalcType() != null && item.getCalcType() == 2) {
                    BigDecimal base = item.getBaseAmount() != null ? item.getBaseAmount() : newArchive.getBaseSalary();
                    // 这里 ratio 假设前端传 0.0800
                    item.setAmount(base.multiply(item.getRatio()).setScale(8, RoundingMode.HALF_UP));
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

        // 2. 核心拦截：已生效的记录坚决不准撤销，防止破坏已算出的历史工资账单！业务隔离：版本 1 是初始定薪，通常不允许直接 revoke，建议用删除逻辑
        if (latestArchive.getAuditStatus() == 1) {
            throw new BusinessException("初始定薪版本无法通过‘撤销’移除，请使用删除功能或发起调薪");
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
        // 1. 查询当前待审核的档案
        SalaryArchive currentArchive = this.getById(auditDTO.getId());
        if (currentArchive == null || currentArchive.getAuditStatus() != 0) {
            throw new BusinessException("档案不存在或已处理");
        }
        // 只有待审核(0)状态的档案才允许审核
        if (currentArchive.getAuditStatus() != 0) {
            throw new BusinessException("该档案已处理，请勿重复操作");
        }
        // 2. 如果审核通过 (status = 1)
        if (auditDTO.getAuditStatus() == 1) {
            // A. 将该员工之前所有标记为 is_latest = 1 的旧版本全部更新为 0
            this.update(new LambdaUpdateWrapper<SalaryArchive>()
                    .eq(SalaryArchive::getEmployeeId, currentArchive.getEmployeeId())
                    .eq(SalaryArchive::getIsLatest, 1)
                    .set(SalaryArchive::getIsLatest, 0));

            // B. 设置当前档案为最新且生效
            currentArchive.setIsLatest(1);
            currentArchive.setAuditStatus(1);
        }
        // 3. 如果审核驳回 (status = 2)
        else if (auditDTO.getAuditStatus() == 2) {
            currentArchive.setAuditStatus(2);
            currentArchive.setIsLatest(0); // 驳回的版本不作为最新版本显示在默认列表
        }

        // 4. 更新备注及审核信息
        currentArchive.setRemark(auditDTO.getRemark());
        currentArchive.setUpdateBy(UserContextUtil.getUsername());
        currentArchive.setUpdateTime(LocalDateTime.now());

        return this.updateById(currentArchive);
    }
}
