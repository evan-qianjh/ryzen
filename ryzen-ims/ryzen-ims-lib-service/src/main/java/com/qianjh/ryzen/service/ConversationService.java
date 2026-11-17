package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.Conversation;

public interface ConversationService extends IService<Conversation> {
    Conversation getById(Long oemId, Long tenantId, Long id);
}
