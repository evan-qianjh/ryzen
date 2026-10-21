package com.qianjh.ryzen.plugin.meituan.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._TablePrefix;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author QianJH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TableName(_TablePrefix.PLUGIN + "meituan_app_access_token")
public class PluginMeiTuanAppAccessToken {
    @TableId
    private Long id;
    private Long oemId;
    private Long appId;

    private String scope;
    private String accessToken;
    private String refreshToken;

    /**
     * access_token有效时间【单位：s】
     */
    private Integer expireIn;
    private LocalDateTime expireTime;

    /**
     * 授权实体的唯一标识
     */
    private String opBizCode;
    /**
     * 授权实体名称（如果授权实体是门店则为门店名）
     */
    private String opBizName;

    /**
     * ID=58（服务零售）：每个门店独立拥有accessToken，作为门店唯一标识。门店授权（如独立运营）
     * ID=59（服务零售-客户）：同一客户（连锁总账号）下所有门店共享一个accessToken。连锁客户授权（统一管理）
     */
    private String businessId;
}
