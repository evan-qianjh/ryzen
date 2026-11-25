package com.qianjh.ryzen.controller.tenant.app.dto;

import java.util.List;

public record PostMatchTagsByDomainReq(
        List<Long> matchTagIds
) {

}
