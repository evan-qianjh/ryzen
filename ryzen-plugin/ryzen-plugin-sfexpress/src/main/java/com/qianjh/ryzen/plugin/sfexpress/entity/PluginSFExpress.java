package com.qianjh.ryzen.plugin.sfexpress.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._TablePrefix;
import lombok.Data;

/**
 * 顺丰
 * @author QianJH
 */
@Data
@TableName(_TablePrefix.PLUGIN + "sfexpress")
public class PluginSFExpress {
    @TableId
    private Long id;
    private Long oemId;

    private String customerCode;
    private String checkWord;
    private String apiHost;
}
