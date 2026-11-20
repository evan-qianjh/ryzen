package com.qianjh.ryzen.model;

import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.enums.MessageDirection;
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
        MessageDirection direction,
        String accountCode,
        String messageCode,
        String conversationCode,
        String senderCode,
        String senderAvatar,
        String content,
        Long sentTime
) {
}
