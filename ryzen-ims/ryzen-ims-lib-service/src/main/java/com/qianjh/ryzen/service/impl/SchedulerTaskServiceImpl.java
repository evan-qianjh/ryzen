package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.SchedulerTask;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.ims.enums.SchedulerTaskStatus;
import com.qianjh.ryzen.ims.stream.scheduler.scheduler_task.SchedulerTaskCreated;
import com.qianjh.ryzen.mapper.SchedulerTaskMapper;
import com.qianjh.ryzen.service.SchedulerTaskService;
import com.qianjh.ryzen.service.dto.AppToSchedulerTaskContent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchedulerTaskServiceImpl extends ServiceImpl<SchedulerTaskMapper, SchedulerTask> implements SchedulerTaskService {

    private final SchedulerTaskCreated.Publisher schedulerTaskCreatedPublisher;

    @Override
    public SchedulerTask getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<SchedulerTask>()
                .eq(SchedulerTask::getOemId, oemId)
                .eq(SchedulerTask::getTenantId, tenantId)
                .eq(SchedulerTask::getId, id)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SchedulerTask create(ServiceAccount serviceAccount, ImAccount imAccount, ImConversation imConversation, AppToSchedulerTaskContent content) {
        SchedulerTask entity = SchedulerTask.builder()
                .oemId(imAccount.getOemId())
                .tenantId(imAccount.getTenantId())
                .executorAssignmentId(imAccount.getExecutorAssignmentId())
                .conversationId(imConversation.getId())
                .content(content.content())
                .status(SchedulerTaskStatus.PENDING)
                .serviceAccountId(serviceAccount.getId())
                .build();
        save(entity);

        schedulerTaskCreatedPublisher.publish(
                SchedulerTaskCreated.Body.builder()
                        .id(entity.getId())
                        .oemId(entity.getOemId())
                        .tenantId(entity.getTenantId())
                        .build()
        );
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateStatus(SchedulerTask entity, SchedulerTaskStatus before, SchedulerTaskStatus after,
                                String imReceiptCode, String errorMessage, Operator operator) {
        return lambdaUpdate()
                .set(SchedulerTask::getStatus, after)
                .set(StringUtils.isNotBlank(imReceiptCode), SchedulerTask::getImReceiptCode, imReceiptCode)
                .set(StringUtils.isNotBlank(errorMessage), SchedulerTask::getResponseMessage, errorMessage)
                //
                .eq(SchedulerTask::getId, entity.getId())
                .eq(SchedulerTask::getOemId, entity.getOemId())
                .eq(SchedulerTask::getTenantId, entity.getTenantId())
                .eq(SchedulerTask::getStatus, before)
                .update();
    }
}
