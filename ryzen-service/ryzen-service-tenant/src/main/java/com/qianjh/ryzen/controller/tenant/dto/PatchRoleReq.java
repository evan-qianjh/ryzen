package com.qianjh.ryzen.controller.tenant.dto;

import lombok.Data;

import java.util.Set;

@Data
public class PatchRoleReq {

    private String title;

    private Boolean enabled;

    private Set<Long> permissionIds;
}
