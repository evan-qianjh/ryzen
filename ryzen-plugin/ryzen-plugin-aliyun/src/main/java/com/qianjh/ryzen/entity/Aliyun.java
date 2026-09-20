package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._Schemas;
import lombok.Data;

/**
 * @author QianJH
 */
@Data
@TableName(_Schemas.RYZEN + "aliyun")
public class Aliyun {
    @TableId
    private Long id;
    private Long oemId;
    private String accessKey;
    private String accessSecret;
}
