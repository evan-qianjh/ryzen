package com.qianjh.ryzen.controller.tenant.app.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record GetMatchTagImAccountsResp(
        String id,
        String matchTagId,
        String imAccountId,
        Long createdTime
) {
}
