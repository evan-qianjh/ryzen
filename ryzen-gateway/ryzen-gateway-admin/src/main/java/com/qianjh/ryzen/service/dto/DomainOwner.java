package com.qianjh.ryzen.service.dto;

import com.qianjh.ryzen.framework.saas.entity.Oem;
import com.qianjh.ryzen.framework.saas.entity.OemDomain;

public record DomainOwner(Oem oem, OemDomain oemDomain) {
}
