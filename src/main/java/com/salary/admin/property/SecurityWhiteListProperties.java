package com.salary.admin.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityWhiteListProperties {
    /**
     * 从 YAML 注入 security.whitelist
     */
    private List<String> whitelist;
}
