package com.qianjh.ryzen.model;

import com.qianjh.ryzen.enums.ExecutorCommandType;
import lombok.Builder;

@Builder(toBuilder = true)
public record ExecutorCommand(
        String commandKey,
        String executorCode,
        ExecutorCommandType type,
        Assignment assignment
) {
}
