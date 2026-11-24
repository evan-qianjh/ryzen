package com.qianjh.ryzen.service.dto;


import com.qianjh.ryzen.tenant.enums.AccountBusinessStatus;

public record TenantPatchAccountBusinessReq(
        AccountBusinessStatus status
) {
}
