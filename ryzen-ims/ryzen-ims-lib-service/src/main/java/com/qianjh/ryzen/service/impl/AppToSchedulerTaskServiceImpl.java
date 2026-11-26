package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.AppToSchedulerTask;
import com.qianjh.ryzen.ims.enums.ToSchedulerTaskStatus;
import com.qianjh.ryzen.mapper.AppToSchedulerTaskMapper;
import com.qianjh.ryzen.service.AppToSchedulerTaskService;
import com.qianjh.ryzen.service.dto.AppToSchedulerTaskContent;
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
                .status(ToSchedulerTaskStatus.PENDING)
                .build();
        save(entity);
        return entity;
    }
}
