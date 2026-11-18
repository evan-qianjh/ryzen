package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.enums.ExecutorOperationType;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.enums.ImType;
import lombok.Builder;

@Builder(toBuilder = true)
public record GetExecutorsResp(
        String id,
        String code,
        ImType imType,
        String accountCode,
        ExecutorStatus status,
        ExecutorOperationType currentOperation,
        String currentTaskId,
        Long registeredTime,
        Long lastHeartbeatTime,
        String errorMessage,
        Long createdTime
) {
}
