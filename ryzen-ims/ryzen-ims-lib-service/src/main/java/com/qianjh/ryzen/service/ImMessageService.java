package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.type.SchedulerToAppReportMessage;

public interface ImMessageService extends IService<ImMessage> {

    ImMessage getById(Long oemId, Long tenantId, Long id);

    ImMessage getByUk(Long oemId, Long tenantId, Long conversationId, String code);

    ImMessage create(ImAccount imAccount, ImConversation conversation, SchedulerToAppReportMessage.Body message);

    ImMessage createIfAbsent(ImAccount imAccount, ImConversation conversation, SchedulerToAppReportMessage.Body message);
}
