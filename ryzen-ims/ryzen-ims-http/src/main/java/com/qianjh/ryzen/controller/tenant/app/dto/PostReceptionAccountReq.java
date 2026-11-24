package com.qianjh.ryzen.controller.tenant.app.dto;


import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PostReceptionAccountReq(
        @NotNull
        Long accountId,
        List<Long> routeTagIds
) {
}
