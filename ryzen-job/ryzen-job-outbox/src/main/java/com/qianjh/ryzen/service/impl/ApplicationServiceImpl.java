package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Value("${spring.application.name}")
    private String application;

    @Value("${server.port}")
    private Integer port;

    private static final String INSTANCE_FORMAT = "%s:%s:%s";


    @Override
    public String getName() {
        return application;
    }

    @Override
    public String getInstance() {
        return String.format(INSTANCE_FORMAT, application, getServerIp(), port);
    }

    private String getServerIp() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress(); // 返回 IP 地址
        } catch (UnknownHostException e) {
            log.error("获取IP失败");
            return "UNKNOWN";
        }
    }
}
