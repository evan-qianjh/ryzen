package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.entity.*;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.ims.enums.SchedulerCommandType;
import com.qianjh.ryzen.ims.stream.app_to_scheduler.command.AppToSchedulerCommandRequest;
import com.qianjh.ryzen.ims.stream.app_to_scheduler.task.AppToSchedulerTaskRequest;
import com.qianjh.ryzen.service.AppToSchedulerCommandService;
import com.qianjh.ryzen.service.AppToSchedulerService;
import com.qianjh.ryzen.service.AppToSchedulerTaskService;
import com.qianjh.ryzen.service.dto.AppToSchedulerTaskContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppToSchedulerServiceImpl implements AppToSchedulerService {

    private final AppToSchedulerTaskService toSchedulerTaskService;
    private final AppToSchedulerTaskRequest.Publisher appToSchedulerTaskRequestPublisher;
    private final AppToSchedulerCommandService appToSchedulerCommandService;
    private final AppToSchedulerCommandRequest.Publisher appToSchedulerCommandRequestPublisher;

    @Override
    public AppToSchedulerCommand createCommand(ImAccount imAccount, SchedulerCommandType commandType) {

        // create
        AppToSchedulerCommand command = appToSchedulerCommandService.create(imAccount, commandType);

        // publish
        appToSchedulerCommandRequestPublisher.publish(
                AppToSchedulerCommandRequest.Body.builder()
                        .code(IdUtils.toString(command.getId()))
                        .type(commandType)
                        .imType(imAccount.getImType())
                        .accountCode(imAccount.getCode())
                        .assignmentId(imAccount.getExecutorAssignmentId())
                        .build()
        );

        return command;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AppToSchedulerTask createTask(ServiceAccount serviceAccount, ImAccount imAccount, ImConversation imConversation, AppToSchedulerTaskContent content) {

        // create
        AppToSchedulerTask task = toSchedulerTaskService.create(serviceAccount, imAccount, imConversation, content);

        log.info("创建任务 ::: task={}", task);

        //
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
