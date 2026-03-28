package com.salary.admin.model.vo.salary.archive;

import com.salary.admin.model.vo.salary.archiveitem.SalaryArchiveItemVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 薪资档案详情视图对象
 * 用于展示薪资档案主表信息，并级联聚合员工基本信息与薪资明细项
 *
 * @author system
 * @since 2026-03-13
 */
@Data
@Schema(description = "薪资档案详情视图对象")
public class SalaryArchiveVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 档案ID
     */
    @Schema(description = "档案记录ID")
    private Long id;
    /**
     * 员工ID
     */
    @Schema(description = "员工ID")
    private Long employeeId;
    /**
     * 员工姓名 (关联员工表获取)
     */
    @Schema(description = "员工姓名 (关联员工表获取)")
    private String employeeName;
    /**
     * 员工编号 (关联员工表获取)
     */
    @Schema(description = "员工编号 (关联员工表获取)")
    private String employeeCode;
    /**
     * 版本号 (每次调薪递增)
     */
    @Schema(description = "版本号 (每次调薪递增)")
    private Integer version;
    /**
     * 审核状态: 0-草稿/待审, 1-已生效, 2-驳回
     */
    @Schema(description = "审核状态: 0-草稿/待审, 1-已生效, 2-驳回 ")
    private Integer auditStatus;
    /**
     * 是否当前最新版本: 0-历史版本, 1-最新版本
     */
    @Schema(description = "是否当前最新版本: 0-历史版本, 1-最新版本")
    private Integer latestFlag;
    /**
     * 基本工资/转正底薪
     */
    @Schema(description = "基本工资/转正底薪")
    private BigDecimal baseSalary;
    /**
     * 试用期底薪
     */
    @Schema(description = "试用期底薪")
    private BigDecimal probationBaseSalary;
    /**
     * 默认结算币种
     */
    @Schema(description = "默认结算币种 (如: CNY)")
    private String currency;
    /**
     * 币种名称
     */
    @Schema(description = "币种名称 (业务字典转换: 菲律宾比索)")
    private String currencyLabel;
    /**
     * 生效起始日期
     */
    @Schema(description = "生效起始日期")
    private LocalDate effectiveDate;
    /**
     * 失效日期
     */
    @Schema(description = "失效日期")
    private LocalDate expiryDate;

    /**
     * 个税规则Code
     */
    @Schema(description = "个税规则Code")
    private String taxRuleCode;

    /**
     * 调薪原因 (如: 年度普调、晋升)
     */
    @Schema(description = "调薪原因 (如: 年度普调、晋升)")
    private String changeReason;
    /**
     * 档案备注
     */
    @Schema(description = "档案备注")
    private String remark;
    /**
     * 薪资项明细列表
     */
    @Schema(description = "薪资项明细列表")
    private List<SalaryArchiveItemVO> archiveItems;
}
