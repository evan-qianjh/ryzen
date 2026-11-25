package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.ims.enums.MessageDirection;
import lombok.Builder;

@Builder(toBuilder = true)
public record GetMessagesResp(
        String id,
        String seq,
        MessageDirection direction,
        String senderName,
        String senderAvatar,
        Long sentTime,
        String content
) {
}
