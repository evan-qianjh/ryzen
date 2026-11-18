package com.qianjh.ryzen.model;

import com.qianjh.ryzen.enums.ImType;

/**
 * 聚合聊天服务 -> 调度器。id 用于上游请求幂等。时间字段统一使用 Unix 毫秒。
 */
public record SchedulerRequest(
        String code,
        ImType imType,
        String accountCode,
        String conversationCode,
        String content,
        Long deadlineTime
) {
}
