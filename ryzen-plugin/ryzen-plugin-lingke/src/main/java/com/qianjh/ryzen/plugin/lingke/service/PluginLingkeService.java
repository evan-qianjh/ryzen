package com.qianjh.ryzen.plugin.lingke.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.plugin.lingke.entity.PluginLingke;

public interface PluginLingkeService extends IService<PluginLingke> {

    PluginLingke get(Long oemId, Long id);

    PluginLingke mustGet(Long oemId, Long id);
}
