package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.mapper.MatchTagMapper;
import com.qianjh.ryzen.service.MatchTagService;
import com.qianjh.ryzen.service.dto.TenantPatchMatchTagReq;
import com.qianjh.ryzen.service.dto.TenantPostMatchTagReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class MatchTagServiceImpl extends ServiceImpl<MatchTagMapper, MatchTag> implements MatchTagService {

    @Override
    public MatchTag getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<MatchTag>()
                .eq(MatchTag::getOemId, oemId)
                .eq(MatchTag::getTenantId, tenantId)
                .eq(MatchTag::getId, id)
        );
    }

    @Override
    public MatchTag getByUk(Long oemId, Long tenantId, String tag) {
        return getOne(new LambdaQueryWrapper<MatchTag>()
                .eq(MatchTag::getOemId, oemId)
                .eq(MatchTag::getTenantId, tenantId)
                .eq(MatchTag::getTag, tag)
        );
    }

    @Override
    public List<MatchTag> listByIds(Long oemId, Long tenantId, List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return list(new LambdaQueryWrapper<MatchTag>()
                .eq(MatchTag::getOemId, oemId)
                .eq(MatchTag::getTenantId, tenantId)
                .in(MatchTag::getId, ids)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MatchTag create(Long oemId, Long tenantId, TenantPostMatchTagReq body) {
        MatchTag entity = MatchTag.builder()
                .oemId(oemId)
                .tenantId(tenantId)
                .tag(body.tag())
                .groupName(body.group())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean patch(MatchTag entity, TenantPatchMatchTagReq body) {
        return lambdaUpdate()
                .set(body.status() != null, MatchTag::getStatus, body.status())
                .set(body.group() != null, MatchTag::getGroupName, body.group())
                //
                .eq(MatchTag::getOemId, entity.getId())
                .eq(MatchTag::getTenantId, entity.getTenantId())
                .eq(MatchTag::getId, entity.getId())
                .update();
    }
}
