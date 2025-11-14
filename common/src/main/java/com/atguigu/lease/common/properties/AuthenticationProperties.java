package com.atguigu.lease.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "web.admin")
@Data
public class AuthenticationProperties {
    private String[] excludePath;
    private String[] includePath;
}
