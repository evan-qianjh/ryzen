package com.qianjh.ryzen.controller.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PutAccountSecretPasswordReq {
    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;
}
