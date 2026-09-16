package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.Aliyun;
import com.qianjh.ryzen.exception.SystemConfigException;
import com.qianjh.ryzen.mapper.AliyunMapper;
import com.qianjh.ryzen.service.AliyunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author QianJH
 */
@Slf4j
@Service
public class AliyunServiceImpl extends ServiceImpl<AliyunMapper, Aliyun> implements AliyunService {

    @Override
    public Aliyun mustGet(Long tenantId) {
        Aliyun entity = getOne(new LambdaQueryWrapper<Aliyun>()
                .eq(Aliyun::getOemId, tenantId)
        );
        if (entity == null) {
            log.error("未配置ZenAliyun ::: tenantId={}", tenantId);
            throw new SystemConfigException();
        }
        return entity;
    }
}
