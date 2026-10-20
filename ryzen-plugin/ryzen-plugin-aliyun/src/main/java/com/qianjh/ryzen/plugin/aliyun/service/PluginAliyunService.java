package com.qianjh.ryzen.plugin.aliyun.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.plugin.aliyun.entity.PluginAliyun;

/**
 * @author QianJH
 */
public interface PluginAliyunService extends IService<PluginAliyun> {

    PluginAliyun mustGet(Long oemId);
}
