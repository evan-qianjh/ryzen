package com.qianjh.ryzen.ims.dto;

import lombok.Builder;

/**
 * 调度器 -> 执行器。字段与执行器当前 ExecutorTask 保持一致。
 */
@Builder(toBuilder = true)
public record ExecutorTaskRequest(
        Long assignmentId,
        String executorCode,
        Long taskId,
        String conversationCode,
        String content
) {
}
