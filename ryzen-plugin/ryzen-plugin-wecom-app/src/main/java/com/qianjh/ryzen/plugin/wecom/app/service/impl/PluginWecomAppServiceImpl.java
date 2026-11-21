package com.qianjh.ryzen.plugin.wecom.app.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.framework.common.exception.SystemConfigException;
import com.qianjh.ryzen.plugin.wecom.app.entity.PluginWecomApp;
import com.qianjh.ryzen.plugin.wecom.app.mapper.PluginWecomAppMapper;
import com.qianjh.ryzen.plugin.wecom.app.service.PluginWecomAppService;
import com.qianjh.ryzen.plugin.wecom.entity.PluginWecom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PluginWecomAppServiceImpl extends ServiceImpl<PluginWecomAppMapper, PluginWecomApp> implements PluginWecomAppService {

    @Override
    public PluginWecomApp getByApp(Long oemId, String corpCode, String appCode) {
        return getBaseMapper().selectJoinOne(new MPJLambdaWrapper<PluginWecomApp>()
                // select
                .selectAll(PluginWecomApp.class)
                // join
                .innerJoin(PluginWecom.class, PluginWecom::getId, PluginWecomApp::getWecomId)
                // where
                .eq(PluginWecomApp::getOemId, oemId)
                .eq(PluginWecomApp::getAppCode, appCode)
                .eq(PluginWecom::getCorpCode, corpCode)
        );
    }

    @Override
    public PluginWecomApp getByApp(Long oemId, String corpCode, String appCode, boolean assertNotNull) {

        PluginWecomApp entity = getByApp(oemId, corpCode, appCode);
        //
        if (assertNotNull && entity == null) {
            log.error("未配置PluginWecomApp ::: oemId={}, appCode={}", oemId, appCode);
            throw new SystemConfigException();
        }
        return entity;
    }
}
