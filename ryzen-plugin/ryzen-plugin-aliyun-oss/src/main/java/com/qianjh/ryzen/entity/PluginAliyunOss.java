package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 * @author QianJH
 */
@Data
@TableName(Namespace.PREFIX + "plugin_aliyun_oss")
public class PluginAliyunOss {
    @TableId
    private Long id;
    private Long tenantId;


    private String bucketName;

    /* download */
    private String downloadHost;

    /* upload */
    private String uploadPublicHost;
    private String uploadPrivateHost;

    /* endpoint */
    private String endpointPrivateHost;
    private String endpointPublicHost;
}
