package com.salary.admin.model.dto.salary.itemconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 薪资项目统一配置 修改请求 DTO
 * 继承新增 DTO 以复用字段校验，补充 ID 主键
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "修改薪资项目配置请求")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemConfigEditReqDTO extends ItemConfigAddReqDTO implements Serializable {

    @NotNull(message = "配置ID不能为空")
    @Schema(description = "主键ID (雪花算法)", example = "189532145698741235")
    private Long id;

}
