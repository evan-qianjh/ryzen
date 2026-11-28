package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.ims.enums.MessageContentType;
import jakarta.validation.constraints.NotNull;

public record PostMessageReq(
        @NotNull
        Long conversationId,

        @NotNull
        String content,

        MessageContentType contentType
) {
}
