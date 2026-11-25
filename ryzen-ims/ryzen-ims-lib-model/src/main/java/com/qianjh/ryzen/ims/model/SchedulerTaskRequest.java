package com.qianjh.ryzen.ims.model;

import lombok.Builder;

/**
 * 聚合聊天服务 -> 调度器。id 用于上游请求幂等。时间字段统一使用 Unix 毫秒。
 */
@Builder(toBuilder = true)
public record SchedulerTaskRequest(
        String code,
        Long assignmentId,
        String conversationCode,
        String content,
        Long deadlineTime
) {
}
