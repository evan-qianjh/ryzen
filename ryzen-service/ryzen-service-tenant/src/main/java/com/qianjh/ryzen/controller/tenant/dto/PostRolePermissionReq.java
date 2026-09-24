package com.qianjh.ryzen.controller.tenant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostRolePermissionReq {
    @NotNull
    private Long roleId;

    @NotNull
    private Long permissionId;
}
