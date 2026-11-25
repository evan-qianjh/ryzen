package com.qianjh.ryzen.controller.tenant.app.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record GetMatchTagServiceAccountsResp(
        String id,
        String matchTagId,
        String serviceAccountId,
        Long createdTime
) {
}
