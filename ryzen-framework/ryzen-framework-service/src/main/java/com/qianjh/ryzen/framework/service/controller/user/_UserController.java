package com.qianjh.ryzen.framework.service.controller.user;

import com.qianjh.ryzen.framework.service.controller._Controller;

/**
 * 用户
 *
 * @author QianJH
 */
public abstract class _UserController extends _Controller {
    public static final String PATH_PREFIX = "/user";
    public static final String PUBLIC_PATH_PREFIX = PATH_PREFIX + PUBLIC;

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

}
