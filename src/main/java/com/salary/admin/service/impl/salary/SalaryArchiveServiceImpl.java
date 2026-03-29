package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.archive.ArchiveConvert;
import com.salary.admin.convert.salary.archiveitem.ArchiveItemConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryArchiveExtMapper;
import com.salary.admin.model.dto.salary.archive.ArchiveAdjustReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveAuditReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveInitReqDTO;
import com.salary.admin.model.dto.salary.archive.ArchiveQueryReqDTO;
import com.salary.admin.model.dto.salary.archiveitem.ArchiveItemReqDTO;
import com.salary.admin.model.entity.salary.SalaryArchive;
import com.salary.admin.model.entity.salary.SalaryArchiveItem;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.model.entity.salary.SalaryItemConfig;
import com.salary.admin.model.vo.salary.archive.SalaryArchiveVO;
import com.salary.admin.model.vo.salary.archiveitem.SalaryArchiveItemVO;
import com.salary.admin.service.ISysDictItemService;
import com.salary.admin.service.salary.ISalaryArchiveItemService;
import com.salary.admin.service.salary.ISalaryArchiveService;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import com.salary.admin.service.salary.ISalaryItemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工薪资标准配置表(含版本历史) 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryArchiveServiceImpl extends ServiceImpl<SalaryArchiveExtMapper, SalaryArchive> implements ISalaryArchiveService {

    private final ISalaryArchiveItemService archiveItemService;
    private final ISalaryItemConfigService itemConfigService;
    private final ISalaryEmployeeService employeeService;
    private final ISysDictItemService dictItemService;

    private final ArchiveConvert archiveConvert;
    private final ArchiveItemConvert archiveItemConvert;

    // ==========================================
    // 1. 员工入职定薪 (初始化 V1)
    // ==========================================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long initEmployeeArchive(ArchiveInitReqDTO reqDTO) {
        // 1. 校验：该员工是否已经存在档案
        long count = this.count(new LambdaQueryWrapper<SalaryArchive>()
                .eq(SalaryArchive::getEmployeeId, reqDTO.getEmployeeId()));
        if (count > 0) {
            throw new BusinessException("该员工已存在薪资档案，请走调薪流程");
        }
        //2. 增强：同步更新员工岗位信息 (定薪时确定的岗位)
        updateEmployeeJobInfo(reqDTO.getEmployeeId(), reqDTO.getChangeReason());
        // 3. 转换为实体并初始化拉链表属性
        SalaryArchive archive = archiveConvert.initToEntity(reqDTO);
        archive.setVersion(1);                   // 初始版本 V1
        archive.setLatestFlag(1);                // 标记为最新版本
        archive.setAuditStatus(1);               // 入职定薪直接生效 (可根据业务调整为 0-待审)
        archive.setExpiryDate(LocalDate.of(9999, 12, 31)); // 默认无限期

        // 如果前端未传生效日期，默认当天
        if (archive.getEffectiveDate() == null) {
            archive.setEffectiveDate(LocalDate.now());
        }

        this.save(archive);

        // 4. 级联保存明细项 (暗箱防伪造逻辑)
        saveArchiveItemsSecurely(archive.getId(), reqDTO.getArchiveItems());

        log.info("员工 [{}] 入职定薪成功，档案ID: {}", reqDTO.getEmployeeId(), archive.getId());
        return archive.getId();
    }

    // ==========================================
    // 2. 调薪申请 (生成 V(n+1) 草稿)
    // ==========================================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long adjustSalary(ArchiveAdjustReqDTO reqDTO) {
        // 1. 获取当前 [已生效] 的最新版本 (不查草稿)
        SalaryArchive currentEffective = this.getOne(new LambdaQueryWrapper<SalaryArchive>()
                .eq(SalaryArchive::getEmployeeId, reqDTO.getEmployeeId())
                .eq(SalaryArchive::getLatestFlag, 1)
                .eq(SalaryArchive::getAuditStatus, 1)); // 必须是已生效的

        if (currentEffective == null) {
            throw new BusinessException("未找到生效的薪资档案，请先为其定薪");
        }

        // 2. 检查是否已经存在 [审批中] 的版本，防止重复提交
        long pendingCount = this.count(new LambdaQueryWrapper<SalaryArchive>()
                .eq(SalaryArchive::getEmployeeId, reqDTO.getEmployeeId())
                .eq(SalaryArchive::getAuditStatus, 0));
        if (pendingCount > 0) {
            throw new BusinessException("该员工已有正在审批中的调薪申请，请勿重复提交");
        }

        // 🌟 修正：这里不再修改 currentEffective 的 latestFlag！！保持系统在审批期间仍能计算工资

        // 3. 构建新版本
        SalaryArchive newArchive = archiveConvert.adjustToEntity(reqDTO);
        newArchive.setEmployeeId(currentEffective.getEmployeeId());
        newArchive.setVersion(currentEffective.getVersion() + 1);
        newArchive.setLatestFlag(0); // 💡 注意：审批中的档案不占用 latestFlag，审批通过再抢
        newArchive.setAuditStatus(0);
        newArchive.setExpiryDate(LocalDate.of(9999, 12, 31));

        this.save(newArchive);
        saveArchiveItemsSecurely(newArchive.getId(), reqDTO.getArchiveItems());

        return newArchive.getId();
    }

    // ==========================================
    // 3. 调薪审批处理 (拉链表截断逻辑)
    // ==========================================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditArchive(ArchiveAuditReqDTO reqDTO) {
        // 从 DTO 中提取参数
        Long archiveId = reqDTO.getId();
        Integer auditStatus = reqDTO.getAuditStatus(); // 1-通过, 2-驳回
        String auditRemark = reqDTO.getRemark();
        // 1.基础防御校验
        if (auditStatus == null || (auditStatus != 1 && auditStatus != 2)) {
            throw new BusinessException("非法的审批状态");
        }
        // 2. 锁定并获取待审档案
        SalaryArchive draftArchive = this.getById(archiveId);
        if (draftArchive == null) {
            throw new BusinessException("档案不存在");
        }
        //双重状态校验，防止并发重复审批
        if (draftArchive.getAuditStatus() != 0) {
            throw new BusinessException("该申请已处理（当前状态：" + draftArchive.getAuditStatus() + "），请刷新页面");
        }
        if (auditStatus == 1) {
            // 3. 校验生效日期：不能早于上一个版本的生效日期
            SalaryArchive previous = this.getOne(new LambdaQueryWrapper<SalaryArchive>()
                    .eq(SalaryArchive::getEmployeeId, draftArchive.getEmployeeId())
                    .eq(SalaryArchive::getLatestFlag, 1)
                    .eq(SalaryArchive::getAuditStatus, 1));

            if (previous != null) {
                // 4. 生效时间线校验：禁止版本间的时间空隙或重叠
                if (!draftArchive.getEffectiveDate().isAfter(previous.getEffectiveDate())) {
                    throw new BusinessException("新版生效日期必须晚于当前版本的生效日期: " + previous.getEffectiveDate());
                }
                // 5.原子切换：旧版在此刻失效
                previous.setLatestFlag(0);
                previous.setExpiryDate(draftArchive.getEffectiveDate().minusDays(1));
                // 强校验更新结果，失败则抛出异常，触发 @Transactional 回滚
                boolean updatePrevSuccess = this.updateById(previous);
                if (!updatePrevSuccess) {
                    throw new BusinessException("系统繁忙，旧版档案状态更新失败，请重试");
                }
            }

            // 6. 新版在此刻上位
            draftArchive.setAuditStatus(1);
            draftArchive.setLatestFlag(1);
        } else {
            draftArchive.setAuditStatus(2); // 驳回即可，旧版因为我们没动过，依然是生效的
            draftArchive.setLatestFlag(0); //  显式归零，确保安全
        }

        draftArchive.setRemark(auditRemark);
        return this.updateById(draftArchive);
    }

    // ==========================================
    // 4. 获取当前 [已生效] 且 [最新] 的档案 (供引擎调用)
    // ==========================================
    @Override
    public SalaryArchiveVO getLatestEffectiveArchive(Long employeeId) {
        SalaryArchive archive = this.getOne(new LambdaQueryWrapper<SalaryArchive>()
                .eq(SalaryArchive::getEmployeeId, employeeId)
                .eq(SalaryArchive::getLatestFlag, 1)
                .eq(SalaryArchive::getAuditStatus, 1));

        if (archive == null) return null;
        return assembleArchiveVO(archive);
    }

    // ==========================================
    // 5. 获取历史版本列表
    // ==========================================
    @Override
    public List<SalaryArchiveVO> listArchiveHistory(Long employeeId) {
        List<SalaryArchive> list = this.list(new LambdaQueryWrapper<SalaryArchive>()
                .eq(SalaryArchive::getEmployeeId, employeeId)
                .orderByDesc(SalaryArchive::getVersion));

        return list.stream()
                .map(this::assembleArchiveVO) // 依次组装详情
                .collect(Collectors.toList());
    }

    // ==========================================
    // 6. 分页查询档案列表 (重构版)
    // ==========================================
    @Override
    public PageResult<SalaryArchiveVO> getArchivePage(ArchiveQueryReqDTO reqDTO) {
        Page<SalaryArchiveVO> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 1. 执行 XML 自定义 SQL，一次性带出主表和员工信息
        IPage<SalaryArchiveVO> resultPage = baseMapper.selectArchivePage(page, reqDTO);
        List<SalaryArchiveVO> records = resultPage.getRecords();

        if (CollUtil.isEmpty(records)) {
            return PageResult.of(resultPage, records);
        }

        // 2. 提取当前页所有的档案 ID
        List<Long> archiveIds = records.stream().map(SalaryArchiveVO::getId).collect(Collectors.toList());

        // 3. 性能优化核心：【批量查出】这些档案关联的所有明细项 (只发 1 次 SQL)
        List<SalaryArchiveItem> allItems = archiveItemService.list(
                new LambdaQueryWrapper<SalaryArchiveItem>()
                        .in(SalaryArchiveItem::getArchiveId, archiveIds)
        );

        // 4. 在 Java 内存中按 archiveId 进行分组聚合 (O(N) 复杂度)
        Map<Long, List<SalaryArchiveItem>> itemMap = allItems.stream()
                .collect(Collectors.groupingBy(SalaryArchiveItem::getArchiveId));

        // 5. 遍历组装并进行字典翻译 (纯内存操作，极速)
        records.forEach(vo -> {
            // 翻译结算币种
            if (StrUtil.isNotBlank(vo.getCurrency())) {
                vo.setCurrencyLabel(dictItemService.getDictLabel("settlement_currency", vo.getCurrency()));
            }

            // 挂载对应的明细项
            List<SalaryArchiveItem> myItems = itemMap.getOrDefault(vo.getId(), List.of());
            if (CollUtil.isNotEmpty(myItems)) {
                List<SalaryArchiveItemVO> itemVOList = archiveItemConvert.toVOList(myItems);
                itemVOList.forEach(itemVO -> {
                    String dictType = mapCategoryToDictType(itemVO.getItemType());
                    itemVO.setCategoryDictLabel(dictItemService.getDictLabel(dictType, itemVO.getCategoryDictValue()));
                });
                vo.setArchiveItems(itemVOList);
            }
        });

        return PageResult.of(resultPage, records);
    }

    // =========================================================================
    // 🔐 核心私有辅助方法
    // =========================================================================

    /**
     * 安全地级联保存明细项 (防篡改设计)
     * 忽略前端传入的字典名，强制从后端 item_config 表提取元数据
     */
    private void saveArchiveItemsSecurely(Long archiveId, List<ArchiveItemReqDTO> dtoList) {
        if (CollUtil.isEmpty(dtoList)) return;

        // 1. 批量获取相关配置的真实元数据
        Set<Long> configIds = dtoList.stream().map(ArchiveItemReqDTO::getItemConfigId).collect(Collectors.toSet());
        Map<Long, SalaryItemConfig> configMap = itemConfigService.listByIds(configIds).stream()
                .collect(Collectors.toMap(SalaryItemConfig::getId, c -> c));

        // 2. 构建实体
        List<SalaryArchiveItem> items = dtoList.stream().map(dto -> {
            SalaryItemConfig config = configMap.get(dto.getItemConfigId());
            if (config == null) throw new BusinessException("检测到非法的薪资配置项");

            SalaryArchiveItem item = archiveItemConvert.toEntity(dto);
            item.setArchiveId(archiveId);

            // 🛡️ 1.强制覆盖：用后端的权威数据填充快照，彻底防止前端越权篡改！
            item.setItemType(config.getItemCategory());
            item.setTypeName(config.getItemName());
            item.setCategoryDictValue(config.getCategoryDictValue());
            // 2. 关键：固化精度逻辑（假设实体类已增加这两个字段）
            // 如果配置表没有定义精度，默认保留2位；如果没有定义规则，默认四舍五入
            int precision = (config.getDecimalPlaces() != null) ? config.getDecimalPlaces() : 2;
            //使用 commons-lang3 提供的安全默认值方法
            String modeStr = StringUtils.defaultIfBlank(config.getRoundingMode(), "HALF_UP");

            if (item.getAmount() != null) {
                // 根据配置规则强制截断/舍入金额
                item.setAmount(item.getAmount().setScale(precision, RoundingMode.valueOf(modeStr)));
            }
            return item;
        }).collect(Collectors.toList());

        // 3. 批量入库
        archiveItemService.saveBatch(items);
    }

    /**
     * 组装完整的档案视图 (聚合员工信息、字典翻译、关联明细)
     */
    private SalaryArchiveVO assembleArchiveVO(SalaryArchive archive) {
        SalaryArchiveVO vo = archiveConvert.toVO(archive);

        // 1. 补全员工基本信息
        SalaryEmployee employee = employeeService.getById(archive.getEmployeeId());
        if (employee != null) {
            vo.setEmployeeName(employee.getEmployeeName());
            vo.setEmployeeCode(employee.getEmployeeCode());
            vo.setProbationEndDate(employee.getProbationEndDate()); // VO新增字段
            vo.setJobTitle(employee.getJobTitle()); // VO新增字段
        }

        // 2. 字典翻译：结算币种 (如 CNY -> 人民币)
        if (StrUtil.isNotBlank(archive.getCurrency())) {
            vo.setCurrencyLabel(dictItemService.getDictLabel("settlement_currency", archive.getCurrency()));
        }

        // 3. 查出挂载的明细项，并翻译明细字典
        List<SalaryArchiveItem> items = archiveItemService.list(new LambdaQueryWrapper<SalaryArchiveItem>()
                .eq(SalaryArchiveItem::getArchiveId, archive.getId()));

        if (CollUtil.isNotEmpty(items)) {
            List<SalaryArchiveItemVO> itemVOList = archiveItemConvert.toVOList(items);
            itemVOList.forEach(itemVO -> {
                // 翻译字典：如 'base_pay' -> '固定薪资类'
                String dictType = mapCategoryToDictType(itemVO.getItemType());
                itemVO.setCategoryDictLabel(dictItemService.getDictLabel(dictType, itemVO.getCategoryDictValue()));
            });
            vo.setArchiveItems(itemVOList);
        }

        return vo;
    }
    private void updateEmployeeJobInfo(Long employeeId, String jobTitle) {
        if (StrUtil.isBlank(jobTitle)) return;
        employeeService.updateById(SalaryEmployee.builder()
                .id(employeeId)
                .jobTitle(jobTitle)
                .build());
    }
    /**
     * 辅助方法：大类映射
     */
    private String mapCategoryToDictType(Integer category) {
        if (category == null) return "common_dict";
        return switch (category) {
            case 1 -> "salary_income_type";
            case 2 -> "salary_deduction_type";
            case 3 -> "salary_tax_social_type";
            case 4 -> "salary_company_expense_type";
            default -> "common_dict";
        };
    }
}
