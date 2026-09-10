package com.qianjh.ryzen.controller.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostAccountReq {

    @NotNull
    private String username;

    @NotNull
    private String nickname;

    private Boolean enabled;
}
