package com.salary.admin.service.salary;

import com.baomidou.mybatisplus.extension.service.IService;
import com.salary.admin.model.entity.salary.SalaryArchiveItem;

import java.util.List;

/**
 * <p>
 * 薪资档案固定项明细表 服务类
 * </p>
 *
 * @author system
 * @since 2026-03-13
 */
public interface ISalaryArchiveItemService extends IService<SalaryArchiveItem> {

    /**
     * 根据档案ID获取所有配置项
     */
    List<SalaryArchiveItem> getItemsByArchiveId(Long archiveId);
}
