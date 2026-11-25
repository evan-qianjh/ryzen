package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.ims.enums.MatchTagStatus;
import lombok.Builder;

@Builder(toBuilder = true)
public record GetMatchTagsResp(
        String id,
        String tag,
        String group,
        MatchTagStatus status
) {
}
