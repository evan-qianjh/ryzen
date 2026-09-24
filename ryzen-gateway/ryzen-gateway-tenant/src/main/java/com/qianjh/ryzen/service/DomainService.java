package com.qianjh.ryzen.service;

import com.qianjh.ryzen.service.dto.DomainOwner;

public interface DomainService {

    DomainOwner resolve(String domain);
}
