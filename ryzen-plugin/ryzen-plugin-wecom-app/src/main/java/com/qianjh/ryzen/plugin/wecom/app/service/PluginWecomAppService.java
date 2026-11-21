package com.qianjh.ryzen.plugin.wecom.app.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.plugin.wecom.app.entity.PluginWecomApp;

public interface PluginWecomAppService extends IService<PluginWecomApp> {

    PluginWecomApp getByApp(Long oemId, String corpCode, String appCode);

    PluginWecomApp getByApp(Long oemId, String corpCode, String appCode, boolean assertNotNull);
}
