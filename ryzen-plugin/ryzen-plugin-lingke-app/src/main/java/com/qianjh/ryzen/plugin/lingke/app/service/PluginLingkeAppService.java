package com.qianjh.ryzen.plugin.lingke.app.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.plugin.lingke.app.entity.PluginLingkeApp;

public interface PluginLingkeAppService extends IService<PluginLingkeApp> {

    PluginLingkeApp get(Long oemId, String appCode);

    PluginLingkeApp mustGet(Long oemId, String appCode);
}
