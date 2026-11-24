package com.qianjh.ryzen.controller.tenant.app.dto;

import jakarta.validation.constraints.NotNull;

public record PostMatchTagReceptionAccountReq(
        @NotNull
        Long tagId,
        @NotNull
        Long receptionAccountId
) {
}
