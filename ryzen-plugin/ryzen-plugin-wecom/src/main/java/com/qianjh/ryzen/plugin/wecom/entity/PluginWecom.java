package com.qianjh.ryzen.plugin.wecom.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._TablePrefix;
import lombok.Data;

/**
 * @author QianJH
 */
@Data
@TableName(_TablePrefix.PLUGIN + "wecom")
public class PluginWecom {
    @TableId
    private Long id;
    private Long oemId;
    private String name;

    private String corpCode;

    // extra
}
