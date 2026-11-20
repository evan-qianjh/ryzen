package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.enums.ImAccountAssignStatus;
import com.qianjh.ryzen.enums.ImAccountExecutorStatus;
import com.qianjh.ryzen.enums.ImType;
import lombok.Builder;

@Builder(toBuilder = true)
public record GetImAccountsResp(
        String id,
        ImType imType,
        String code,
        String remark,
        ImAccountAssignStatus assignStatus,
        ImAccountExecutorStatus executorStatus,
        Long createdTime
) {
}
