package com.qianjh.ryzen.controller.user;

/**
 * 用户
 *
 * @author QianJH
 */
public abstract class _UserController {
    public static final String PATH_PREFIX = "/user";
    public static final String PUBLIC_PATH_PREFIX = PATH_PREFIX + "/public";

    public static final String DEFAULT_PAGE_INDEX = "1";
    public static final String DEFAULT_PAGE_SIZE = "15";

    private static final int MAX_PAGE_SIZE = 100;

    /**
     * 获取默认头像
     *
     * @param service
     * @param tenantId
     * @return
     */
    public String getDefaultAvatar(String service, Long tenantId) {
        return String.format("tenant/%s/assets/%s/static/default-avatar.jpg", tenantId, service);
    }

    /**
     * 安全的页面大小
     *
     * @param limit 页面大小
     * @return pageSize
     */
    public int safeLimit(Integer limit) {
        if (limit == null) {
            return Integer.parseInt(DEFAULT_PAGE_SIZE);
        }
        return Math.min(limit, MAX_PAGE_SIZE);
    }
}
