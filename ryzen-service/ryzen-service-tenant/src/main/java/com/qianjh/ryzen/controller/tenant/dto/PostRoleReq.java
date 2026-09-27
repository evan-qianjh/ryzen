package com.qianjh.ryzen.controller.tenant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PostRoleReq {

    @NotNull
    private String title;

    private Boolean enabled;

    private List<Long> permissionIds;
}
