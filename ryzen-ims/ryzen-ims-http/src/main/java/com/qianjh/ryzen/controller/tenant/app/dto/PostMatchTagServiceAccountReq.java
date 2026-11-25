package com.qianjh.ryzen.controller.tenant.app.dto;

import jakarta.validation.constraints.NotNull;

public record PostMatchTagServiceAccountReq(
        @NotNull
        Long matchTagId,
        @NotNull
        Long serviceAccountId
) {
}
