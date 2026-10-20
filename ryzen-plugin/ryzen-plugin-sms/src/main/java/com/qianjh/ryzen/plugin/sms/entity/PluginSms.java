package com.qianjh.ryzen.plugin.sms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._TablePrefix;
import lombok.Data;

/**
 * @author QianJH
 */
@Data
@TableName(_TablePrefix.PLUGIN + "sms")
public class PluginSms {
    @TableId
    private Long id;
    private Long oemId;
    private String signName;
}
