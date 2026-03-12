package com.salary.admin.service.impl.salary;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.employee.EmployeeCovert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.salary.SalaryEmployeeExtMapper;
import com.salary.admin.model.dto.salary.employee.EmployeeAddReqDTO;
import com.salary.admin.model.dto.salary.employee.EmployeeEditReqDTO;
import com.salary.admin.model.dto.salary.employee.EmployeeQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryEmployee;
import com.salary.admin.model.vo.salary.employee.EmployeeOptionVO;
import com.salary.admin.model.vo.salary.employee.EmployeeVO;
import com.salary.admin.service.salary.ISalaryEmployeeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工基本信息表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-11
 */
@Service
@Slf4j
public class SalaryEmployeeServiceImpl extends ServiceImpl<SalaryEmployeeExtMapper, SalaryEmployee> implements ISalaryEmployeeService {
   @Autowired
    private SalaryEmployeeExtMapper salaryEmployeeExtMapper;

    @Resource
    private EmployeeCovert employeeCovert; // 注入转换器

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addEmployee(EmployeeAddReqDTO reqDTO) {
        // 1. 唯一性校验：员工编号不能重复
        long count = this.count(new LambdaQueryWrapper<SalaryEmployee>()
                .eq(SalaryEmployee::getEmployeeCode, reqDTO.getEmployeeCode()));
        if (count > 0) {
            throw new BusinessException("员工编号已存在，请勿重复添加");
        }

        // 2. DTO 转 Entity  使用 MapStruct 转换
        SalaryEmployee employee = employeeCovert.toEntity(reqDTO);

        // 3. 执行保存
        this.save(employee);
        log.info("新增员工成功，ID: {}, 编号: {}", employee.getId(), employee.getEmployeeCode());
        return employee.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editEmployee(EmployeeEditReqDTO reqDTO) {
        // 1. 存在性校验
        SalaryEmployee existEmployee = this.getById(reqDTO.getId());
        if (existEmployee == null) {
            throw new BusinessException("该员工档案不存在或已被删除");
        }

        // 2. 编号冲突校验 (如果修改了编号)
        if (!existEmployee.getEmployeeCode().equals(reqDTO.getEmployeeCode())) {
            long count = this.count(new LambdaQueryWrapper<SalaryEmployee>()
                    .eq(SalaryEmployee::getEmployeeCode, reqDTO.getEmployeeCode())
                    .ne(SalaryEmployee::getId, reqDTO.getId()));
            if (count > 0) {
                throw new BusinessException("员工编号已存在，修改失败");
            }
        }

        // 3. DTO 转 Entity 并更新
        BeanUtil.copyProperties(reqDTO, existEmployee);
        return this.updateById(existEmployee);
    }

    @Override
    public PageResult<EmployeeVO> selectEmployeePage(EmployeeQueryReqDTO reqDTO) {
        // 1. 构造分页参数
        Page<SalaryEmployee> page = new Page<>(reqDTO.getPageNum(), reqDTO.getPageSize());

        // 2. 构造查询条件
        LambdaQueryWrapper<SalaryEmployee> wrapper = new LambdaQueryWrapper<>();
        // 关键词模糊查询 (编号或姓名)
        if (StrUtil.isNotBlank(reqDTO.getKeyword())) {
            wrapper.and(q -> q.like(SalaryEmployee::getEmployeeCode, reqDTO.getKeyword())
                    .or()
                    .like(SalaryEmployee::getEmployeeName, reqDTO.getKeyword()));
        }
        // 部门精确查询
        if (StrUtil.isNotBlank(reqDTO.getDepartment())) {
            wrapper.eq(SalaryEmployee::getDepartment, reqDTO.getDepartment());
        }

        wrapper.orderByDesc(SalaryEmployee::getCreateTime);

        // 3. 执行查询
        IPage<SalaryEmployee> resultPage = this.page(page, wrapper);

        // 4. Entity 转 VO 列表
        List<EmployeeVO> voList = employeeCovert.toVOList(resultPage.getRecords());

        // 🌟 5. 使用你提供的 PageResult 场景 2 进行封装返回
        return PageResult.of(resultPage, voList);
    }

    @Override
    public EmployeeVO getEmployeeDetail(Long id) {
        SalaryEmployee employee = this.getById(id);
        if (employee == null) {
            return null;
        }
        EmployeeVO vo = employeeCovert.toVO(employee);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteEmployeeById(Long id, boolean logicalDelete) {
        if (logicalDelete) {
            // 逻辑删除：触发 MyBatis-Plus 的 @TableLogic 逻辑
            return this.removeById(id);
        } else {
            // 2. 物理删除：调用 ExtMapper 中的自定义 SQL
            log.warn("正在对员工执行物理删除操作，ID: {}", id);
            return baseMapper.physicalDeleteById(id) > 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteEmployeeByIds(List<Long> ids, boolean logicalDelete) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        if (logicalDelete) {
            // 1. 批量逻辑删除
            return this.removeByIds(ids);
        } else {
            // 2. 批量物理删除
            log.warn("正在对员工执行批量物理删除操作，IDs: {}", ids);
            return baseMapper.physicalDeleteByIds(ids) > 0;
        }
    }

    // 在 SalaryEmployeeServiceImpl.java 中实现方法

    @Override
    public List<EmployeeOptionVO> listOption(String keyword) {
        // 1. 构造查询条件
        LambdaQueryWrapper<SalaryEmployee> wrapper = new LambdaQueryWrapper<>();

        // 条件一：必须是在职员工且未被删除
        wrapper.eq(SalaryEmployee::getEmploymentStatus, 1)
                .eq(SalaryEmployee::getDeleteFlag, 0);

        // 条件二：如果传了关键字，则匹配姓名或工号
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(q -> q.like(SalaryEmployee::getEmployeeName, keyword)
                    .or()
                    .like(SalaryEmployee::getEmployeeCode, keyword));
        }

        // 2. 性能优化：只查询特定的列 (ID, 姓名, 编号)
        // 避免把“银行卡号”、“住址”等敏感且沉重的数据查出来
        wrapper.select(SalaryEmployee::getId,
                SalaryEmployee::getEmployeeName,
                SalaryEmployee::getEmployeeCode);

        // 3. 排序及数量限制 (企业级标准：防止全量加载)
        wrapper.orderByAsc(SalaryEmployee::getEmployeeCode).last("LIMIT 50");

        // 4. 执行查询并转换
        List<SalaryEmployee> list = this.list(wrapper);

        // 5. 将 Entity 转为轻量级的 OptionVO
        return list.stream().map(item -> {
            EmployeeOptionVO vo = new EmployeeOptionVO();
            vo.setId(item.getId());
            vo.setEmployeeName(item.getEmployeeName());
            vo.setEmployeeCode(item.getEmployeeCode());
            return vo;
        }).collect(Collectors.toList());
    }
}
