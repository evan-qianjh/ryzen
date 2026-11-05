package com.qianjh.ryzen.framework.http.controller.user;

import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.framework.http.controller._Controller;

/**
 * 用户
 *
 * @author QianJH
 */
public abstract class _UserOpenController extends _Controller {
    public static final String PATH_PREFIX = "/user/open";
    public static final String PUBLIC_PATH_PREFIX = PATH_PREFIX + PUBLIC;

    /**
     * 构建操作者
     *
     * @param id   ID
     * @param name 名称
     * @return 操作者
     */
    public Operator buildOperator(Long id, String name) {
        return Operator.builder()
                .identity(Operator.Identity.USER_APIKEY)
                .id(id)
                .name(name)
                .build();
    }
}
