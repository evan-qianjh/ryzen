package com.qianjh.ryzen.topic._private.ims_message.model;

import lombok.Builder;

import java.util.List;


@Builder(toBuilder = true)
public record PushData(
        String id,
        String seq,
        String direction,
        String senderName,
        String senderAvatar,
        String content,
        Long sentTime,
        Conversation conversation
) {

    @Builder(toBuilder = true)
    public record Conversation(
            String id,
            String imType,
            String name,
            String avatar,
            List<String> matchTags
    ) {

    }
}
