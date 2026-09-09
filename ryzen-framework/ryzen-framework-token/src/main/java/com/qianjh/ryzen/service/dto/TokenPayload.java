package com.qianjh.ryzen.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author QianJH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TokenPayload {
    private String id;

    private String accountId;

    private Long generatedTime;

    private Long expiresTime;
}
