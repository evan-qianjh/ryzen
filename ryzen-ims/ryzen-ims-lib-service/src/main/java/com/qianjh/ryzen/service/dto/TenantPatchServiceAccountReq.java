package com.qianjh.ryzen.service.dto;


import com.qianjh.ryzen.ims.enums.ServiceAccountStatus;
import lombok.Builder;

@Builder(toBuilder = true)
public record TenantPatchServiceAccountReq(
        ServiceAccountStatus status,
        Boolean online,
        String remark
) {
}
