package com.qianjh.ryzen.plugin.aliyun.oss.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._TablePrefix;
import lombok.Data;

/**
 *
 * @author QianJH
 */
@Data
@TableName(_TablePrefix.PLUGIN + "aliyun_oss")
public class AliyunOss {
    @TableId
    private Long id;
    private Long oemId;

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
