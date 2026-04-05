package com.salary.admin.mapper.ext;

import com.salary.admin.mapper.auto.SalaryAdjustmentMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 薪资周期专项调整表 (处理各类动态奖金与扣款) Mapper 扩展接口
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Mapper
public interface SalaryAdjustmentExtMapper  extends SalaryAdjustmentMapper {
}
