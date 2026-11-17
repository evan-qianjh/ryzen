package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.Message;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.mapper.MessageMapper;
import com.qianjh.ryzen.model.ExecutorMessage;
import com.qianjh.ryzen.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public Message getByUk(ImType imType, String messageCode) {
        return getOne(new LambdaQueryWrapper<Message>()
                .eq(Message::getImType, imType)
                .eq(Message::getMessageCode, messageCode)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Message create(ExecutorMessage message) {
        Message entity = Message.builder()
                .imType(message.imType())
                .messageCode(message.messageCode())
                .direction(message.direction())

                .accountCode(message.accountCode())
                .conversationCode(message.conversationCode())
                .senderCode(message.senderCode())
                .senderAvatar(message.senderAvatar())

                .sentAt(DateTimeUtils.parseMillis(message.sentAt()))
                .content(message.content())
                .build();
        save(entity);
        return entity;
    }

}
