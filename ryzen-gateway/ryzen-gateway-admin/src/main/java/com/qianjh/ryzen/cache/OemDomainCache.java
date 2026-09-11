package com.qianjh.ryzen.cache;


import com.qianjh.ryzen.entity.OemDomain;

/**
 * @author QianJH
 */
public interface OemDomainCache {

    OemDomain getByDomain(String domain);
}
