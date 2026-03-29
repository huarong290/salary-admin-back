package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.common.PageResult;
import com.salary.admin.model.dto.salary.itemconfig.ItemConfigAddReqDTO;
import com.salary.admin.model.dto.salary.itemconfig.ItemConfigEditReqDTO;
import com.salary.admin.model.dto.salary.itemconfig.ItemConfigQueryReqDTO;
import com.salary.admin.model.entity.salary.SalaryItemConfig;
import com.salary.admin.model.vo.salary.itemconfig.ItemConfigOptionVO;
import com.salary.admin.model.vo.salary.itemconfig.SalaryItemConfigVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 薪资项目统一配置表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
public interface ISalaryItemConfigService extends IService<SalaryItemConfig> {
    // ======================== 1. 新增操作 (Create) ========================
    /**
     * 新增薪资项目
     * 校验 item_code 和 env_var_name 的全局唯一性
     */
    Long addItemConfig(ItemConfigAddReqDTO reqDTO);
    // ======================== 2. 删除操作 (Delete) ========================
    /**
     * 删除薪资项目 (逻辑删除)
     *
     * @param id 用户主键 ID
     * @param logicalDelete 是否逻辑删除
     *                      true  = 逻辑删除（delete_flag = 1）
     *                      false = 物理删除（DELETE）
     * @return 是否删除成功
     */
    boolean deleteItemConfig(Long id, boolean logicalDelete);
    // ======================== 3. 修改操作 (Update) ========================
    /**
     * 修改薪资项目
     * 逻辑：保护固定项 + 校验变量名冲突 + 刷新计算引擎缓存
     */
    boolean editItemConfig(ItemConfigEditReqDTO reqDTO);
    // ======================== 4. 查询操作 (Read) ========================
    /**
     * 分页查询薪资项目
     * 需要关联查询出字典表的 Label，方便前端展示
     */
    PageResult<SalaryItemConfigVO> selectItemConfigPage(ItemConfigQueryReqDTO reqDTO);

    /**
     * 获取项目详细配置
     * @param id 主键ID
     * @return 包含字典 Label 的视图对象
     */
    SalaryItemConfigVO getItemConfigDetail(Long id);

    /**
     * 获取所有启用的配置，并按 calc_priority 升序排序
     * 计算引擎初始化必备：确保 basicSalary 先于 overtimePay 计算
     */
    List<SalaryItemConfig> listActiveConfigsSorted();

    /**
     * 获取环境变量名与配置项的映射表(Key: envVarName, Value: Entity)
     * 用于计算引擎上下文（Context）的变量注入
     */
    Map<String, SalaryItemConfig> getEnvVarMap();
    /**
     * 获取薪资配置项下拉列表
     * @return 启用的配置选项列表
     */
    List<ItemConfigOptionVO> listOptions();
    /**
     * 强制刷新/清除配置缓存
     */
    void clearCache();
}
