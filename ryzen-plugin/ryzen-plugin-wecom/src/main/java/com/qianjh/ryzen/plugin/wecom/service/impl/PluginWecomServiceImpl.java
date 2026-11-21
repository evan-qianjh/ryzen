package com.qianjh.ryzen.plugin.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.framework.common.exception.SystemConfigException;
import com.qianjh.ryzen.plugin.wecom.entity.PluginWecom;
import com.qianjh.ryzen.plugin.wecom.mapper.PluginWecomMapper;
import com.qianjh.ryzen.plugin.wecom.service.PluginWecomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PluginWecomServiceImpl extends ServiceImpl<PluginWecomMapper, PluginWecom> implements PluginWecomService {

    @Override
    public PluginWecom getById(Long oemId, Long id) {
        return getOne(new LambdaQueryWrapper<PluginWecom>()
                .eq(PluginWecom::getOemId, oemId)
                .eq(PluginWecom::getId, id)
        );
    }

    @Override
    public PluginWecom getById(Long oemId, Long id, boolean assertNotNull) {
        PluginWecom entity = getById(oemId, id);
        if (assertNotNull && entity == null) {
            log.error("未配置PluginWecom ::: oemId={}, id={}", oemId, id);
            throw new SystemConfigException();
        }
        return null;
    }

    @Override
    public PluginWecom getByCorp(Long oemId, String corpCode) {
        return getOne(new LambdaQueryWrapper<PluginWecom>()
                .eq(PluginWecom::getOemId, oemId)
                .eq(PluginWecom::getCorpCode, corpCode)
        );
    }
}
