package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.SchedulerMessage;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerReportMessage;
import com.qianjh.ryzen.mapper.SchedulerMessageMapper;
import com.qianjh.ryzen.service.SchedulerMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchedulerMessageServiceImpl extends ServiceImpl<SchedulerMessageMapper, SchedulerMessage> implements SchedulerMessageService {

    @Override
    public SchedulerMessage getByUk(ImType imType, String messageCode) {
        return getOne(new LambdaQueryWrapper<SchedulerMessage>()
                .eq(SchedulerMessage::getImType, imType)
                .eq(SchedulerMessage::getMessageCode, messageCode)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SchedulerMessage create(ExecutorToSchedulerReportMessage.Body message) {
        SchedulerMessage entity = SchedulerMessage.builder()
                .imType(message.imType())
                .messageCode(message.messageCode())
                .direction(message.direction())
                .accountCode(message.accountCode())

                .conversationCode(message.conversationCode())
                .conversationTitle(message.conversationTitle())
                .conversationSubTitle(message.conversationSubTitle())
                .conversationAvatar(message.conversationAvatar())

                .senderCode(message.senderCode())
                .senderName(message.senderName())
                .senderAvatar(message.senderAvatar())
                .sentTime(DateTimeUtils.parseMillis(message.sentTime()))
                .content(message.content())
                .contentType(message.contentType())
                .build();
        save(entity);
        return entity;
    }

}
