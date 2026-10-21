package com.qianjh.ryzen.plugin.meituan.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._TablePrefix;
import lombok.Data;

/**
 * @author QianJH
 */
@Data
@TableName(_TablePrefix.PLUGIN + "meituan_app")
public class PluginMeiTuanApp {
    @TableId
    private Long id;
    private Long oemId;
    private Long meituanId;

    private String name;
    private String developerId;
    private String signKey;
}
