package com.qianjh.ryzen.service.dto;


import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TenantPostReceptionAccountReq(
        @NotNull
        Long accountId,
        List<Long> routeTagIds
) {
}
