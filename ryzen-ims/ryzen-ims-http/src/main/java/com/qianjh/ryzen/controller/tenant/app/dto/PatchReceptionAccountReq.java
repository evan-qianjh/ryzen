package com.qianjh.ryzen.controller.tenant.app.dto;


import com.qianjh.ryzen.enums.ReceptionAccountStatus;

public record PatchReceptionAccountReq(
        ReceptionAccountStatus status,
        String remark
) {
}
