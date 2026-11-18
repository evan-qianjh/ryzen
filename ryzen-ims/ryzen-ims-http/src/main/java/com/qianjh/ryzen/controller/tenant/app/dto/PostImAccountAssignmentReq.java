package com.qianjh.ryzen.controller.tenant.app.dto;

import jakarta.validation.constraints.NotNull;

public record PostImAccountAssignmentReq(
        @NotNull
        Long accountId
) {
}
