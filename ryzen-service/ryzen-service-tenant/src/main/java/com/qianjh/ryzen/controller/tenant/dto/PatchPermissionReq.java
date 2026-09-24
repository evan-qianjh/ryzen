package com.qianjh.ryzen.controller.tenant.dto;

import com.qianjh.ryzen.tenant.enums.PermissionType;
import lombok.Data;

@Data
public class PatchPermissionReq {

    private Long parentId;

    private PermissionType type;

    private String title;

    private Boolean enabled;
}
