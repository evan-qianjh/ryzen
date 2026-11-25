package com.qianjh.ryzen.controller.tenant.app.dto;

import java.util.List;

public record PostMatchTagsByServiceAccountReq(
        List<Long> matchTagIds
) {

}
