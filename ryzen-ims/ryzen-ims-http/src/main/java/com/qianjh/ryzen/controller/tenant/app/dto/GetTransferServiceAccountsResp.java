package com.qianjh.ryzen.controller.tenant.app.dto;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record GetTransferServiceAccountsResp(
        String id,
        String name,
        Boolean online,
        List<String> matchTags
) {
}
