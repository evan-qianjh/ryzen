package com.qianjh.ryzen.framework.token.service.dto;

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

    // subject: saas -> oem -> tenant -> business

    /**
     * OEM ID，subject为saas时,为null
     */
    private String oemId;
    /**
     * 租户 ID，subject为saas/oem时，为null
     */
    private String tenantId;
    /**
     * 主体 ID，例如工厂账号登录，它的subjectId是factoryId
     */
    private String subjectId;
    /**
     * 账户 ID
     */
    private String accountId;
    /**
     * 生成时间
     */
    private Long generatedTime;
    /**
     * 过期时间
     */
    private Long expiresTime;
}
