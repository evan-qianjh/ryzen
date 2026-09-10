package com.qianjh.ryzen.controller.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostPermissionReq {

    private Long parentId;

    @NotNull
    private String title;
    
    @NotNull
    private String symbol;

    private Boolean enabled;
}
