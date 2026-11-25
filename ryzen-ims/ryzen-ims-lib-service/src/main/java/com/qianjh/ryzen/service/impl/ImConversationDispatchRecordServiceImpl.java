package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImConversationDispatchRecord;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.ims.enums.ImConversationDispatchType;
import com.qianjh.ryzen.mapper.ImConversationDispatchRecordMapper;
import com.qianjh.ryzen.service.ImConversationDispatchRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ImConversationDispatchRecordServiceImpl extends ServiceImpl<ImConversationDispatchRecordMapper, ImConversationDispatchRecord> implements ImConversationDispatchRecordService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ImConversationDispatchRecord create(ImConversation conversation, ServiceAccount serviceAccount, ImConversationDispatchType type, Long beforeServiceAccountId) {
        ImConversationDispatchRecord entity = ImConversationDispatchRecord.builder()
                .oemId(conversation.getOemId())
                .tenantId(conversation.getTenantId())
                .imConversationId(conversation.getId())
                .serviceAccountId(serviceAccount.getId())
                .beforeServiceAccountId(beforeServiceAccountId)
                .type(type)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }
}
