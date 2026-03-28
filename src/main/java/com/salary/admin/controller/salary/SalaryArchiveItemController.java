package com.salary.admin.controller.salary;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 薪资档案固定项明细表 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-03-27
 */
@RestController
@RequestMapping("/api/salary/archive-item")
@Tag(name = "薪资档案明细 (内部支撑)", description = "内部支撑模块，不暴露独立API")
public class SalaryArchiveItemController {

    /*
     * ⛔⛔⛔ 架构师警告 (ARCHITECT WARNING) ⛔⛔⛔
     * * 本 Controller 严禁暴露任何 CRUD 接口！
     * * 原因：
     * 薪资档案明细 (SalaryArchiveItem) 属于 SalaryArchive(档案主表) 的强关联聚合子节点。
     * 为了保证 SCD Type 2 (拉链表) 的历史追溯能力和财务数据的绝对不可变性，
     * 所有明细项的增加、修改、删除，【必须】通过主表的“定薪(/init)”或“调薪(/adjust)”接口以整单级联的形式进行版本更替。
     * * 绝对不允许对已存在的档案明细进行任何形式的单条 UPDATE 或 DELETE 操作！
     */

}
