package com.qianjh.ryzen.plugin.aliyun.oss.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.plugin.aliyun.oss.entity.AliyunOss;
import com.qianjh.ryzen.plugin.aliyun.oss.service.dto.AliyunOssUploadToken;
import com.qianjh.ryzen.plugin.oss.service.OssService;

import java.io.IOException;

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
     * @param domain   领域
     * @param domainId 领域ID
     * @param category 类目
     * @param fileName 文件名
     * @return token
     */
    AliyunOssUploadToken createUserToken(Long oemId, Long tenantId, String domain, Long domainId, String category, String fileName);

    /**
     * 上传
     *
     * @param token       token
     * @param content     内容
     * @param contentType 内容类型
     * @param fileName    文件名
     * @return 是否成功
     */
    boolean upload(AliyunOssUploadToken token, byte[] content, String contentType, String fileName) throws IOException, InterruptedException;
}
