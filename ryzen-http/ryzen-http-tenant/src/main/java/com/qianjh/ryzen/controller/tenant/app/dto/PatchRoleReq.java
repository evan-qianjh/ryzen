package com.qianjh.ryzen.controller.tenant.app.dto;

import lombok.Data;

import java.util.Set;

@Data
public class PatchRoleReq {

    private String title;

    private Boolean enabled;

    private Set<Long> permissionIds;
}
