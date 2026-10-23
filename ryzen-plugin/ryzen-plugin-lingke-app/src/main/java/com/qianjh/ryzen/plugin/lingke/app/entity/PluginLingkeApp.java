package com.qianjh.ryzen.plugin.lingke.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._TablePrefix;
import lombok.Data;

/**
 * 领克
 *
 * @author QianJH
 */
@Data
@TableName(_TablePrefix.PLUGIN + "lingke_app")
public class PluginLingkeApp {
    @TableId
    private Long id;
    private Long oemId;
    private Long lingkeId;

    private String name;

    private String appCode;
    private String appSecret;
}
