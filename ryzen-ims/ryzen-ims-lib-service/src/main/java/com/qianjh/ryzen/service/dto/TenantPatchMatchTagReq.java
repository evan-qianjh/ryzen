package com.qianjh.ryzen.service.dto;


public record TenantPatchMatchTagReq(
        String group,
        Boolean enabled
) {
}
