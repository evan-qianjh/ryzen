package com.qianjh.ryzen.controller.tenant.dto;

import lombok.Data;

@Data
public class PatchPermissionReq {

    private Long parentId;

    private String title;

    private Boolean enabled;
}
