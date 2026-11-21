package com.qianjh.ryzen.plugin.wecom.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._TablePrefix;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TableName(_TablePrefix.PLUGIN + "wecom_app")
public class PluginWecomApp {
    @TableId
    private Long id;
    private Long oemId;

    private Long wecomId;

    /**
     * 企业微信应用ID
     */
    private String appCode;
    /**
     * 企业微信应用密钥（对称加密）
     */
    private String appSecret;
}
