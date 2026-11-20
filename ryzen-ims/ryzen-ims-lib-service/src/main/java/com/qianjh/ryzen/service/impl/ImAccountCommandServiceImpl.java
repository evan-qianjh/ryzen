package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImAccountCommand;
import com.qianjh.ryzen.mapper.ImAccountCommandMapper;
import com.qianjh.ryzen.service.ImAccountCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImAccountCommandServiceImpl extends ServiceImpl<ImAccountCommandMapper, ImAccountCommand> implements ImAccountCommandService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ImAccountCommand create(ImAccount imAccount, String command) {
        ImAccountCommand entity = ImAccountCommand.builder()
                .oemId(imAccount.getOemId())
                .tenantId(imAccount.getTenantId())
                .imAccountId(imAccount.getId())
                .command(command)
                .success(null)
                .errorMessage(null)
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean callback(ImAccountCommand entity, Boolean success, String errorMessage) {
        return lambdaUpdate()
                .set(ImAccountCommand::getSuccess, success)
                .set(ImAccountCommand::getErrorMessage, errorMessage)
                //
                .eq(ImAccountCommand::getId, entity.getId())
                .eq(ImAccountCommand::getOemId, entity.getOemId())
                .eq(ImAccountCommand::getTenantId, entity.getTenantId())
                .update();
    }
}
