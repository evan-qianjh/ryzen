package com.qianjh.ryzen.ims.model;

import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.enums.MessageDirection;
import lombok.Builder;

/**
 * 调度器采集的消息
 *
 * @param accountCode      账号
 * @param messageCode      消息
 * @param conversationCode 对话
 * @param senderCode       发送者
 * @param content          内容
 * @param sentTime         发送时间
 */
@Builder(toBuilder = true)
public record SchedulerMessage(
        ImType imType,
        String accountCode,
        String messageCode,
        MessageDirection direction,

        String conversationCode,
        String conversationTitle,
        String conversationSubTitle,
        String conversationAvatar,
        String senderCode,
        String senderName,
        String senderAvatar,
        String content,
        Long sentTime
) {
}
