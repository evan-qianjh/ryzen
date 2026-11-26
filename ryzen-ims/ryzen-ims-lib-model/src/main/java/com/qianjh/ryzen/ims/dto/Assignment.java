package com.qianjh.ryzen.ims.dto;

import com.qianjh.ryzen.ims.enums.ImType;
import lombok.Builder;

@Builder(toBuilder = true)
public record Assignment(
        Long id,

        ImType imType,
        String accountCode
) {
}
