package com.qianjh.ryzen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * jwt 相关配置
 *
 * @author QianJH
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String prefix = "Bearer ";
//    private String publicKey;
}
