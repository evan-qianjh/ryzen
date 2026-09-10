package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author QianJH
 */
@Data
@TableName(Namespace.PREFIX + "oem_domain")
public class OemDomain {

    @TableId
    private Long id;

    @TableField("oem_id")
    private Long oemId;

    @TableField("domain")
    private String domain;

    @TableField("enabled")
    private Boolean enabled;
}
