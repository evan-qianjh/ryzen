package com.qianjh.ryzen.controller.tenant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PutAccountSecretPasswordReq {
    @NotNull
    private Long keyId;

    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;
}
