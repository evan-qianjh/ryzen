package com.qianjh.ryzen.service;

import com.qianjh.ryzen.entity.*;
import com.qianjh.ryzen.ims.enums.SchedulerCommandType;
import com.qianjh.ryzen.service.dto.AppToSchedulerTaskContent;

public interface AppToSchedulerService {

    AppToSchedulerCommand createCommand(ImAccount imAccount, SchedulerCommandType commandType);

    AppToSchedulerTask createTask(ServiceAccount serviceAccount, ImAccount imAccount, ImConversation imConversation, AppToSchedulerTaskContent content);
}
