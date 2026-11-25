package com.qianjh.ryzen.controller.tenant.app.dto;

import jakarta.validation.constraints.NotNull;

public record PostMatchTagImAccountReq(
        @NotNull
        Long matchTagId,
        @NotNull
        Long imAccountId
) {
}
