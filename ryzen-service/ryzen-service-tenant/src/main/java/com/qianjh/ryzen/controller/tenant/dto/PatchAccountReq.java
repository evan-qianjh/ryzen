package com.qianjh.ryzen.controller.tenant.dto;

import lombok.Data;

import java.util.Set;

@Data
public class PatchAccountReq {

    private String username;
    private String nickname;
    private Boolean enabled;

    private Set<Long> roleIds;
}
