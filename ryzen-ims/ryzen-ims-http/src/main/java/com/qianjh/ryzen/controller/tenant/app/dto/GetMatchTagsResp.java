package com.qianjh.ryzen.controller.tenant.app.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record GetMatchTagsResp(
        String id,
        String tag,
        String group
) {
}
