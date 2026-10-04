package com.qianjh.ryzen.framework.service.controller.oem;

import com.qianjh.ryzen.framework.service.controller._Controller;

/**
 * OEM
 */
public abstract class _OemAppController extends _Controller {
    public static final String PATH_PREFIX = "/oem/app";
    public static final String PUBLIC_PATH_PREFIX = PATH_PREFIX + PUBLIC;

    /**
     * 分页默认页面大小
     */
    public static final String DEFAULT_PAGE_SIZE = "20";
    /**
     * 分页最大页面大小
     */
    public static final int MAX_PAGE_SIZE = 200;

    @Override
    public int safePageSize(Integer pageSize) {
        return super.safePageSize(pageSize, DEFAULT_PAGE_SIZE, MAX_PAGE_SIZE);
    }
}
