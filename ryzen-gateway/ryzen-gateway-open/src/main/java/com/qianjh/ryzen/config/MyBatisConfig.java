package com.qianjh.ryzen.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author QianJH
 */
@MapperScan(value = {"com.qianjh.ryzen.mapper"}, markerInterface = BaseMapper.class)
@Configuration
public class MyBatisConfig {


}
