package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author QianJH
 */
@Data
@TableName(Namespace.PREFIX + "oem_tenant_domain")
public class OemTenantDomain {

    @TableId
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("domain")
    private String domain;

    @TableField("enabled")
    private Boolean enabled;
}
