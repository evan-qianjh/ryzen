package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.service.dto.TenantPatchMatchTagReq;
import com.qianjh.ryzen.service.dto.TenantPostMatchTagReq;

import java.util.List;

public interface MatchTagService extends IService<MatchTag> {
    MatchTag getById(Long oemId, Long tenantId, Long id);

    MatchTag getByUk(Long oemId, Long tenantId, String tag);

    List<MatchTag> listByIds(Long oemId, Long tenantId, List<Long> ids);

    MatchTag create(Long oemId, Long tenantId, TenantPostMatchTagReq body);

    boolean patch(MatchTag entity, TenantPatchMatchTagReq body);
}
