package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
