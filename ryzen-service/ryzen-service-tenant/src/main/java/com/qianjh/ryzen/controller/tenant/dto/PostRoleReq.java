package com.qianjh.ryzen.controller.tenant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostRoleReq {

    @NotNull
    private String title;

    private Boolean enabled;
}
