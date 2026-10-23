package com.qianjh.ryzen.plugin.lingke.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._TablePrefix;
import lombok.Data;

/**
 * 领克
 * @author QianJH
 */
@Data
@TableName(_TablePrefix.PLUGIN + "lingke")
public class PluginLingke {
    @TableId
    private Long id;
    private Long oemId;
    private String name;
    private String apiHost;
}
