package com.qianjh.ryzen.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.quartz.autoconfigure.QuartzDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class QuartzConfig {
    @Bean
    @ConfigurationProperties("ryzen.quartz.datasource")
    public DataSourceProperties quartzDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @QuartzDataSource
    public DataSource quartzDataSource(DataSourceProperties quartzDataSourceProperties) {
        return quartzDataSourceProperties
                .initializeDataSourceBuilder()
                .build();
    }
}
