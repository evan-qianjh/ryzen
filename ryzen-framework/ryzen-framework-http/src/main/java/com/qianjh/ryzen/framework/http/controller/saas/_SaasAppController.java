package com.qianjh.ryzen.framework.http.controller.saas;

import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.framework.http.controller._Controller;

/**
 * SaaS后台
 * @author QianJH
 */
public abstract class _SaasAppController extends _Controller {
    public static final String PATH_PREFIX = "/saas/app";
    public static final String PUBLIC_PATH_PREFIX = PATH_PREFIX + PUBLIC;

    /**
     * 构建操作者
     *
     * @param id   ID
     * @param name 名称
     * @return 操作者
     */
    public Operator buildOperator(Long id, String name) {
        return buildOperator(id, name, Operator.Identity.SAAS_ACCOUNT);
    }
}
