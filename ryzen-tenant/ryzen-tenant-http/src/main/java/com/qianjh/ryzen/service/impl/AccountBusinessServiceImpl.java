package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.AccountBusiness;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.mapper.AccountBusinessMapper;
import com.qianjh.ryzen.service.AccountBusinessService;
import com.qianjh.ryzen.service.dto.TenantPatchAccountBusinessReq;
import com.qianjh.ryzen.service.dto.TenantPostAccountBusinessReq;
import com.qianjh.ryzen.tenant.enums.AccountBusinessStatus;
import com.qianjh.ryzen.tenant.enums.AccountBusinessType;
import com.qianjh.ryzen.tenant.stream.topic.account.domain.account_business.type.AccountBusinessCreated;
import com.qianjh.ryzen.tenant.stream.topic.account.domain.account_business.type.AccountBusinessPatched;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 *
 * @author QianJH
 */
@Service
@RequiredArgsConstructor
public class AccountBusinessServiceImpl extends ServiceImpl<AccountBusinessMapper, AccountBusiness> implements AccountBusinessService {
    private final AccountBusinessCreated.Publisher accountBusinessCreatedPublisher;
    private final AccountBusinessPatched.Publisher accountBusinessPatchedPublisher;

    @Override
    public AccountBusiness getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<AccountBusiness>()
                .eq(AccountBusiness::getOemId, oemId)
                .eq(AccountBusiness::getTenantId, tenantId)
                .eq(AccountBusiness::getId, id)
        );
    }

    @Override
    public AccountBusiness getByUk(Long oemId, Long tenantId, Long accountId, AccountBusinessType type) {
        return getOne(new LambdaQueryWrapper<AccountBusiness>()
                .eq(AccountBusiness::getOemId, oemId)
                .eq(AccountBusiness::getTenantId, tenantId)
                .eq(AccountBusiness::getAccountId, accountId)
                .eq(AccountBusiness::getType, type)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AccountBusiness create(Long oemId, Long tenantId, TenantPostAccountBusinessReq body, Operator operator) {
        AccountBusiness entity = AccountBusiness.builder()
                .oemId(oemId)
                .tenantId(tenantId)
                .accountId(body.accountId())
                .type(body.type())
                .status(AccountBusinessStatus.ENABLED)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        accountBusinessCreatedPublisher.publish(
                AccountBusinessCreated.Body.builder()
                        .oemId(oemId)
                        .tenantId(tenantId)
                        .id(entity.getId())
                        .accountId(entity.getAccountId())
                        .type(entity.getType())
                        .status(entity.getStatus())
                        .build()
        );
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean patch(AccountBusiness entity, TenantPatchAccountBusinessReq body, Operator operator) {
        boolean success = lambdaUpdate()
                .set(body.status() != null, AccountBusiness::getStatus, body.status())
                //
                .eq(AccountBusiness::getOemId, entity.getOemId())
                .eq(AccountBusiness::getTenantId, entity.getTenantId())
                .eq(AccountBusiness::getId, entity.getId())
                .update();

        if (success) {
            accountBusinessPatchedPublisher.publish(
                    AccountBusinessPatched.Body.builder()
                            .oemId(entity.getOemId())
                            .tenantId(entity.getTenantId())
                            .id(entity.getId())
                            .accountId(entity.getAccountId())
                            .type(entity.getType())
                            .status(entity.getStatus())
                            .build()
            );
        }

        return success;
    }
}
