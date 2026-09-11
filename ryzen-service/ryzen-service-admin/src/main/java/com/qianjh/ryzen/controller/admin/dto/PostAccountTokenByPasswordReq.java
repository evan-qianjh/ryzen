package com.qianjh.ryzen.controller.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 */
@Data
public class PostAccountTokenByPasswordReq {
    @NotNull
    @Schema(description = "用户账号")
    private String username;

    @NotNull
    @Schema(description = "用户密码")
    private String password;

    @Schema(description = "加密密钥ID")
    private Long keyId;

    @Schema(description = "TOTP")
    private Integer totp;
}
