package com.qianjh.ryzen.ims.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExecutorToSchedulerTaskResult {
    ACCEPTED(false),
    RUNNING(false),
    SUCCESS(true),
    FAILED(true),
    UNKNOWN(true),
    CANCELED(true);
//    /**
//     * 长时间处于PENDING状态
//     */
//    TIMEOUT,

    private final boolean closed;
}
