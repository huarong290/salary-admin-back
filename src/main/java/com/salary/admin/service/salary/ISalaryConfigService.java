package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.config.SalaryConfigAddReqDTO;
import com.salary.admin.model.dto.salary.config.SalaryConfigEditReqDTO;
import com.salary.admin.model.dto.salary.config.SalaryConfigQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryConfig;
import com.salary.admin.model.vo.salary.config.SalaryConfigVO;

import java.util.List;

/**
 * <p>
 * 薪资系统全局配置表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-20
 */
public interface ISalaryConfigService extends IService<SalaryConfig> {
    /**
     * 新增薪资全局配置
     *
     * @param reqDTO 配置新增参数
     * @return 新生成的配置 ID
     */
    Long addConfig(SalaryConfigAddReqDTO reqDTO);
    /**
     * 获取所有配置项列表
     * * @return 配置 VO 列表
     */
    List<SalaryConfigVO> selectAllConfigs();

    /**
     * 根据配置键获取具体配置值
     * 建议实现类中加入 Redis 缓存：key = "sys:config:val:{configKey}"
     *
     * @param configKey 配置键 (如: SETTLEMENT_CURRENCY)
     * @return 配置值 (如: USDT)
     */
    String getConfigValue(String configKey);

    /**
     * 更新配置信息
     * 更新成功后需同步失效/更新 Redis 缓存
     *
     * @param reqDTO 配置更新参数
     * @return 是否成功
     */
    boolean updateConfig(SalaryConfigEditReqDTO reqDTO);

    /**
     * 获取当前系统结算本位币
     * 便捷方法，内部调用 getConfigValue("SETTLEMENT_CURRENCY")
     *
     * @return 币种代码 (如: USDT)
     */
    String getCurrentSettlementCurrency();

    /**
     * 分页查询薪资全局配置
     *
     * @param reqDTO 查询条件
     * @return 分页结果封装
     */
    PageResult<SalaryConfigVO> selectConfigByPage(SalaryConfigQueryReqDTO reqDTO);
}
