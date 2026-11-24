package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.enums.ReceptionAccountStatus;
import lombok.Builder;

@Builder(toBuilder = true)
public record GetReceptionAccountsResp(
        String id,
        String accountId,
        ReceptionAccountStatus status,
        String remark
) {
}
