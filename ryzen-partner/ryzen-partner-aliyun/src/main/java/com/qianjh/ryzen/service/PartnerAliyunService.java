package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.PartnerAliyun;

/**
 * @author QianJH
 */
public interface PartnerAliyunService extends IService<PartnerAliyun> {

    PartnerAliyun mustGet(Long tenantId);
}
