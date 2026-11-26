package com.qianjh.ryzen.service.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record AppToSchedulerTaskContent(
        String content
) {
}
