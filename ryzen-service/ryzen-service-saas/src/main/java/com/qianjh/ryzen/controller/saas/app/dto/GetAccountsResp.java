package com.qianjh.ryzen.controller.saas.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetAccountsResp {
    private String id;
    private String nickname;
    private String username;
    private String email;
    private Boolean enabled;
    private boolean administrator;
    private Long createdTime;
}
