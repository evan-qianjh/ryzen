package com.qianjh.ryzen.service.dto;

import com.qianjh.ryzen.framework.saas.entity.Oem;
import com.qianjh.ryzen.framework.saas.entity.OemDomain;
import com.qianjh.ryzen.framework.saas.entity.Tenant;
import com.qianjh.ryzen.framework.saas.entity.TenantDomain;
import lombok.Data;

@Data
public class DomainOwner {
    private Oem oem;
    private OemDomain oemDomain;
    private Tenant tenant;
    private TenantDomain tenantDomain;
}
