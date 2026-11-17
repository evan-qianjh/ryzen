package com.qianjh.ryzen.model;

import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.enums.MessageDirection;
import lombok.Builder;

/**
 * 执行器采集的消息
 *
 * @param executorCode     执行器
 * @param accountCode      账号
 * @param messageCode      消息
 * @param conversationCode 对话
 * @param senderCode       发送者
 * @param content          内容
 * @param sentAt           发送时间
 */
@Builder(toBuilder = true)
public record ExecutorMessage(
        ImType imType,
        MessageDirection direction,
        String executorCode,
        String accountCode,
        String messageCode,
        String conversationCode,
        String senderCode,
        String senderAvatar,
        Long sentAt,
        String content
) {
}
