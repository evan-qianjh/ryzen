package com.qianjh.ryzen.controller.tenant.dto;

import com.qianjh.ryzen.tenant.enums.PermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostPermissionReq {

    private Long parentId;

    @NotNull
    private PermissionType type;

    @NotNull
    private String title;
    
    @NotNull
    private String symbol;

    private Boolean enabled;
}
