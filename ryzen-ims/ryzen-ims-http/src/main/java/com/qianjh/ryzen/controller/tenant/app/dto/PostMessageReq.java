package com.qianjh.ryzen.controller.tenant.app.dto;

import jakarta.validation.constraints.NotNull;

public record PostMessageReq(
        @NotNull
        Long conversationId,

        @NotNull
        String content
) {
}
