package com.qianjh.ryzen.framework.saas.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._Schemas;
import lombok.Data;

/**
 * @author QianJH
 */
@Data
@TableName(_Schemas.RYZEN + "tenant")
public class Tenant {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long oemId;

    private String symbol;

    private Boolean enabled;
}
