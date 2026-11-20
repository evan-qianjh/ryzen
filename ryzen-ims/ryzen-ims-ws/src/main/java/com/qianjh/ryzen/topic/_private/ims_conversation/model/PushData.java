package com.qianjh.ryzen.topic._private.ims_conversation.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record PushData(
        String id,
        ConversationAction action
) {

}
