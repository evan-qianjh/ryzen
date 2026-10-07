package com.qianjh.ryzen.plugin.aliyun.oss.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.plugin.aliyun.oss.entity.AliyunOss;
import com.qianjh.ryzen.plugin.aliyun.oss.service.dto.AliyunOssUploadToken;
import com.qianjh.ryzen.plugin.oss.service.OssService;

/**
 *
 * @author QianJH
 */
public interface AliyunOssService extends IService<AliyunOss>, OssService {

    /**
     * 创建账户token
     *
     * @param oemId    OEM ID
     * @param tenantId 租户ID
     * @param userType 用户类型
     * @param userId   用户ID
     * @param category 类目
     * @param fileName 文件名
     * @return token
     */
    AliyunOssUploadToken createUserToken(Long oemId, Long tenantId, String userType, Long userId, String category, String fileName);
}
