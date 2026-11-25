package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.ims.enums.ImType;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record GetConversationsResp(
        String id,
        ImType imType,
        String name,
        String avatar,
        String lastMessageContent,
        List<String> matchTags
) {
}
