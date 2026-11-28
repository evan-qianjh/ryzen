package com.qianjh.ryzen.ims.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SchedulerTaskStatus {
    PENDING(false),
    ACCEPTED(false),

    REJECTED(true),
    SUCCESS(true),
    FAILED(true),
    UNKNOWN(true),
    TIMEOUT(true),
    ;
    final boolean closed;
}
