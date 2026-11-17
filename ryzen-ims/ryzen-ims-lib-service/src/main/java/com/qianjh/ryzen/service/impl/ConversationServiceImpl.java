package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.Conversation;
import com.qianjh.ryzen.mapper.ConversationMapper;
import com.qianjh.ryzen.service.ConversationService;
import org.springframework.stereotype.Service;

@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements ConversationService {
    @Override
    public Conversation getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getOemId, oemId)
                .eq(Conversation::getTenantId, tenantId)
                .eq(Conversation::getId, id)
        );
    }
}
