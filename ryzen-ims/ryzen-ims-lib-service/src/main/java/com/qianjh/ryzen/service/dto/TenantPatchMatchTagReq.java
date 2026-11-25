package com.qianjh.ryzen.service.dto;


import com.qianjh.ryzen.ims.enums.MatchTagStatus;

public record TenantPatchMatchTagReq(
        String group,
        MatchTagStatus status
) {
}
