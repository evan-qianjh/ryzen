package com.qianjh.ryzen.model;

import com.qianjh.ryzen.enums.ImType;
import lombok.Builder;

/**
 * 调度器 -> 执行器。字段与执行器当前 ExecutorTask 保持一致。
 */
@Builder(toBuilder = true)
public record ExecutorTaskRequest(
        Long assignmentId,
        Long fencingToken,
        ImType imType,
        String accountCode,
        String executorCode,
        Long taskId,
        String conversationCode,
        String content
) {
}
