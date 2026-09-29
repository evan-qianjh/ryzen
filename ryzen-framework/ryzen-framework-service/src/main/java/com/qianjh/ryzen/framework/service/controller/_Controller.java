package com.qianjh.ryzen.framework.service.controller;

/**
 * 基础Controller
 *
 * @author QianJH
 */
public abstract class _Controller {

    /**
     * 公开接口路径前缀
     */
    public static final String PUBLIC = "/public";
    /**
     * 分页默认起点下标
     */
    public static final String DEFAULT_PAGE_INDEX = "1";
    /**
     * 分页默认页面大小
     */
    public static final String DEFAULT_PAGE_SIZE = "20";
    /**
     * 分页最大页面大小
     */
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * 安全的页面大小。若默认的不满足，子类可以重写，参考_TenantController
     *
     * @param pageSize 页面大小
     * @return limit
     */
    public int safePageSize(Integer pageSize) {
        return safePageSize(pageSize, DEFAULT_PAGE_SIZE, MAX_PAGE_SIZE);
    }

    /**
     * 安全的页面大小
     *
     * @param pageSize 页面大小
     * @return pageSize
     */
    public int safePageSize(Integer pageSize, String defaultSize, int maxSize) {
        if (pageSize == null) {
            return Integer.parseInt(defaultSize);
        }
        return Math.min(pageSize, maxSize);
    }
}
