package com.qianjh.ryzen.service.dto;


import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TenantPostServiceAccountReq(
        @NotNull
        Long accountId,
        String remark,
        List<Long> matchTagIds
) {
}
