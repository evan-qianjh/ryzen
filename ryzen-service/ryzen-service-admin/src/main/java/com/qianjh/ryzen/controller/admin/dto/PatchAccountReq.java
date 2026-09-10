package com.qianjh.ryzen.controller.admin.dto;

import lombok.Data;

@Data
public class PatchAccountReq {

    private String username;
    private String nickname;
    private Boolean enabled;
}
