package com.qianjh.ryzen.controller.oem.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetTenantsResp {
    private String id;
    private String symbol;
    private Boolean enabled;
    private Long createdTime;
}
