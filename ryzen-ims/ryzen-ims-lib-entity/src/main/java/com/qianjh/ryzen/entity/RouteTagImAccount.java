package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IM账户路由标签
 *
 * @author QianJH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schema.TABLE_PREFIX + "route_tag_im_account")
public class RouteTagImAccount {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long oemId;
    private Long tenantId;

    private Long routeTag;
    private Long imAccountId;
}
