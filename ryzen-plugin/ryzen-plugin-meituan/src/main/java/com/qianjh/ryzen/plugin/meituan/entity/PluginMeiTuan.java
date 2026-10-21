package com.qianjh.ryzen.plugin.meituan.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._TablePrefix;
import lombok.Data;

/**
 * @author QianJH
 */
@Data
@TableName(_TablePrefix.PLUGIN + "meituan")
public class PluginMeiTuan {
    @TableId
    private Long id;
    private Long oemId;
    private String name;
}
