package com.qianjh.ryzen.plugin.sms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._Schemas;
import lombok.Data;

/**
 * @author QianJH
 */
@Data
@TableName(_Schemas.RYZEN + "sms")
public class Sms {
    @TableId
    private Long id;
    private Long oemId;
    private String signName;
}
