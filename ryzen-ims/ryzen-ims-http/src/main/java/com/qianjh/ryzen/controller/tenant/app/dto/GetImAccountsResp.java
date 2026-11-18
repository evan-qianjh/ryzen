package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.enums.ImType;
import lombok.Builder;

@Builder(toBuilder = true)
public record GetImAccountsResp(
        String id,
        ImType imType,
        String code,
        String remark,
        Long createdTime
) {
}
