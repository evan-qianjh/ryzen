package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.enums.MessageDirection;
import lombok.Builder;

@Builder(toBuilder = true)
public record GetMessagesResp(
        String id,
        MessageDirection direction,
        String senderName,
        String senderAvatar,
        Long sentTime,
        String content
) {
}
