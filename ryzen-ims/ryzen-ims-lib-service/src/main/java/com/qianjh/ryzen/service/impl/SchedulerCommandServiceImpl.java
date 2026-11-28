package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.SchedulerCommand;
import com.qianjh.ryzen.ims.enums.SchedulerCommandType;
import com.qianjh.ryzen.ims.enums.SchedulerCommandDomainType;
import com.qianjh.ryzen.mapper.SchedulerCommandMapper;
import com.qianjh.ryzen.service.SchedulerCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchedulerCommandServiceImpl extends ServiceImpl<SchedulerCommandMapper, SchedulerCommand> implements SchedulerCommandService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SchedulerCommand create(Long oemId, Long tenantId, SchedulerCommandType commandType, SchedulerCommandDomainType domainType, Long domainId) {
        SchedulerCommand entity = SchedulerCommand.builder()
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
    public SchedulerCommand create(ImAccount domain, SchedulerCommandType commandType) {
        return create(domain.getOemId(), domain.getTenantId(), commandType, SchedulerCommandDomainType.IM_ACCOUNT, domain.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean response(SchedulerCommand entity, Boolean success, String errorMessage) {
        return lambdaUpdate()
                .set(SchedulerCommand::getSuccess, success)
                .set(SchedulerCommand::getErrorMessage, errorMessage)
                //
                .eq(SchedulerCommand::getId, entity.getId())
                .eq(SchedulerCommand::getOemId, entity.getOemId())
                .eq(SchedulerCommand::getTenantId, entity.getTenantId())
                .update();
    }
}
