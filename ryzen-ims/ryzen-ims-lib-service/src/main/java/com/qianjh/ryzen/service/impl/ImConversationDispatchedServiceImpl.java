package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImConversationDispatched;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.ims.enums.ImConversationDispatchType;
import com.qianjh.ryzen.ims.stream.domain.im_conversation_dispatched.type.ImConversationDispatchedCreatedEvent;
import com.qianjh.ryzen.mapper.ImConversationDispatchedMapper;
import com.qianjh.ryzen.service.ImConversationDispatchedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ImConversationDispatchedServiceImpl extends ServiceImpl<ImConversationDispatchedMapper, ImConversationDispatched> implements ImConversationDispatchedService {

    private final ImConversationDispatchedCreatedEvent.Publisher publisher;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ImConversationDispatched create(ImConversation conversation, ServiceAccount serviceAccount, ImConversationDispatchType type, Long beforeServiceAccountId) {
        ImConversationDispatched entity = ImConversationDispatched.builder()
                .oemId(conversation.getOemId())
                .tenantId(conversation.getTenantId())
                .imConversationId(conversation.getId())
                .serviceAccountId(serviceAccount.getId())
                .beforeServiceAccountId(beforeServiceAccountId)
                .type(type)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);

        publisher.publish(ImConversationDispatchedCreatedEvent.Body.builder()
                .oemId(entity.getOemId())
                .tenantId(entity.getTenantId())
                .id(entity.getId())
                .build()
        );

        return entity;
    }
}
