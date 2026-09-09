package com.qianjh.ryzen.service;

import com.qianjh.ryzen.service.dto.AliyunOssUploadToken;

/**
 *
 * @author QianJH
 */
public interface RyzenAliyunOssService extends RyzenOssService {

    /**
     * 创建资源token
     *
     * @param tenantId   租户ID
     * @param service    服务
     * @param filePrefix 前缀
     * @param fileSuffix 后缀
     * @return token
     */
    AliyunOssUploadToken createAssetToken(Long tenantId, String service, String filePrefix, String fileSuffix);

    /**
     * 创建账户token
     *
     * @param tenantId    租户ID
     * @param accountType 账户类型
     * @param accountId   账户ID
     * @param filePrefix  前缀
     * @param fileSuffix  后缀
     * @return token
     */
    AliyunOssUploadToken createAccountToken(Long tenantId, String accountType, Long accountId, String filePrefix, String fileSuffix);
}
