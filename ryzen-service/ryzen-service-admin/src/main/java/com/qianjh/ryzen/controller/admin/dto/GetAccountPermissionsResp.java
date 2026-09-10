package com.qianjh.ryzen.controller.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetAccountPermissionsResp {
    private List<Role> roles;
    private List<Permission> permissions;

    public record Role(String id, String title) {

    }

    public record Permission(String id, String title, String symbol) {

    }
}
