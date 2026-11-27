package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.AppToSchedulerTask;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.ims.enums.AppToSchedulerTaskStatus;
import com.qianjh.ryzen.mapper.AppToSchedulerTaskMapper;
import com.qianjh.ryzen.service.AppToSchedulerTaskService;
import com.qianjh.ryzen.service.dto.AppToSchedulerTaskContent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppToSchedulerTaskServiceImpl extends ServiceImpl<AppToSchedulerTaskMapper, AppToSchedulerTask> implements AppToSchedulerTaskService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AppToSchedulerTask create(ImAccount imAccount, ImConversation imConversation, AppToSchedulerTaskContent content) {
        AppToSchedulerTask entity = AppToSchedulerTask.builder()
                .oemId(imAccount.getOemId())
                .tenantId(imAccount.getTenantId())
                .executorAssignmentId(imAccount.getExecutorAssignmentId())
                .conversationId(imConversation.getId())
                .content(content.content())
                .status(AppToSchedulerTaskStatus.PENDING)
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateStatus(AppToSchedulerTask entity, AppToSchedulerTaskStatus before, AppToSchedulerTaskStatus after, String errorMessage, Operator operator) {
        return lambdaUpdate()
                .set(AppToSchedulerTask::getStatus, after)
                .set(StringUtils.isNotBlank(errorMessage), AppToSchedulerTask::getResponseMessage, errorMessage)
                //
                .eq(AppToSchedulerTask::getId, entity.getId())
                .eq(AppToSchedulerTask::getOemId, entity.getOemId())
                .eq(AppToSchedulerTask::getTenantId, entity.getTenantId())
                .eq(AppToSchedulerTask::getStatus, before)
                .update();
    }
}
