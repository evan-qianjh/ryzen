package com.qianjh.ryzen.service.dto;

import com.qianjh.ryzen.ims.enums.ImType;
import jakarta.validation.constraints.NotNull;

public record TenantPostImAccountReq(
        @NotNull
        ImType imType,
        @NotNull
        String accountCode,
        String remark
) {
}
