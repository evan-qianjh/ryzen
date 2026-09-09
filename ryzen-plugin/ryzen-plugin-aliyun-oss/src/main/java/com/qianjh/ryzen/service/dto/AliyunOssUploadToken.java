package com.qianjh.ryzen.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author QianJH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AliyunOssUploadToken {
    private String bucket;
    private String fileKey;
    private String policy;
    private String accessKeyId;
    private String signature;
    private String downloadHost;
    private String uploadHost;
}
