package com.qianjh.ryzen.plugin.wecom.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.framework.common.exception.SystemConfigException;
import com.qianjh.ryzen.plugin.wecom.app.entity.PluginWecomApp;
import com.qianjh.ryzen.plugin.wecom.app.mapper.PluginWecomAppMapper;
import com.qianjh.ryzen.plugin.wecom.app.service.PluginWecomAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PluginWecomAppServiceImpl extends ServiceImpl<PluginWecomAppMapper, PluginWecomApp> implements PluginWecomAppService {

    @Override
    public PluginWecomApp getByApp(Long oemId, String appCode) {
        return getOne(new LambdaQueryWrapper<PluginWecomApp>()
                .eq(PluginWecomApp::getOemId, oemId)
                .eq(PluginWecomApp::getAppCode, appCode)
        );
    }

    @Override
    public PluginWecomApp getByApp(Long oemId, String appCode, boolean assertNotNull) {

        PluginWecomApp entity = getByApp(oemId, appCode);
        //
        if (assertNotNull && entity == null) {
            log.error("未配置PluginWecomApp ::: oemId={}, appCode={}", oemId, appCode);
            throw new SystemConfigException();
        }
        return entity;
    }
}
