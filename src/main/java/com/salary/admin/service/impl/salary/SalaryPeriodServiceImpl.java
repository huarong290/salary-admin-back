package com.salary.admin.service.impl.salary;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.period.PeriodConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.salary.SalaryPeriodExtMapper;
import com.salary.admin.model.dto.salary.period.PeriodAddReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodBatchInitReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodEditReqDTO;
import com.salary.admin.model.dto.salary.period.PeriodQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.model.entity.salary.SalaryPeriod;
import com.salary.admin.model.vo.salary.period.PeriodOptionVO;
import com.salary.admin.model.vo.salary.period.PeriodVO;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import com.salary.admin.service.salary.ISalaryPeriodService;
import com.salary.admin.utils.UserContextUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 薪资周期管理 服务实现类
 * </p>
 * 核心逻辑：
 * 1. 周期记录与汇总记录(Summary) 1:1 强绑定
 * 2. 批量初始化支持幂等，自动过滤已存在的记录
 * 3. 严格保护薪资数据，禁止非超管执行物理删除
 *
 * @author system
 * @since 2026-03-11
 */
@Service
@Slf4j
public class SalaryPeriodServiceImpl extends ServiceImpl<SalaryPeriodExtMapper, SalaryPeriod> implements ISalaryPeriodService {

    @Resource
    private PeriodConvert periodConvert;

    @Autowired
    private ISalaryEmployeeService employeeService;

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
        fillWorkMonth(entity);
        this.save(entity);
        return entity.getId();
    }

    /**
     * 批量初始化薪资周期（核心功能）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SalaryPeriod> batchInitPeriodsOnly(PeriodBatchInitReqDTO reqDTO) {
        String month = reqDTO.getSettlementMonth();
        List<Long> targetEmpIds = reqDTO.getEmployeeIds();

        // 1. 确定目标名单：若未传 ID 列表，则默认加载所有在职员工
        if (CollUtil.isEmpty(targetEmpIds)) {
            targetEmpIds = employeeService.list(new LambdaQueryWrapper<SalaryEmployee>()
                            .eq(SalaryEmployee::getEmploymentStatus, 1) // 在职
                            .eq(SalaryEmployee::getDeleteFlag, 0))
                    .stream().map(SalaryEmployee::getId).collect(Collectors.toList());
        }

        if (CollUtil.isEmpty(targetEmpIds)) {
            throw new BusinessException("未找到可初始化的在职员工名单");
        }

        // 2. 🌟 幂等处理：排除该月份已经存在周期的员工，防止索引冲突
        List<Long> existIds = this.list(new LambdaQueryWrapper<SalaryPeriod>()
                        .eq(SalaryPeriod::getSettlementMonth, month)
                        .in(SalaryPeriod::getEmployeeId, targetEmpIds))
                .stream().map(SalaryPeriod::getEmployeeId).collect(Collectors.toList());

        List<Long> readyIds = targetEmpIds.stream()
                .filter(id -> !existIds.contains(id))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(readyIds)) {
            log.info("{} 月份周期已全部初始化", month);
            return Collections.emptyList(); // 没人生效，返回空列表
        }

        // 3. 构造并批量保存周期记录
        String workMonth = month.substring(0, 4) + "-" + month.substring(4);
        List<SalaryPeriod> periods = readyIds.stream().map(empId -> {
            SalaryPeriod p = new SalaryPeriod();
            p.setEmployeeId(empId);
            p.setSettlementMonth(month);
            p.setWorkMonth(workMonth);
            p.setStartDate(reqDTO.getStartDate());
            p.setEndDate(reqDTO.getEndDate());
            p.setMonthDays(30); // 默认 30 天，建议根据具体日期计算
            return p;
        }).collect(Collectors.toList());

        this.saveBatch(periods);
        log.info("基础周期表批量初始化成功：新增 {} 条", periods.size());
        return periods;
    }

    /**
     * 分页查询周期列表
     */
    @Override
    public PageResult<PeriodVO> selectPeriodPage(PeriodQueryReqDTO reqDTO) {
        Page<SalaryPeriod> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());
        LambdaQueryWrapper<SalaryPeriod> wrapper = new LambdaQueryWrapper<>();

        // 条件过滤
        wrapper.eq(reqDTO.getEmployeeId() != null, SalaryPeriod::getEmployeeId, reqDTO.getEmployeeId());
        wrapper.eq(StrUtil.isNotBlank(reqDTO.getSettlementMonth()), SalaryPeriod::getSettlementMonth, reqDTO.getSettlementMonth());

        wrapper.orderByDesc(SalaryPeriod::getSettlementMonth).orderByDesc(SalaryPeriod::getCreateTime);

        IPage<SalaryPeriod> resultPage = this.page(page, wrapper);
        List<PeriodVO> voList = periodConvert.toVOList(resultPage.getRecords());

        // 批量填充员工基本信息，减少数据库交互
        if (CollUtil.isNotEmpty(voList)) {
            List<Long> employeeIds = voList.stream().map(PeriodVO::getEmployeeId).distinct().collect(Collectors.toList());
            Map<Long, SalaryEmployee> empMap = employeeService.listByIds(employeeIds).stream()
                    .collect(Collectors.toMap(SalaryEmployee::getId, e -> e));

            voList.forEach(vo -> {
                SalaryEmployee emp = empMap.get(vo.getEmployeeId());
                if (emp != null) {
                    vo.setEmployeeName(emp.getEmployeeName());
                    // 如果 VO 扩展了工号/部门等字段，可在此处填充
                }
            });
        }

        return PageResult.of(resultPage, voList);
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
        SalaryEmployee employee = employeeService.getById(vo.getEmployeeId());
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
                .select(SalaryPeriod::getId,
                        SalaryPeriod::getSettlementMonth,
                        SalaryPeriod::getWorkMonth,
                        SalaryPeriod::getStartDate,
                        SalaryPeriod::getEndDate)
                .groupBy(SalaryPeriod::getId,
                        SalaryPeriod::getSettlementMonth,
                        SalaryPeriod::getWorkMonth,
                        SalaryPeriod::getStartDate,
                        SalaryPeriod::getEndDate)
                .orderByDesc(SalaryPeriod::getSettlementMonth));

        // 2. 转换类型
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
     * 填充在岗月份展示字段
     */
    private void fillWorkMonth(SalaryPeriod entity) {
        if (StrUtil.isBlank(entity.getWorkMonth()) && StrUtil.length(entity.getSettlementMonth()) == 6) {
            entity.setWorkMonth(entity.getSettlementMonth().substring(0, 4) + "-" + entity.getSettlementMonth().substring(4));
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
}