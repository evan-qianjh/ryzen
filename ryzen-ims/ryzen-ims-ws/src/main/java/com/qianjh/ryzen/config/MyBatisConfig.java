package com.qianjh.ryzen.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@MapperScan("com.qianjh.ryzen.**.mapper")
@Configuration
public class MyBatisConfig {

}
