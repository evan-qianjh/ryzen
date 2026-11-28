package com.qianjh.ryzen.topic._private.ims_conversation.model;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record PushData(
        String id,
        String imType,
        String title,
        String subTitle,
        String avatar,
        String lastMessageContent,
        String lastMessageContentType,
        String lastMessageId,
        Long lastMessageTime,
        List<String> matchTags
) {

}
