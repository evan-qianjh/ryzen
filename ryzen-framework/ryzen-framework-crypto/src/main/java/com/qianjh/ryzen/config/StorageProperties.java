package com.qianjh.ryzen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author QianJH
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ryzen.framework.crypto.storage")
public class StorageProperties {
    private String aesSecret;
}
