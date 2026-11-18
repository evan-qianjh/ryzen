package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.enums.ImAccountStatus;
import com.qianjh.ryzen.mapper.ImAccountMapper;
import com.qianjh.ryzen.service.ImAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImAccountServiceImpl extends ServiceImpl<ImAccountMapper, ImAccount> implements ImAccountService {
    @Override
    public ImAccount getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<ImAccount>()
                .eq(ImAccount::getOemId, oemId)
                .eq(ImAccount::getTenantId, tenantId)
                .eq(ImAccount::getId, id)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateStatus(ImAccount entity, ImAccountStatus before, ImAccountStatus after) {
        return lambdaUpdate()
                .set(ImAccount::getStatus, after)
                //
                .eq(ImAccount::getOemId, entity.getOemId())
                .eq(ImAccount::getTenantId, entity.getTenantId())
                .eq(ImAccount::getId, entity.getId())
                //
                .eq(ImAccount::getStatus, before)
                .update();
    }
}
