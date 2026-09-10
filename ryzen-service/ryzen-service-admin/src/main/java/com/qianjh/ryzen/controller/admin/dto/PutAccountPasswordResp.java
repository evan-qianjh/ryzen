package com.qianjh.ryzen.controller.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PutAccountPasswordResp {
    private String nickname;
    private String username;
    private String password;
}
