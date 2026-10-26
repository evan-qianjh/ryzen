package com.qianjh.ryzen.controller.tenant.app.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record PutAccountRolesReq(@NotNull Set<Long> roleIds) {
}
