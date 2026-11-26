package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.entity.AppToSchedulerTask;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.ims.stream.app_to_scheduler.task.AppToSchedulerTaskRequest;
import com.qianjh.ryzen.service.AppToSchedulerService;
import com.qianjh.ryzen.service.AppToSchedulerTaskService;
import com.qianjh.ryzen.service.dto.AppToSchedulerTaskContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppToSchedulerServiceImpl implements AppToSchedulerService {

    private final AppToSchedulerTaskService toSchedulerTaskService;
    private final AppToSchedulerTaskRequest.Publisher appToSchedulerTaskRequestPublisher;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AppToSchedulerTask createTask(ServiceAccount serviceAccount, ImAccount imAccount, ImConversation imConversation, AppToSchedulerTaskContent content) {

        // create
        AppToSchedulerTask task = toSchedulerTaskService.create(imAccount, imConversation, content);

        // 生成一个随机码+内容发给调度器
        appToSchedulerTaskRequestPublisher.publish(
                AppToSchedulerTaskRequest.Body.builder()
                        .code(IdUtils.toString(task.getId()))
                        .assignmentId(imAccount.getExecutorAssignmentId())
                        .conversationCode(imConversation.getCode())
                        .content(content.content())
                        .build()
        );

        return task;
    }
}
