package com.qianjh.ryzen.controller.tenant.app.dto;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record GetAvailableTransferServiceAccountsResp(
        String id,
        String name,
        List<String> matchTags
) {
}
