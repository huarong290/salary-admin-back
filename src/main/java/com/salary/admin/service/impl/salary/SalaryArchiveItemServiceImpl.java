package com.salary.admin.service.impl.salary;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salary.admin.mapper.ext.salary.SalaryArchiveItemExtMapper;
import com.salary.admin.model.entity.salary.SalaryArchiveItem;
import com.salary.admin.service.salary.ISalaryArchiveItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 薪资档案固定项明细表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-03-13
 */
@Service
public class SalaryArchiveItemServiceImpl extends ServiceImpl<SalaryArchiveItemExtMapper, SalaryArchiveItem> implements ISalaryArchiveItemService {

    @Override
    public List<SalaryArchiveItem> getItemsByArchiveId(Long archiveId) {
        return this.list(Wrappers.<SalaryArchiveItem>lambdaQuery()
                .eq(SalaryArchiveItem::getArchiveId, archiveId)
                .orderByAsc(SalaryArchiveItem::getItemType, SalaryArchiveItem::getId));
    }
}
