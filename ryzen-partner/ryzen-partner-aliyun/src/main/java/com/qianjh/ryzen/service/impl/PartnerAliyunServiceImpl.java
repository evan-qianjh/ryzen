package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.PartnerAliyun;
import com.qianjh.ryzen.exception.SystemConfigException;
import com.qianjh.ryzen.mapper.PartnerAliyunMapper;
import com.qianjh.ryzen.service.PartnerAliyunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author QianJH
 */
@Slf4j
@Service
public class PartnerAliyunServiceImpl extends ServiceImpl<PartnerAliyunMapper, PartnerAliyun> implements PartnerAliyunService {

    @Override
    public PartnerAliyun mustGet(Long tenantId) {
        PartnerAliyun entity = getOne(new LambdaQueryWrapper<PartnerAliyun>()
                .eq(PartnerAliyun::getTenantId, tenantId)
        );
        if (entity == null) {
            log.error("未配置ZenAliyun ::: tenantId={}", tenantId);
            throw new SystemConfigException();
        }
        return entity;
    }
}
