package com.qianjh.ryzen.controller.saas;

/**
 * @author QianJH 租户
 */
public abstract class _SaasController {
    public static final String PATH_PREFIX = "/saas";
    public static final String PUBLIC_PATH_PREFIX = PATH_PREFIX + "/public";

    public static final String DEFAULT_PAGE_INDEX = "1";
    public static final String DEFAULT_PAGE_SIZE = "15";
    public static final Long MAX_PAGE_SIZE = 100L;
}
