package com.qianjh.ryzen.model;

import com.qianjh.ryzen.enums.ExecutorCommandType;
import lombok.Builder;

@Builder(toBuilder = true)
public record ExecutorCommand(
        String code,
        ExecutorCommandType type,
        String executorCode,
        Assignment assignment
) {
}
