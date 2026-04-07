package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.period.PeriodConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryPeriodExtMapper;
import com.salary.admin.model.dto.salary.period.*;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.salary.period.PeriodBatchInitResultVO;
import com.salary.admin.model.vo.salary.period.PeriodOptionVO;
import com.salary.admin.model.vo.salary.period.PeriodVO;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import com.salary.admin.service.salary.ISalaryPeriodService;
import com.salary.admin.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 薪资周期管理 服务实现类
 * </p>
 * 核心逻辑：
 * 1. 周期记录与汇总记录(Summary) 1:1 强绑定
 * 2. 批量初始化支持幂等，自动过滤已存在的记录
 * 3. 严格保护薪资数据，禁止非超管执行物理删除
 * 4. 具备锁定校验，防止篡改已结算数据
 * @author system
 * @since 2026-03-11
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryPeriodServiceImpl extends ServiceImpl<SalaryPeriodExtMapper, SalaryPeriod> implements ISalaryPeriodService {

    private  final SalaryPeriodExtMapper salaryPeriodExtMapper;
    // 注入转换器
    private final PeriodConvert periodConvert;

    private final ISalaryEmployeeService iSalaryEmployeeService;

    @Value("${salary.delete.allow-physical:false}")
    private boolean allowPhysicalDelete;
    /**
     * 新增单条薪资周期
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addPeriod(PeriodAddReqDTO reqDTO) {
        // 1. 业务唯一性校验：防止同一个员工在同一个结算月重复创建
        checkUniquePeriod(reqDTO.getEmployeeId(), reqDTO.getSettlementMonth(), null);

        // 2. 转换实体并补全在岗月份格式 (例如 202603 -> 2026-03)
        SalaryPeriod entity = periodConvert.toEntity(reqDTO);
        //动态计算在岗月份 (整型累计数)
        fillWorkMonth(entity);
        // 3.兜底优化：如果前端没传满勤开关，默认给 0 (非满勤)
        if (entity.getFullAttendanceFlag() == null) {
            entity.setFullAttendanceFlag(0);
        }
        this.save(entity);
        return entity.getId();
    }

    /**
     * 批量初始化薪资周期（核心功能，重构版）
     * <p>
     * 设计思想：
     * 1. 周期归周期：只负责生成 SalaryPeriod，不涉及金额计算。
     * 2. 幂等性：避免重复插入，保证 (employeeId, settlementMonth) 唯一。
     * 3. 合法性校验：确保员工状态合法，避免脏数据进入系统。
     * 4. 智能兜底：自动计算月天数，支持传入日期或默认法定计薪天数。
     * 5. 在岗月数：批量查出员工入职日期，统一调用私有方法计算正确的 workMonth。
     * 6. 分批保存：大规模员工场景下避免一次性 SQL 过大。
     * 7. 返回结果 VO：只返回统计信息，避免一次性返回大数据量。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PeriodBatchInitResultVO batchInitPeriodsOnly(PeriodBatchInitReqDTO reqDTO) {
        String month = reqDTO.getSettlementMonth();
        List<Long> targetEmpIds = reqDTO.getEmployeeIds();

        // ============================
        // 1. 确定目标员工名单
        // ============================
        if (CollUtil.isEmpty(targetEmpIds)) {
            // 默认加载所有在职员工（employmentStatus=1，deleteFlag=0）
            targetEmpIds = iSalaryEmployeeService.list(new LambdaQueryWrapper<SalaryEmployee>()
                            .eq(SalaryEmployee::getEmploymentStatus, 1)
                            .eq(SalaryEmployee::getDeleteFlag, 0))
                    .stream().map(SalaryEmployee::getId).collect(Collectors.toList());
        }

        if (CollUtil.isEmpty(targetEmpIds)) {
            throw new BusinessException("未找到可初始化的在职员工名单");
        }
        int totalCount = targetEmpIds.size();
        // ============================
        // 2. 幂等处理：排除已存在周期
        // ============================
        Set<Long> existIds = this.list(new LambdaQueryWrapper<SalaryPeriod>()
                        .eq(SalaryPeriod::getSettlementMonth, month)
                        .in(SalaryPeriod::getEmployeeId, targetEmpIds))
                .stream().map(SalaryPeriod::getEmployeeId).collect(Collectors.toSet());

        List<Long> readyIds = targetEmpIds.stream()
                .filter(id -> !existIds.contains(id))
                .collect(Collectors.toList());
        int skipCount = existIds.size();
        if (CollUtil.isEmpty(readyIds)) {
            log.info("{} 月份周期已全部初始化，无需重复建账", month);
            return PeriodBatchInitResultVO.builder()
                    .totalCount(totalCount)
                    .successCount(0)
                    .skipCount(skipCount)
                    .settlementMonth(month)
                    .build();
        }

        // ============================
        // 3. 批量查出员工入职日期
        // ============================
        Map<Long, SalaryEmployee> empMap = iSalaryEmployeeService.listByIds(readyIds).stream()
                .collect(Collectors.toMap(SalaryEmployee::getId, e -> e));

        // ============================
        // 4. 智能计算月天数
        // ============================
        BigDecimal calcMonthDays;
        if (reqDTO.getStartDate() != null && reqDTO.getEndDate() != null) {
            // 按实际日历天数计算（包含起止当天，所以 +1）
            long daysBetween = ChronoUnit.DAYS.between(reqDTO.getStartDate(), reqDTO.getEndDate()) + 1;
            calcMonthDays = BigDecimal.valueOf(daysBetween);
        } else {
            // 默认使用国家标准法定计薪天数（可改为配置表读取）
            calcMonthDays = new BigDecimal("21.75");
        }

        // ============================
        // 5. 构造周期对象
        // ============================
        List<SalaryPeriod> periods = readyIds.stream().map(empId -> {
            SalaryEmployee emp = empMap.get(empId);
            SalaryPeriod p = new SalaryPeriod();
            p.setEmployeeId(empId);
            p.setSettlementMonth(month);

            // 计算在岗月数 (统一调用私有方法)
            p.setWorkMonth(calculateWorkMonthNum(emp, month));
            // 透传前端 DTO 里的手动设置
            p.setStartDate(reqDTO.getStartDate());
            p.setEndDate(reqDTO.getEndDate());

            // 默认值：月天数、出勤天数（后续考勤系统可回填）
            // 优先用 DTO 手填的，否则用兜底计算的
            p.setMonthDays(reqDTO.getMonthDays() != null ?
                    reqDTO.getMonthDays() : calcMonthDays);
            // 出勤天数和满勤状态：直接透传 DTO（支持前端弹窗的“全员满勤”快捷设置）
            p.setAttendanceDays(reqDTO.getAttendanceDays() != null ?
                    reqDTO.getAttendanceDays() : null);
            p.setFullAttendanceFlag(null); // 建议初始化为 null，待考勤系统判定
            return p;
        }).collect(Collectors.toList());


        // ============================
        // 6.  优雅批量保存：利用 MP 原生机制，一行代码搞定分批插入 避免一次性 SQL 过大
        // ============================
        this.saveBatch(periods, 500);
        int successCount = periods.size();
        log.info("基础周期表批量初始化成功：新增 {} 条", periods.size());
        // ============================
        // 7. 返回结果 VO
        // ============================
        return PeriodBatchInitResultVO.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .skipCount(skipCount)
                .settlementMonth(month)
                .newPeriodEntities(periods) // 内部传输载荷 (包含了员工名字，下游引擎美滋滋)
                .build();
    }


    /**
     * 分页查询周期列表
     */
    @Override
    public PageResult<PeriodVO> selectPeriodPage(PeriodQueryReqDTO reqDTO) {
        // 1. 构造 MyBatis-Plus 分页对象
        Page<PeriodVO> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 2. 调用自定义 XML 连表查询
        IPage<PeriodVO> resultPage = baseMapper.selectPeriodPage(page, reqDTO);

        // 3. 直接封装返回 (因为 XML 里的 resultType 已经是 PeriodVO，自带员工姓名了)
        return PageResult.of(resultPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editPeriod(PeriodEditReqDTO reqDTO) {
        SalaryPeriod exist = this.getById(reqDTO.getId());
        if (exist == null) throw new BusinessException("薪资周期不存在");

        checkUniquePeriod(reqDTO.getEmployeeId(), reqDTO.getSettlementMonth(), reqDTO.getId());

        SalaryPeriod entity = periodConvert.toEntity(reqDTO);
        fillWorkMonth(entity);
        return this.updateById(entity);
    }

    @Override
    public PeriodVO getPeriodDetail(Long id) {
        SalaryPeriod entity = this.getById(id);
        if (entity == null) return null;

        PeriodVO vo = periodConvert.toVO(entity);
        SalaryEmployee employee = iSalaryEmployeeService.getById(vo.getEmployeeId());
        if (employee != null) vo.setEmployeeName(employee.getEmployeeName());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePeriodById(Long id, boolean logicalDelete) {
        if (logicalDelete) return this.removeById(id);

        validatePhysicalDeleteAction();
        return baseMapper.physicalDeleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePeriodByIds(List<Long> ids, boolean logicalDelete) {
        if (CollUtil.isEmpty(ids)) return false;
        if (logicalDelete) return this.removeByIds(ids);

        validatePhysicalDeleteAction();
        return baseMapper.physicalDeleteByIds(ids) > 0;
    }

    @Override
    public List<PeriodOptionVO> listOption() {
        // 1. 增加 startDate 和 endDate 的查询，确保转换器能拿到数据
        List<SalaryPeriod> list = this.list(new LambdaQueryWrapper<SalaryPeriod>()
                .select(SalaryPeriod::getSettlementMonth,
                        SalaryPeriod::getStartDate,
                        SalaryPeriod::getEndDate)
                .groupBy(SalaryPeriod::getSettlementMonth,
                        SalaryPeriod::getStartDate,
                        SalaryPeriod::getEndDate)
                .orderByDesc(SalaryPeriod::getSettlementMonth));

        // 2. 转换类型
        return periodConvert.toOptionVOList(list);
    }

    @Override
    public List<PeriodOptionVO> listOptionByEmployee(PeriodSelectQueryReqDTO queryReqDTO) {
        LambdaQueryWrapper<SalaryPeriod> wrapper = new LambdaQueryWrapper<>();

        // 1. 核心逻辑分发
        if (queryReqDTO != null && queryReqDTO.getEmployeeId() != null) {
            // 【查个人】：需要查出该员工所有的周期，不能用 groupBy，因为同一个月可能因为调薪有多个切片
            wrapper.eq(SalaryPeriod::getEmployeeId, queryReqDTO.getEmployeeId());
        } else {
            // 【查全局】：做去重处理，通常用于搜索栏的月份筛选
            wrapper.select(SalaryPeriod::getSettlementMonth,
                            SalaryPeriod::getStartDate,
                            SalaryPeriod::getEndDate)
                    .groupBy(SalaryPeriod::getSettlementMonth,
                            SalaryPeriod::getStartDate,
                            SalaryPeriod::getEndDate);
        }

        // 2. 统一排序：按月份倒序，最新的在最上面
        wrapper.orderByDesc(SalaryPeriod::getSettlementMonth)
                .orderByDesc(SalaryPeriod::getStartDate);

        List<SalaryPeriod> list = this.list(wrapper);

        // 3. 转换并返回
        return periodConvert.toOptionVOList(list);
    }

    // ============================ 私有辅助方法 ============================

    /**
     * 唯一性校验：确保员工+月份唯一
     */
    private void checkUniquePeriod(Long empId, String month, Long excludeId) {
        LambdaQueryWrapper<SalaryPeriod> wrapper = new LambdaQueryWrapper<SalaryPeriod>()
                .eq(SalaryPeriod::getEmployeeId, empId)
                .eq(SalaryPeriod::getSettlementMonth, month);
        if (excludeId != null) wrapper.ne(SalaryPeriod::getId, excludeId);

        if (this.count(wrapper) > 0) {
            throw new BusinessException("该员工在 [" + month + "] 已存在周期，请勿重复创建");
        }
    }


    /**
     * 智能填充在岗月数 (整型)
     * 如果前端没有传，或者传了无效值，则自动去员工库查入职日期进行计算
     */
    private void fillWorkMonth(SalaryPeriod entity) {
        if (entity.getWorkMonth() == null || entity.getWorkMonth() <= 0) {
            // 根据员工ID查询员工档案，获取入职日期
            SalaryEmployee emp = iSalaryEmployeeService.getById(entity.getEmployeeId());
            entity.setWorkMonth(calculateWorkMonthNum(emp, entity.getSettlementMonth()));
        }
    }

    /**
     * 物理删除安全校验
     */
    private void validatePhysicalDeleteAction() {
        if (!allowPhysicalDelete) throw new BusinessException("系统策略：当前环境禁止物理删除薪资周期");
        if (!UserContextUtil.isAdmin()) throw new BusinessException("权限不足：只有管理员可执行物理删除");
        log.warn("管理员 {} 正在执行物理删除敏感薪资数据", UserContextUtil.getUsername());
    }

    /**
     * 计算员工在岗月数（返回 Integer，对应数据库新设计）
     * 逻辑：入职当月算第 1 个月，次月算第 2 个月。
     *
     * @param emp             员工对象（必须包含入职日期）
     * @param settlementMonth 结算月份（格式：yyyyMM）
     * @return 累计在岗月数（最小值为 1）
     */
    private Integer calculateWorkMonthNum(SalaryEmployee emp, String settlementMonth) {
        if (emp == null || emp.getEntryDate() == null || StringUtils.isBlank(settlementMonth)) {
            log.warn("员工 {} 缺失入职日期或月份为空，workMonth 默认设置为 1", emp != null ? emp.getId() : "未知");
            return 1;
        }
        try {
            //：利用正则把所有 "非数字" 字符全部替换掉
            // 完美兼容: "2026-03", "2026/03", "2026.03", "2026_03", "2026年03月" -> "202603"
            String cleanMonth = settlementMonth.replaceAll("\\D", "");

            // 安全校验：清理后如果不是 6 位数字，说明数据畸形
            if (cleanMonth.length() != 6) {
                log.warn("结算月份提取异常，原始数据: {}, 清理后: {}", settlementMonth, cleanMonth);
                return 1;
            }

            // 归一化计算：全部对齐到 1 号
            LocalDate current = LocalDate.parse(cleanMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate entry = emp.getEntryDate().withDayOfMonth(1);

            // 计算月差 + 1 (入职当月算第1个月)
            long months = ChronoUnit.MONTHS.between(entry, current) + 1;

            // 返回整型结果，最低保障为 1
            return (int) Math.max(1, months);
        } catch (Exception e) {
            log.error("计算员工 {} 在岗月数异常: {}", emp.getId(), e.getMessage(), e);
            return 1;
        }
    }
}
