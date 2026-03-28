package com.salary.admin.mapper.ext;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.admin.mapper.auto.SalaryItemConfigMapper;
import com.salary.admin.model.dto.salary.itemconfig.ItemConfigQueryReqDTO;
import com.salary.admin.model.vo.salary.itemconfig.SalaryItemConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 薪资项目统一配置表扩展 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@Mapper
public interface SalaryItemConfigExtMapper extends SalaryItemConfigMapper {

    /**
     * 分页查询薪资项目配置
     * <p>
     * 功能：
     * - 自动关联字典表获取分类标签（categoryLabel）
     * - 支持全字段模糊搜索（itemName、itemCode、pinyinCode）
     * - 按计算优先级和排序值升序排列
     * </p>
     *
     * @param page   分页参数对象（由 MyBatis-Plus 提供）
     * @param reqDTO 查询条件封装对象
     * @return 分页结果，包含薪资项目配置及字典标签信息
     */
    IPage<SalaryItemConfigVO> selectItemConfigPage(Page<?> page, @Param("req") ItemConfigQueryReqDTO reqDTO);
}
