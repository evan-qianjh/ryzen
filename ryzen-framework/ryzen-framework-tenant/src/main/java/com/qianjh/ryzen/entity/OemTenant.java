package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author QianJH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TableName(Namespace.PREFIX + "oem_tenant")
public class OemTenant {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 启用的
     */
    @TableField(value = "enabled")
    private Boolean enabled;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 合作伙伴消息域名Host
     * e.g.: 'https://partner-api.xxx.com'
     */
    private String partnerNotifyHost;
}
