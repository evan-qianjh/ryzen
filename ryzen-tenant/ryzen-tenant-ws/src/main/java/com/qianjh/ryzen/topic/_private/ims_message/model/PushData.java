package com.qianjh.ryzen.topic._private.ims_message.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record PushData(
        String id,
        String seq,
        String direction,
        String senderName,
        String senderAvatar,
        String content,
        Conversation conversation
) {

    @Builder(toBuilder = true)
    public record Conversation(
            String id,
            String avatar,
            String name
    ) {

    }
}
