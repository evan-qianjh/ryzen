package com.qianjh.ryzen.plugin.wecom.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.plugin.wecom.entity.PluginWecom;

public interface PluginWecomService extends IService<PluginWecom> {

    PluginWecom getById(Long oemId, Long id);

    PluginWecom getById(Long oemId, Long id, boolean assertNotNull);

    PluginWecom getByCorp(Long oemId, String corpCode);
}
