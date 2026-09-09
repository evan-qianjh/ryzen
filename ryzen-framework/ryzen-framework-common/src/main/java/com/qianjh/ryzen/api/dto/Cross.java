package com.qianjh.ryzen.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 跨域报文
 * @author QianJH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Cross {

    private Long tenantId;

    private String accountType;
    private String accountId;

    private String businessType;
    private Long businessId;

    private Long expireTime;
}
