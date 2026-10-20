package com.qianjh.ryzen.plugin.alipay.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.plugin.alipay.entity.PluginAlipay;
import com.qianjh.ryzen.plugin.aliyun.entity.PluginAliyun;

public interface PluginAlipayService extends IService<PluginAlipay> {

    PluginAlipay mustGet(Long oemId);
}
