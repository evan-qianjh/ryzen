package com.qianjh.ryzen.controller.tenant.app.dto;

import jakarta.validation.constraints.NotNull;

public record PostConversationTransferReq(
        @NotNull
        Long serviceAccountId,
        String remark
) {
}
