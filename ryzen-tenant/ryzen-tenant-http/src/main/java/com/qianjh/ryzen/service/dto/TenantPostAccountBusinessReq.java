package com.qianjh.ryzen.service.dto;


import com.qianjh.ryzen.tenant.enums.AccountBusinessType;
import jakarta.validation.constraints.NotNull;

public record TenantPostAccountBusinessReq(
        @NotNull
        Long accountId,
        @NotNull
        AccountBusinessType type
) {
}
