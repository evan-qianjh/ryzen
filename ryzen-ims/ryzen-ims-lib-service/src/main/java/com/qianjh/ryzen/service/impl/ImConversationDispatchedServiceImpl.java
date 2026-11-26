package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImConversationDispatched;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.ims.enums.ImConversationDispatchType;
import com.qianjh.ryzen.ims.stream.im_conversation.im_conversation_dispatched.ImConversationDispatchedCreated;
import com.qianjh.ryzen.mapper.ImConversationDispatchedMapper;
import com.qianjh.ryzen.service.ImConversationDispatchedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ImConversationDispatchedServiceImpl extends ServiceImpl<ImConversationDispatchedMapper, ImConversationDispatched> implements ImConversationDispatchedService {

    private final ImConversationDispatchedCreated.Publisher publisher;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ImConversationDispatched create(ImConversation conversation, ServiceAccount serviceAccount,
                                           ImConversationDispatchType type, String remark, Long beforeServiceAccountId) {
        ImConversationDispatched entity = ImConversationDispatched.builder()
                .oemId(conversation.getOemId())
                .tenantId(conversation.getTenantId())
                .imConversationId(conversation.getId())
                .serviceAccountId(serviceAccount.getId())
                .beforeServiceAccountId(beforeServiceAccountId)
                .type(type)
                .remark(remark)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);

        publisher.publish(ImConversationDispatchedCreated.Body.builder()
                .id(entity.getId())
                .oemId(entity.getOemId())
                .tenantId(entity.getTenantId())
                .imConversationId(entity.getImConversationId())
                .serviceAccountId(entity.getServiceAccountId())
                .beforeServiceAccountId(entity.getBeforeServiceAccountId())
                .type(entity.getType())
                .remark(remark)
                .build()
        );

        return entity;
    }
}
