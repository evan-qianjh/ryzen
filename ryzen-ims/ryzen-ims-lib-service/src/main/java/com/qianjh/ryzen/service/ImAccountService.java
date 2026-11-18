package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.enums.ImAccountStatus;

public interface ImAccountService extends IService<ImAccount> {

    ImAccount getById(Long oemId, Long tenantId, Long id);

    boolean updateStatus(ImAccount entity, ImAccountStatus before, ImAccountStatus after);
}
