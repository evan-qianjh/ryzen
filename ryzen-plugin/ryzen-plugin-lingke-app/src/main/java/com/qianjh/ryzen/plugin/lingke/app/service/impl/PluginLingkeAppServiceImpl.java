package com.qianjh.ryzen.plugin.lingke.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.framework.common.exception.SystemConfigException;
import com.qianjh.ryzen.plugin.lingke.app.entity.PluginLingkeApp;
import com.qianjh.ryzen.plugin.lingke.app.mapper.PluginLingkeAppMapper;
import com.qianjh.ryzen.plugin.lingke.app.service.PluginLingkeAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PluginLingkeAppServiceImpl extends ServiceImpl<PluginLingkeAppMapper, PluginLingkeApp> implements PluginLingkeAppService {

    @Override
    public PluginLingkeApp get(Long oemId, String appCode) {
        return getOne(new LambdaQueryWrapper<PluginLingkeApp>()
                .eq(PluginLingkeApp::getOemId, oemId)
                .eq(PluginLingkeApp::getAppCode, appCode)
        );
    }

    @Override
    public PluginLingkeApp mustGet(Long oemId, String appCode) {
        PluginLingkeApp entity = get(oemId, appCode);
        if (entity == null) {
            log.error("未配置PluginLingkeApp ::: oemId={}, appCode={}", oemId, appCode);
            throw new SystemConfigException();
        }
        return entity;
    }
}
