package com.qianjh.ryzen.controller.tenant.app.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record GetConversationsResp(
        String id,
        String name,
        String avatar,
        String lastMessageContent
) {
}
