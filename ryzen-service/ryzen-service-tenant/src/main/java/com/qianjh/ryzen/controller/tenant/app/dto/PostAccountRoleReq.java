package com.qianjh.ryzen.controller.tenant.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostAccountRoleReq {
    @NotNull
    private Long accountId;

    @NotNull
    private Long roleId;
}
