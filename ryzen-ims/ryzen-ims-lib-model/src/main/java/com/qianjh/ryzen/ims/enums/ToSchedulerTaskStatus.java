package com.qianjh.ryzen.ims.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ToSchedulerTaskStatus {
    PENDING(false),
    ACCEPTED(false),
    SUCCESS(true),
    FAILED(true),
    ;
    final boolean closed;
}
