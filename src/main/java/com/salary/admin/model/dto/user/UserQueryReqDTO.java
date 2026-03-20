package com.salary.admin.model.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.salary.admin.model.dto.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户分页查询请求参数")
//架构师标配：自动忽略前端传来的、DTO 中未定义的冗余字段
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserQueryReqDTO extends PageQueryDTO {

    @Schema(description = "用户名(支持模糊查询)")
    private String username;

    @Schema(description = "手机号(精确匹配)")
    private String phone;

    @Schema(description = "账号状态(0:禁用, 1:正常)")
    private Integer status;
}