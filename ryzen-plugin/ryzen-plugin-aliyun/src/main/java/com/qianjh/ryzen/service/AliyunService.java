package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.Aliyun;

/**
 * @author QianJH
 */
public interface AliyunService extends IService<Aliyun> {

    Aliyun mustGet(Long tenantId);
}
