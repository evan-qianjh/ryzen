package com.qianjh.ryzen.service.dto;

import com.qianjh.ryzen.ims.enums.MessageContentType;
import lombok.Builder;

@Builder(toBuilder = true)
public record AppToSchedulerTaskContent(
        String content,
        MessageContentType contentType
) {
}
