package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.AppToSchedulerCommand;
import com.qianjh.ryzen.ims.enums.SchedulerCommandType;
import com.qianjh.ryzen.ims.enums.ToSchedulerCommandDomainType;
import com.qianjh.ryzen.mapper.AppToSchedulerCommandMapper;
import com.qianjh.ryzen.service.AppToSchedulerCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppToSchedulerCommandServiceImpl extends ServiceImpl<AppToSchedulerCommandMapper, AppToSchedulerCommand> implements AppToSchedulerCommandService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AppToSchedulerCommand create(Long oemId, Long tenantId, SchedulerCommandType commandType, ToSchedulerCommandDomainType domainType, Long domainId) {
        AppToSchedulerCommand entity = AppToSchedulerCommand.builder()
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
    public AppToSchedulerCommand create(ImAccount domain, SchedulerCommandType commandType) {
        return create(domain.getOemId(), domain.getTenantId(), commandType, ToSchedulerCommandDomainType.IM_ACCOUNT, domain.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean response(AppToSchedulerCommand entity, Boolean success, String errorMessage) {
        return lambdaUpdate()
                .set(AppToSchedulerCommand::getSuccess, success)
                .set(AppToSchedulerCommand::getErrorMessage, errorMessage)
                //
                .eq(AppToSchedulerCommand::getId, entity.getId())
                .eq(AppToSchedulerCommand::getOemId, entity.getOemId())
                .eq(AppToSchedulerCommand::getTenantId, entity.getTenantId())
                .update();
    }
}
