package com.salary.admin.service.salary;

import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.config.SalaryConfigAddReqDTO;
import com.salary.admin.model.dto.salary.config.SalaryConfigEditReqDTO;
import com.salary.admin.model.dto.salary.config.SalaryConfigQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryConfig;
import com.salary.admin.model.vo.salary.config.SalaryConfigVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 薪资系统全局配置表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
public interface ISalaryConfigService extends IService<SalaryConfig> {

    /**
     * 分页查询薪资全局配置
     *
     * @param reqDTO 查询条件 (配置名/配置键模糊、状态筛选)
     * @return 分页结果
     */
    PageResult<SalaryConfigVO> pageQuery(SalaryConfigQueryReqDTO reqDTO);

    /**
     * 新增全局配置项
     *
     * @param reqDTO 新增参数 (配置键全局唯一)
     * @return 新配置 ID
     */
    Long addConfig(SalaryConfigAddReqDTO reqDTO);

    /**
     * 修改全局配置项 (配置键只读，仅允许修改名称/值/状态/备注)
     *
     * @param reqDTO 修改参数
     * @return 是否成功
     */
    boolean editConfig(SalaryConfigEditReqDTO reqDTO);

    /**
     * 查询全部启用的配置项 (供其他模块初始化加载)
     *
     * @return 启用配置列表
     */
    List<SalaryConfigVO> listAllActive();

    /**
     * 根据配置键获取配置值 (未命中或停用返回 null)
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getValueByKey(String configKey);
}
