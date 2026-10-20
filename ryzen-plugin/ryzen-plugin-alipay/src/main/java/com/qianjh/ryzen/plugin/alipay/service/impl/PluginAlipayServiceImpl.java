package com.qianjh.ryzen.plugin.alipay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.framework.common.exception.SystemConfigException;
import com.qianjh.ryzen.plugin.alipay.entity.PluginAlipay;
import com.qianjh.ryzen.plugin.alipay.mapper.PluginAlipayMapper;
import com.qianjh.ryzen.plugin.alipay.service.PluginAlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PluginAlipayServiceImpl extends ServiceImpl<PluginAlipayMapper, PluginAlipay> implements PluginAlipayService {
    @Override
    public PluginAlipay mustGet(Long oemId) {
        PluginAlipay entity = getOne(new LambdaQueryWrapper<PluginAlipay>()
                .eq(PluginAlipay::getOemId, oemId)
        );
        if (entity == null) {
            log.error("未配置PluginAlipay ::: oemId={}", oemId);
            throw new SystemConfigException();
        }
        return entity;
    }


}
