package com.salary.admin.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户分页查询请求参数")
public class UserQueryReqDTO {

    @Schema(description = "页码", defaultValue = "1")
    private Long pageNum = 1L;

    @Schema(description = "每页记录数", defaultValue = "10")
    private Long pageSize = 10L;

    @Schema(description = "用户名(支持模糊查询)")
    private String username;

    @Schema(description = "手机号(精确匹配)")
    private String phone;

    @Schema(description = "账号状态(0:禁用, 1:正常)")
    private Integer status;
}