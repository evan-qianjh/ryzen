package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.enums.ServiceAccountStatus;
import lombok.Builder;

@Builder(toBuilder = true)
public record GetServiceAccountsResp(
        String id,
        String accountId,
        ServiceAccountStatus status,
        String remark
) {
}
