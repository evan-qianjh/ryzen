package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.model.SchedulerMessage;

public interface ImMessageService extends IService<ImMessage> {

    ImMessage getById(Long oemId, Long tenantId, Long id);

    ImMessage getByUk(Long oemId, Long tenantId, Long conversationId, String code);

    ImMessage create(ImAccount imAccount, ImConversation conversation, SchedulerMessage schedulerMessage);

    ImMessage createIfAbsent(ImAccount imAccount, ImConversation conversation, SchedulerMessage schedulerMessage);
}
