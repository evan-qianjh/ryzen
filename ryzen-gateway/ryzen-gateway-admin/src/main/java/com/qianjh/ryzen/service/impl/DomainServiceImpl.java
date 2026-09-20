package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qianjh.ryzen.framework.gateway.util.DomainUtils;
import com.qianjh.ryzen.framework.gateway.util.dto.Domain;
import com.qianjh.ryzen.framework.saas.entity.Oem;
import com.qianjh.ryzen.framework.saas.entity.OemDomain;
import com.qianjh.ryzen.framework.saas.entity.Tenant;
import com.qianjh.ryzen.framework.saas.entity.TenantDomain;
import com.qianjh.ryzen.mapper.OemDomainMapper;
import com.qianjh.ryzen.mapper.OemMapper;
import com.qianjh.ryzen.mapper.TenantDomainMapper;
import com.qianjh.ryzen.mapper.TenantMapper;
import com.qianjh.ryzen.service.DomainService;
import com.qianjh.ryzen.service.dto.DomainOwner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainServiceImpl implements DomainService {

    private final OemDomainMapper oemDomainMapper;

    private static final Map<Long, Oem> OEM_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, OemDomain> OEM_DOMAIN_MAP = new ConcurrentHashMap<>();

    private static final Map<Long, Tenant> TENANT_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, TenantDomain> TENANT_DOMAIN_MAP = new ConcurrentHashMap<>();

    private static final Map<String, Map<String, DomainOwner>> DOMAIN_MAP = new ConcurrentHashMap<>();

    private final OemMapper oemMapper;
    private final TenantMapper tenantMapper;
    private final TenantDomainMapper tenantDomainMapper;


    // TODO 不优雅，应该启动时加载一次，然后通过事件驱动更新
    @Scheduled(fixedRate = 10_000L)
    public void job() {
        try {
            refreshAll();
            log.debug("刷新Cache");
        } catch (Exception e) {
            log.error("刷新Cache失败", e);
        }
    }

    private void refreshAll() {
        // oem
        for (Oem entity : oemMapper.selectList(Wrappers.emptyWrapper())) {
            OEM_MAP.put(entity.getId(), entity);
        }
        // oem domain
        for (OemDomain entity : oemDomainMapper.selectList(Wrappers.emptyWrapper())) {
            OEM_DOMAIN_MAP.put(entity.getId(), entity);
        }
        // tenant
        for (Tenant entity : tenantMapper.selectList(Wrappers.emptyWrapper())) {
            TENANT_MAP.put(entity.getId(), entity);
        }
        // tenantDomain
        for (TenantDomain entity : tenantDomainMapper.selectList(Wrappers.emptyWrapper())) {
            TENANT_DOMAIN_MAP.put(entity.getId(), entity);
        }

        //
        for (TenantDomain tenantDomain : TENANT_DOMAIN_MAP.values()) {
            Tenant tenant = TENANT_MAP.get(tenantDomain.getTenantId());
            Oem oem = OEM_MAP.get(tenant.getOemId());

            for (OemDomain oemDomain : OEM_DOMAIN_MAP.values()) {
                Map<String, DomainOwner> tenantMap = DOMAIN_MAP.computeIfAbsent(oemDomain.getDomain(), (_) -> new ConcurrentHashMap<>());
                DomainOwner domainOwner = tenantMap.computeIfAbsent(tenantDomain.getDomain(), (_) -> new DomainOwner());
                //
                domainOwner.setOem(oem);
                domainOwner.setOemDomain(oemDomain);
                domainOwner.setTenant(tenant);
                domainOwner.setTenantDomain(tenantDomain);
            }
        }
    }

    @Override
    public DomainOwner resolve(String domain) {
        Domain entity = DomainUtils.parse(domain);

        String oem = entity.oem();
        String tenant = entity.tenant();
        if(StringUtils.isAnyBlank(oem, tenant)) {
            return null;
        }
        Map<String, DomainOwner> tenantMap = DOMAIN_MAP.get(oem);
        if(tenantMap == null) {
            return null;
        }
        return tenantMap.get(tenant);
    }
}
