package com.qianjh.ryzen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author QianJH
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ryzen.framework.security")
public class SecurityProperties {
    /**
     * 存储密钥
     */
    private volatile AesProperties storage;
    /**
     * 跨系统报文
     */
    private volatile RsaProperties cross;
    /**
     * 通讯报文
     */
    private volatile RsaProperties message;
    /**
     * JWT Token
     */
    private volatile RsaProperties token;
}
