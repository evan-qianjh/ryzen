package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 * @author QianJH
 */
@Data
@TableName(_Schemas.RYZEN + "aliyun_oss")
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
