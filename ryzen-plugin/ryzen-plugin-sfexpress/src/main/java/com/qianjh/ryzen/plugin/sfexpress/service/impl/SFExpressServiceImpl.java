package com.qianjh.ryzen.plugin.sfexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.framework.common.exception.SystemConfigException;
import com.qianjh.ryzen.plugin.sfexpress.entity.SFExpress;
import com.qianjh.ryzen.plugin.sfexpress.mapper.SFExpressMapper;
import com.qianjh.ryzen.plugin.sfexpress.service.SFExpressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author QianJH
 */
@Slf4j
@Service
public class SFExpressServiceImpl extends ServiceImpl<SFExpressMapper, SFExpress> implements SFExpressService {

    @Override
    public SFExpress mustGet(Long oemId) {
        SFExpress entity = getOne(new LambdaQueryWrapper<SFExpress>()
                .eq(SFExpress::getOemId, oemId)
        );
        if (entity == null) {
            log.error("未配置SFExpress ::: oemId={}", oemId);
            throw new SystemConfigException();
        }
        return entity;
    }
}
