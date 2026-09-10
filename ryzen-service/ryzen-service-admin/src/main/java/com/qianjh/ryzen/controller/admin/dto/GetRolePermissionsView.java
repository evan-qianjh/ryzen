package com.qianjh.ryzen.controller.admin.dto;

import com.qianjh.ryzen.entity.RolePermission;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetRolePermissionsView extends RolePermission {
    // permission
    private String symbol;
}
