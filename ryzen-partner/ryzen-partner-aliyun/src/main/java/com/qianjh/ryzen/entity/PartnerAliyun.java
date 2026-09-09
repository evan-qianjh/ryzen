package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author QianJH
 */
@Data
@TableName(Namespace.PREFIX + "partner_aliyun")
public class PartnerAliyun {
    @TableId
    private Long id;
    private Long tenantId;
    private String accessKey;
    private String accessSecret;
}
