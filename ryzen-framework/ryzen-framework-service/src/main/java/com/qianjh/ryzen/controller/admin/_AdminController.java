package com.qianjh.ryzen.controller.admin;

/**
 * 管理后台
 */
public abstract class _AdminController {
    public static final String PATH_PREFIX = "/admin";
    public static final String PUBLIC_PATH_PREFIX = PATH_PREFIX + "/public";

    public static final String DEFAULT_PAGE_INDEX = "1";
    public static final String DEFAULT_PAGE_SIZE = "15";

    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 安全的页面大小
     *
     * @param size 页面大小
     * @return pageSize
     */
    public int safePageSize(Integer size) {
        if (size == null) {
            return Integer.parseInt(DEFAULT_PAGE_SIZE);
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
