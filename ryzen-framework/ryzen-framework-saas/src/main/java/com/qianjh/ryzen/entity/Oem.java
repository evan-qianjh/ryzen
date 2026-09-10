package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName(Namespace.PREFIX + "oem")
public class Oem {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String symbol;

    /**
     * 启用的
     */
    private Boolean enabled;

    /**
     * 合作伙伴消息域名Host
     * e.g.: 'https://partner-api.xxx.com'
     */
    private String partnerNotifyHost;
}
