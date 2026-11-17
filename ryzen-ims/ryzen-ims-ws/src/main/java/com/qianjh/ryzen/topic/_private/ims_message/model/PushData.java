package com.qianjh.ryzen.topic._private.ims_message.model;

import com.qianjh.ryzen.enums.MessageDirection;
import lombok.Builder;

@Builder
public record PushData(
        MessageDirection direction,
        String senderName,
        String senderAvatar,
        String content
) {
}
