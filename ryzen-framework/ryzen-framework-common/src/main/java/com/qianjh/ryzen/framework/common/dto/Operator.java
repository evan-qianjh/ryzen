package com.qianjh.ryzen.framework.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作者
 *
 * @author QianJH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Operator {
    /**
     * 身份
     */
    private Identity identity;
    /**
     * ID
     */
    private Long id;
    /**
     * 名称
     */
    private String name;

    /**
     * 身份
     */
    public enum Identity {
        /**
         * SAAS账户
         */
        SAAS_ACCOUNT,
        /**
         * OEM账户
         */
        OEM_ACCOUNT,
        /**
         * 租户账户
         */
        TENANT_ACCOUNT,
        /**
         * 用户账户
         */
        USER_ACCOUNT,
        /**
         * 用户APIKEY
         */
        USER_APIKEY,
    }
}
