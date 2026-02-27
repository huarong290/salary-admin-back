package com.salary.admin.model.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 角色信息展示视图对象
 * </p>
 *
 * @author system
 */
@Data
@Schema(description = "角色信息展示对象")
public class SysRoleVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 角色主键ID
     */
    @Schema(description = "角色主键ID")
    private Long id;
    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String roleName;
    /**
     * 角色编码
     */
    @Schema(description = "角色编码")
    private String roleCode;
    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Integer roleSort;
    /**
     * 角色状态 (1:正常 0:停用)
     */
    @Schema(description = "角色状态 (1:正常 0:停用)")
    private Integer roleStatus;
    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
    private String roleDesc;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
