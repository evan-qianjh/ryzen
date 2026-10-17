package com.qianjh.ryzen.plugin.sfexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.framework.common.exception.SystemConfigException;
import com.qianjh.ryzen.plugin.sfexpress.entity.PluginSFExpress;
import com.qianjh.ryzen.plugin.sfexpress.mapper.PluginSFExpressMapper;
import com.qianjh.ryzen.plugin.sfexpress.service.PluginSFExpressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author QianJH
 */
@Slf4j
@Service
public class PluginSFExpressServiceImpl extends ServiceImpl<PluginSFExpressMapper, PluginSFExpress> implements PluginSFExpressService {

    @Override
    public PluginSFExpress mustGet(Long oemId) {
        PluginSFExpress entity = getOne(new LambdaQueryWrapper<PluginSFExpress>()
                .eq(PluginSFExpress::getOemId, oemId)
        );
        if (entity == null) {
            log.error("未配置SFExpress ::: oemId={}", oemId);
            throw new SystemConfigException();
        }
        return entity;
    }
}
