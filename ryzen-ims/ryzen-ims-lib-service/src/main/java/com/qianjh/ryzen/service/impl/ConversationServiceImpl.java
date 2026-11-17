package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.Conversation;
import com.qianjh.ryzen.mapper.ConversationMapper;
import com.qianjh.ryzen.service.ConversationService;
import org.springframework.stereotype.Service;

@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements ConversationService {
}
