package com.qianjh.ryzen.framework.common.dict;

/**
 * 操作人类型，用于数据库表中记录操作人类型
 *
 * @author QianJH
 */
public enum OperatorIdentity {
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
