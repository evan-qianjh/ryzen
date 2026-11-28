package com.qianjh.ryzen.service;

import com.qianjh.ryzen.entity.*;
import com.qianjh.ryzen.ims.enums.SchedulerCommandType;
import com.qianjh.ryzen.service.dto.AppToSchedulerTaskContent;

public interface SchedulerService {

    SchedulerCommand createCommand(ImAccount imAccount, SchedulerCommandType commandType);

    SchedulerTask createTask(ServiceAccount serviceAccount, ImAccount imAccount, ImConversation imConversation, AppToSchedulerTaskContent content);
}
