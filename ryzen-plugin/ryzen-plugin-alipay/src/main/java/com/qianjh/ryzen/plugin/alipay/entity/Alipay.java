package com.qianjh.ryzen.plugin.alipay.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._TablePrefix;
import lombok.Data;

/**
 * @author QianJH
 */
@Data
@TableName(_TablePrefix.PLUGIN + "alipay")
public class Alipay {
    @TableId
    private Long id;
    private Long oemId;
    private String name;

    /**
     * 私钥，加密存储
     */
    private String privateKey;
    /**
     * 公钥
     */
    private String publicKey;
    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;
}
