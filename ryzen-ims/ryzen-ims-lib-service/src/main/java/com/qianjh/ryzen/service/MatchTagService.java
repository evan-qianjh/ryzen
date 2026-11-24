package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.service.dto.TenantPatchMatchTagReq;
import com.qianjh.ryzen.service.dto.TenantPostMatchTagReq;

public interface MatchTagService extends IService<MatchTag> {
    MatchTag getById(Long oemId, Long tenantId, Long id);

    MatchTag getByUk(Long oemId, Long tenantId, String tag);

    MatchTag create(Long oemId, Long tenantId, TenantPostMatchTagReq body);

    boolean patch(MatchTag entity, TenantPatchMatchTagReq body);
}
