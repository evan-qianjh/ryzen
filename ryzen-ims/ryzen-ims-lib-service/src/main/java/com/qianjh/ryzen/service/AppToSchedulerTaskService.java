package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.AppToSchedulerTask;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.ims.enums.AppToSchedulerTaskStatus;
import com.qianjh.ryzen.service.dto.AppToSchedulerTaskContent;

public interface AppToSchedulerTaskService extends IService<AppToSchedulerTask> {

    AppToSchedulerTask create(ImAccount imAccount, ImConversation imConversation, AppToSchedulerTaskContent content);

    boolean updateStatus(AppToSchedulerTask entity, AppToSchedulerTaskStatus before, AppToSchedulerTaskStatus after, String errorMessage, Operator operator);
}
