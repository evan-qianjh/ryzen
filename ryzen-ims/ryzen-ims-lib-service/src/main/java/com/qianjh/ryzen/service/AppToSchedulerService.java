package com.qianjh.ryzen.service;

import com.qianjh.ryzen.entity.AppToSchedulerTask;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.service.dto.AppToSchedulerTaskContent;

public interface AppToSchedulerService {

    AppToSchedulerTask createTask(ServiceAccount serviceAccount, ImAccount imAccount, ImConversation imConversation, AppToSchedulerTaskContent content);
}
