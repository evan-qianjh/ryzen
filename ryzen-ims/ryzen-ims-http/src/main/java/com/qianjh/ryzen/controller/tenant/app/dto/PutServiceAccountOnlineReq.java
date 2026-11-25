package com.qianjh.ryzen.controller.tenant.app.dto;

import jakarta.validation.constraints.NotNull;

public record PutServiceAccountOnlineReq(
        @NotNull
        Boolean online
) {
}
