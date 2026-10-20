package com.qianjh.ryzen.plugin.aliyun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.plugin.aliyun.entity.PluginAliyun;
import com.qianjh.ryzen.framework.common.exception.SystemConfigException;
import com.qianjh.ryzen.plugin.aliyun.mapper.PluginAliyunMapper;
import com.qianjh.ryzen.plugin.aliyun.service.PluginAliyunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author QianJH
 */
@Slf4j
@Service
public class PluginAliyunServiceImpl extends ServiceImpl<PluginAliyunMapper, PluginAliyun> implements PluginAliyunService {

    @Override
    public PluginAliyun mustGet(Long oemId) {
        PluginAliyun entity = getOne(new LambdaQueryWrapper<PluginAliyun>()
                .eq(PluginAliyun::getOemId, oemId)
        );
        if (entity == null) {
            log.error("未配置ZenAliyun ::: oemId={}", oemId);
            throw new SystemConfigException();
        }
        return entity;
    }
}
