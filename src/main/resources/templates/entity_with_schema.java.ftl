<#-- 智能识别版本字段，自动添加@Version注解 -->
package ${package.Entity};

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
<#list table.importPackages as pkg>
import ${pkg};
</#list>

<#if table.comment??>
/**
 * ${table.comment}
 *
 * @author ${author}
 * @since ${date}
 */
@Schema(name = "${entity}", description = "${table.comment}")
</#if>
@Data
@EqualsAndHashCode(callSuper = true)
<#if chainModel>
@Accessors(chain = true)
</#if>
@TableName("${table.name}")
public class ${entity} extends Model<${entity}> {

    private static final long serialVersionUID = 1L;

<#list table.fields as field>
    <#if field.comment!?length gt 0>
        <#if entityFieldUseJavaDoc>
    /**
     * ${field.comment}
     */
        </#if>
    @Schema(description = "${field.comment}")
    </#if>
    <#if field.keyFlag>
        <#assign keyPropertyName = field.propertyName>
    @TableId(value = "${field.annotationColumnName}", type = IdType.AUTO)
    <#elseif field.fill??>
    @TableField(value = "${field.annotationColumnName}", fill = FieldFill.${field.fill})
    <#elseif field.logicDeleteField>
    @TableLogic
    @TableField("${field.annotationColumnName}")
    <#elseif field.versionField>
    @Version
    @TableField("${field.annotationColumnName}")
    <#elseif field.annotationColumnName?contains("version") || field.propertyName?contains("version")>
    @Version
    @TableField("${field.annotationColumnName}")
    <#else>
    @TableField("${field.annotationColumnName}")
    </#if>
    private ${field.propertyType} ${field.propertyName};
</#list>

    @Override
    public Serializable pkVal() {
<#if keyPropertyName??>
        return this.${keyPropertyName};
<#else>
        return null;
</#if>
    }
}