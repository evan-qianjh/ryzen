package com.qianjh.ryzen.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author QianJH
 */
@MapperScan(value = {"com.qianjh.ryzen.mapper"})
@Configuration
public class MyBatisConfig {

}
