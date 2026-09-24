package com.qianjh.ryzen.controller.tenant.dto;

import com.qianjh.ryzen.tenant.enums.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetPermissionsResp {
    private String id;
    private String parentId;
    private PermissionType type;
    private String title;
    private String symbol;
    private Boolean enabled;
}
