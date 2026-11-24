package com.qianjh.ryzen.service.dto;


import jakarta.validation.constraints.NotNull;

public record TenantPostMatchTagReq(
        @NotNull
        String tag,
        String group
) {
}
