package com.qianjh.ryzen.framework.saas.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._Schemas;
import lombok.Data;

/**
 * @author QianJH
 */
@Data
@TableName(_Schemas.RYZEN + "oem")
public class Oem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String symbol;

    private Boolean enabled;

    /**
     * 合作伙伴消息域名Host
     * format: 'https://partner-api.{oem}'
     * e.g.: https://partner-api.qianjh.com
     */
    private String partnerNotifyHost;
}
