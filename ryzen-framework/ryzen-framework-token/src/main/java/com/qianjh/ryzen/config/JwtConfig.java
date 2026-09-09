package com.qianjh.ryzen.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author QianJH
 */
@Slf4j
@Getter
@Configuration
public class JwtConfig {

    @Value("${ryzen.framework.token.jwt.access-token.duration-of-minutes:15}")
    private Integer accessTokenDurationOfMinutes;

    @Value("${ryzen.framework.token.jwt.refresh-token.duration-of-days:30}")
    private Integer refreshTokenDurationOfDays;
}
