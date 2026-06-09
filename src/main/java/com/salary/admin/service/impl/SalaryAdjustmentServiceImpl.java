package com.salary.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.common.PageResult;
import com.salary.admin.convert.salary.adjustment.AdjustmentConvert;
import com.salary.admin.exception.BusinessException;
import com.salary.admin.mapper.ext.SalaryAdjustmentExtMapper;
import com.salary.admin.model.dto.salary.adjustment.AdjustmentAddReqDTO;
import com.salary.admin.model.dto.salary.adjustment.AdjustmentEditReqDTO;
import com.salary.admin.model.dto.salary.adjustment.AdjustmentQueryDTO;
import com.salary.admin.model.entity.salary.SalaryAdjustment;
import com.salary.admin.model.vo.salary.adjustment.SalaryAdjustmentVO;
import com.salary.admin.service.ISalaryAdjustmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * <p>
 * 薪资周期专项调整表 (处理各类动态奖金与扣款) 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-04-05
 */
@Slf4j
@Service
@RequiredArgsConstructor // 💡 自动注入 private final 依赖
public class SalaryAdjustmentServiceImpl extends ServiceImpl<SalaryAdjustmentExtMapper, SalaryAdjustment> implements ISalaryAdjustmentService {

    private final AdjustmentConvert adjustmentConvert;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAdjustment(AdjustmentAddReqDTO reqDTO) {
        // 1. DTO 转 实体 (省去了所有冗长的 set 代码)
        SalaryAdjustment entity = adjustmentConvert.addToEntity(reqDTO);

        // 2. 核心防篡改机制：结算本币金额由后端强行计算！保留4位小数，四舍五入
        BigDecimal settlementAmount = reqDTO.getOriginalAmount()
                .multiply(reqDTO.getExchangeRate())
                .setScale(4, RoundingMode.HALF_UP);
        entity.setSettlementAmount(settlementAmount);

        // 3. 初始化状态：1-手工录入，0-草稿待生效
        entity.setSourceType(1);
        entity.setStatus(0);

        this.save(entity);
        log.info("新增专项调整成功, 员工ID: {}, 薪资项: {}, 结算金额: {}", entity.getEmployeeId(), entity.getItemCode(), settlementAmount);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editAdjustment(AdjustmentEditReqDTO reqDTO) {
        // 1. 查出旧数据
        SalaryAdjustment exist = this.getById(reqDTO.getId());
        if (exist == null) {
            throw new BusinessException("该专项调整记录不存在或已被删除！");
        }

        // 2.  状态机保护：已生效的数据绝对禁止修改！
        if (exist.getStatus() == 1) {
            throw new BusinessException(String.format("项目 [%s] 已生效参与算薪，禁止修改！请先撤回状态。", exist.getItemName()));
        }

        // 3. 使用转换器的高级特性，将新参数直接“覆盖”到旧实体上
        adjustmentConvert.updateEntity(reqDTO, exist);

        // 4.重新进行汇率折算
        BigDecimal settlementAmount = reqDTO.getOriginalAmount()
                .multiply(reqDTO.getExchangeRate())
                .setScale(4, RoundingMode.HALF_UP);
        exist.setSettlementAmount(settlementAmount);

        return this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAdjustments(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return false;
        }

        List<SalaryAdjustment> list = this.listByIds(ids);
        for (SalaryAdjustment item : list) {
            // 🌟 状态机保护：禁止删除已生效数据
            if (item.getStatus() == 1) {
                throw new BusinessException(String.format("项目 [%s] 已生效，为保证数据一致性无法直接删除，请先撤回！", item.getItemName()));
            }
        }

        log.info("批量删除专项调整记录, 影响条数: {}", ids.size());
        return this.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditAdjustments(List<Long> ids, Integer targetStatus) {
        if (CollUtil.isEmpty(ids) || targetStatus == null) {
            return false;
        }

        // 使用 LambdaUpdateWrapper 批量高效更新状态
        boolean result = this.lambdaUpdate()
                .in(SalaryAdjustment::getId, ids)
                .set(SalaryAdjustment::getStatus, targetStatus)
                .update();

        log.info("批量变更专项调整状态, 目标状态: {}, 影响条数: {}", targetStatus, ids.size());
        return result;
    }

    @Override
    public PageResult<SalaryAdjustmentVO> pageQuery(AdjustmentQueryDTO queryDTO) {
        // 1. 构造 MyBatis-Plus 分页参数
        Page<SalaryAdjustmentVO> pageParam = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2. 调用 ExtMapper 里的关联查询方法
        // 数据库一层直接出 VO，不再需要 Service 层循环赋值
        IPage<SalaryAdjustmentVO> pageData = baseMapper.selectAdjustmentPageVo(pageParam, queryDTO);

        return PageResult.of(pageData);
    }

    @Override
    public SalaryAdjustmentVO getDetail(Long id) {
        SalaryAdjustmentVO vo = baseMapper.getDetailVoById(id);
        if (vo == null) {
            throw new BusinessException("该专项调整记录不存在！");
        }
        return vo;
    }
}