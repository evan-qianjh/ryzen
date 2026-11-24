package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.tenant.enums.AccountBusinessStatus;
import com.qianjh.ryzen.tenant.enums.AccountBusinessType;
import lombok.Builder;

@Builder(toBuilder = true)
public record GetAccountBusinessResp(
        String id,
        String accountId,
        Account account,
        AccountBusinessStatus status,
        AccountBusinessType type,
        Long createdTime
) {

    @Builder(toBuilder = true)
    public record Account(
            String username,
            String nickname
    ) {
    }
}
