package com.qianjh.ryzen.controller.tenant.app.dto;

import com.qianjh.ryzen.framework.common.dict.PermissionType;
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
