package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ToSchedulerCommand;
import com.qianjh.ryzen.enums.SchedulerCommandType;
import com.qianjh.ryzen.enums.ToSchedulerCommandDomainType;
import com.qianjh.ryzen.mapper.ToSchedulerCommandMapper;
import com.qianjh.ryzen.service.ToSchedulerCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ToSchedulerCommandServiceImpl extends ServiceImpl<ToSchedulerCommandMapper, ToSchedulerCommand> implements ToSchedulerCommandService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ToSchedulerCommand create(Long oemId, Long tenantId, SchedulerCommandType commandType, ToSchedulerCommandDomainType domainType, Long domainId) {
        ToSchedulerCommand entity = ToSchedulerCommand.builder()
                .oemId(oemId)
                .tenantId(tenantId)
                .commandType(commandType)
                .domainId(domainId)
                .domainType(domainType)
                .success(null)
                .errorMessage(null)
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ToSchedulerCommand create(ImAccount domain, SchedulerCommandType commandType) {
        return create(domain.getOemId(), domain.getTenantId(), commandType, ToSchedulerCommandDomainType.IM_ACCOUNT, domain.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean response(ToSchedulerCommand entity, Boolean success, String errorMessage) {
        return lambdaUpdate()
                .set(ToSchedulerCommand::getSuccess, success)
                .set(ToSchedulerCommand::getErrorMessage, errorMessage)
                //
                .eq(ToSchedulerCommand::getId, entity.getId())
                .eq(ToSchedulerCommand::getOemId, entity.getOemId())
                .eq(ToSchedulerCommand::getTenantId, entity.getTenantId())
                .update();
    }
}
