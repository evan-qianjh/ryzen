package com.qianjh.ryzen.plugin.aliyun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.plugin.aliyun.entity.Aliyun;
import com.qianjh.ryzen.framework.common.exception.SystemConfigException;
import com.qianjh.ryzen.plugin.aliyun.mapper.AliyunMapper;
import com.qianjh.ryzen.plugin.aliyun.service.AliyunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author QianJH
 */
@Slf4j
@Service
public class AliyunServiceImpl extends ServiceImpl<AliyunMapper, Aliyun> implements AliyunService {

    @Override
    public Aliyun mustGet(Long oemId) {
        Aliyun entity = getOne(new LambdaQueryWrapper<Aliyun>()
                .eq(Aliyun::getOemId, oemId)
        );
        if (entity == null) {
            log.error("未配置ZenAliyun ::: oemId={}", oemId);
            throw new SystemConfigException();
        }
        return entity;
    }
}
