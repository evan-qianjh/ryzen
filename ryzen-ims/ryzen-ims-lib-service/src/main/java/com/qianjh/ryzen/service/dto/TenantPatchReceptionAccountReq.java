package com.qianjh.ryzen.service.dto;


import com.qianjh.ryzen.enums.ReceptionAccountStatus;

public record TenantPatchReceptionAccountReq(
        ReceptionAccountStatus status,
        String remark
) {
}
