package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;

public interface ImConversationService extends IService<ImConversation> {
    ImConversation getById(Long oemId, Long tenantId, Long id);

    ImConversation getByUk(Long oemId, Long tenantId, Long imAccountId, String code);

    ImConversation create(ImAccount imAccount, String code);

    ImConversation createIfAbsent(ImAccount imAccount, String code);

    boolean recordLastMessage(ImConversation entity, ImMessage message);
}
