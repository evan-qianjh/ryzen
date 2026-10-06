package com.qianjh.ryzen.plugin.alipay.app.entity;

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
@TableName(_TablePrefix.PLUGIN + "alipay_app")
public class AlipayApp {
    @TableId
    private Long id;
    private Long oemId;

    private Long alipayId;

    private String appUk;
    private String name;
}
