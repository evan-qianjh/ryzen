package com.qianjh.ryzen.service.dto;


import com.qianjh.ryzen.entity.Oem;
import com.qianjh.ryzen.entity.OemDomain;

public record DomainOwner(Oem oem, OemDomain oemDomain) {
}
