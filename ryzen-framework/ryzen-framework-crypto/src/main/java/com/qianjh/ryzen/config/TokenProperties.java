package com.qianjh.ryzen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author QianJH
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ryzen.framework.crypto.token")
public class TokenProperties {
    // TODO 要支持滚动更新
    private Long keyId;
    private String rsaPublicKey;
    private String rsaPrivateKey;
}
