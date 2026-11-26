package com.qianjh.ryzen.ims.dto;

import com.qianjh.ryzen.ims.enums.ExecutorCommandType;
import lombok.Builder;

@Builder(toBuilder = true)
public record ExecutorCommandRequest(
        String code,
        ExecutorCommandType type,
        String executorCode,
        Assignment assignment
) {
}
