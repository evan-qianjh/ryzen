package com.qianjh.ryzen.controller.tenant.app.dto;

import jakarta.validation.constraints.NotNull;

public record PostExecutorReq(
        @NotNull
        String code
) {
}
