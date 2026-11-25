package com.qianjh.ryzen.service.dto;


import com.qianjh.ryzen.ims.enums.ServiceAccountStatus;

public record TenantPatchServiceAccountReq(
        ServiceAccountStatus status,
        String remark
) {
}
