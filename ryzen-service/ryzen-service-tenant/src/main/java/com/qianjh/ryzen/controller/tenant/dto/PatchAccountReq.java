package com.qianjh.ryzen.controller.tenant.dto;

public record PatchAccountReq(String username, String nickname, Boolean enabled) {
}
