package com.qianjh.ryzen.model;

import com.qianjh.ryzen.enums.ImType;
import lombok.Builder;

@Builder(toBuilder = true)
public record Assignment(
        Long id,
        Long fencingToken,

        ImType imType,
        String accountCode
) {
}
