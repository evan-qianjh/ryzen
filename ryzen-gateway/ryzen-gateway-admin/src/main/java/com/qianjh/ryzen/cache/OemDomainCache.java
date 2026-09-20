package com.qianjh.ryzen.cache;


import com.qianjh.ryzen.framework.saas.entity.OemDomain;

/**
 * @author QianJH
 */
public interface OemDomainCache {

    OemDomain getByDomain(String domain);
}
