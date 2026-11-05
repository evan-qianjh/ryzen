package com.qianjh.ryzen.framework.http.controller.tenant;

import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.framework.http.controller._Controller;

/**
 * 租户后台
 */
public abstract class _TenantAppController extends _Controller {
    public static final String PATH_PREFIX = "/tenant/app";
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

    /**
     * 构建操作者
     *
     * @param id   ID
     * @param name 名称
     * @return 操作者
     */
    public Operator buildOperator(Long id, String name) {
        return buildOperator(id, name, Operator.Identity.TENANT_ACCOUNT);
    }
}
