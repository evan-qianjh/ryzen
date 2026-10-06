package com.qianjh.ryzen.plugin.aliyun.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.plugin.aliyun.entity.Aliyun;

/**
 * @author QianJH
 */
public interface AliyunService extends IService<Aliyun> {

    Aliyun mustGet(Long tenantId);
}
