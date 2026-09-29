package com.qianjh.ryzen.controller.tenant.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetRolesResp {
    private String id;
    private String title;
    private Boolean enabled;
    private Long createdTime;
}
