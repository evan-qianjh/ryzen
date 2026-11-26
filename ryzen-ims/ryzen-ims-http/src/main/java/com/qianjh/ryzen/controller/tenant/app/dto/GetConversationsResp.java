package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.ims.enums.ImType;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record GetConversationsResp(
        String id,
        ImType imType,
        String title,
        String subTitle,
        String avatar,
        String lastMessageContent,
        String lastMessageId,
        Long lastMessageTime,
        List<String> matchTags
) {
}
