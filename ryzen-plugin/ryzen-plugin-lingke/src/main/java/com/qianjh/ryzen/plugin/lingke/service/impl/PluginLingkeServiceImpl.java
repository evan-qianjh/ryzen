package com.qianjh.ryzen.plugin.lingke.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.framework.common.exception.SystemConfigException;
import com.qianjh.ryzen.plugin.lingke.entity.PluginLingke;
import com.qianjh.ryzen.plugin.lingke.mapper.PluginLingkeMapper;
import com.qianjh.ryzen.plugin.lingke.service.PluginLingkeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PluginLingkeServiceImpl extends ServiceImpl<PluginLingkeMapper, PluginLingke> implements PluginLingkeService {

    @Override
    public PluginLingke get(Long oemId, Long id) {
        return getOne(new LambdaQueryWrapper<PluginLingke>()
                .eq(PluginLingke::getOemId, oemId)
                .eq(PluginLingke::getId, id)
        );
    }

    @Override
    public PluginLingke mustGet(Long oemId, Long id) {
        PluginLingke entity = get(oemId, id);
        if (entity == null) {
            log.error("未配置PluginLingke ::: oemId={}, id={}", oemId, id);
            throw new SystemConfigException();
        }
        return entity;
    }
}
